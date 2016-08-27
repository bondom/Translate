package ua.translate.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

import ua.translate.controller.support.ControllerHelper;
import ua.translate.model.Translator;
import ua.translate.model.Language;
import ua.translate.model.ad.Ad;
import ua.translate.model.ad.Document;
import ua.translate.model.ad.RespondedAd;
import ua.translate.model.viewbean.ChangeEmailBean;
import ua.translate.model.viewbean.ChangePasswordBean;
import ua.translate.service.AdService;
import ua.translate.service.TranslatorService;
import ua.translate.service.exception.DownloadFileAccessDenied;
import ua.translate.service.exception.DuplicateEmailException;
import ua.translate.service.exception.EmailIsConfirmedException;
import ua.translate.service.exception.InvalidConfirmationUrl;
import ua.translate.service.exception.InvalidPasswordException;
import ua.translate.service.exception.NonExistedAdException;
import ua.translate.service.exception.NumberExceedsException;
import ua.translate.service.exception.TranslatorDistraction;
import ua.translate.service.exception.WrongPageNumber;

@Controller
@RequestMapping("/translator")
public class TranslatorController extends UserController{

	@Autowired
	private TranslatorService translatorService;
	
	@Autowired
	private AdService adService;
	
	
	@Autowired
	private ControllerHelper controllerHelper;
	
	@Value("${webRootPath}")
	private String webRootPath;
	
	Logger logger = LoggerFactory.getLogger(TranslatorController.class);
	
	private static final int RESPONDED_ADS_ON_PAGE=3;
	private static final int MAX_NUMBER_OF_SENDED_RESPONDED_ADS = 3;
	
	/**
	 * Returns initial(welcome) page for translators
	 */
	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public ModelAndView index(){
		return new ModelAndView("/translator/index");
	}
	
	/**
	 * Returns translator's profile view
	 * 
	 * @param user
	 * @return translator's profile
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/profile", method = RequestMethod.GET)
	public ModelAndView profile(Principal user) throws UnsupportedEncodingException{
		Translator translatorFromDB = (Translator) translatorService.getUserByEmail(user.getName());
		ModelAndView model = new ModelAndView("/translator/profile");
		model.addObject("translator", translatorFromDB);
		model.addObject("image", controllerHelper.getAvaForRendering(translatorFromDB.getAvatar()));
		return model;
	}
	
	/**
	 * First check if user is authenticated, if is, redirects to profile page
	 * <p>Returns translator's login page, if {@code error!=null}
	 * adds to view appropriate error message and refreshes {@code targetUrl}
	 * <p> if {@code logout!=null} adds appropriate message
	 * @param error
	 * @param logout
	 * @param request
	 */
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public ModelAndView login(
				@RequestParam(value = "error", required = false) String error,
				@RequestParam(value = "logout", required = false) String logout, 
				HttpServletRequest request){
		if(!isCurrentAuthenticationAnonymous()){
			return new ModelAndView("redirect:/translator/profile");
		}
		if(error!=null){
			ModelAndView model = new ModelAndView("/translator/login");
			model.addObject("error","Invalid username or pass");
		
			//if login error, add targetUrl again
			String targetUrl = getRememberMeTargetUrlFromSession(request);
			System.out.println(targetUrl);
			if(StringUtils.hasText(targetUrl)){
				model.addObject("targetUrl", targetUrl);
				model.addObject("loginUpdate",true);
			}
			return model;
		}
		
		if(logout!=null){
			ModelAndView model = new ModelAndView("/translator/login");
			model.addObject("logout", "You have been logged out successfully");
			return model;
		}
		return new ModelAndView("/translator/login");
	}
	
	/**
	 * Returns translator's registration form, check on existing attributeName {@code translator}
	 * in flash attributes, if such doesn't exist, adds empty {@link Translator} object to view.
	 * Adds {@link Language#values()} to resulting view
	 * @param request
	 */
	@RequestMapping(value = "/registration",method = RequestMethod.GET)
	public ModelAndView registrationForm(HttpServletRequest request){
		ModelAndView model = new ModelAndView("/translator/registration");
		Map<String, ?> inputFlashMap = RequestContextUtils.getInputFlashMap(request);
	    if (inputFlashMap == null || !inputFlashMap.containsKey("translator")) {
			Translator translator = new Translator();
			model.addObject("translator", translator);
	    }
		model.addObject("languages",Language.values());
		return model;
	}
	
	/**
	 * Attempts to register translator, if some errors exist, redirects to
	 * registration view and adds flash attribute {@code translator} and {@code result}.
	 * <br>If user inputted all data right, chosen {@code stringLanguages} are converted to
	 * appropriate {@code Language}s, and added to {@code translator}
	 * <p> If user with such email is registered, he is redirected to initial page with added
	 * flash attribute {@code resultRegistration}. 
	 * <br> If user is successfully registered redirects to login page with added
	 * flash attribute {@code resultRegistration}. 
	 * @param translator
	 * @param result
	 * @param stringLanguages
	 * @param attr
	 */
	@RequestMapping(value = "/registrationConfirm",method = RequestMethod.POST)
	public ModelAndView registration(@Valid @ModelAttribute("translator") Translator translator,
									 BindingResult result,
									 @RequestParam(name = "selectedLanguages",required = true) List<String> stringLanguages,
									 RedirectAttributes attr){
		if(result.hasErrors()){
			attr.addFlashAttribute("org.springframework.validation.BindingResult.translator", 
					result);
			attr.addFlashAttribute("translator",translator);
			return new ModelAndView("redirect:/translator/registration");
		}
		
		Set<Language> enumLanguages= new LinkedHashSet<>();
		for(String language:stringLanguages){
			enumLanguages.add(Language.valueOf(language));
		}
		translator.setLanguages(enumLanguages);
		try {
			translatorService.registerUser(translator);
			attr.addFlashAttribute("resultRegistration", 
										"Success registration!");
			return new ModelAndView("redirect:/translator/login");
		} catch (DuplicateEmailException e) {
			attr.addFlashAttribute("resultRegistration",e.getMessage());
			return new ModelAndView("redirect:/translator/registration");
		}
	}
	
	/**
	 * Saves confirmation url in data storage, if email is confirmed, adds to result view {@code error} object
	 * @return {@code ModelAndView} with info about sending letter for confirmation
	 */
	@RequestMapping(value = "/email-confirm",method = RequestMethod.GET)
	public ModelAndView saveConfirmationEmail(Principal user){
		
		try {
			translatorService.saveConfirmationUrl(user.getName());
		} catch (EmailIsConfirmedException e) {
			ModelAndView model = new ModelAndView("/translator/sendedLetter");
			model.addObject("error",true);
			return model;
		}
		
		return new ModelAndView("/translator/sendedLetter");
	}
	
	/**
	 * Attempts to confirm translator's email, and returns page with info about confirmation
	 */
	@RequestMapping(value = "/confirmation",method = RequestMethod.GET)
	public ModelAndView emailConfirmation(@RequestParam("ecu") String confirmationUrl){
		
		String email="";
		try {
			email = translatorService.confirmUserEmail(confirmationUrl);
		} catch (InvalidConfirmationUrl e) {
			ModelAndView model = new ModelAndView("/translator/emailConfirmed");
			return model;
		}
		
		ModelAndView model = new ModelAndView("/translator/emailConfirmed");
		model.addObject("email",email);
		model.addObject("success", true);
		return model;
		
	}
	
	/**
	 * &nbsp&nbsp&nbsp&nbspReturns {@code ModelAndView} page for editing email, if
	 * translator is not authenticated via remember-me authentication
	 * <br>if is, return login form page for re-entering credentials
	 * <br>&nbsp&nbsp&nbsp&nbspIf in flash attributes doesn't exist {@code changeEmailBean} 
	 * creates one and adds to result view
	 */
	@RequestMapping(value = "/email",method = RequestMethod.GET)
	public ModelAndView editEmail(Principal user,HttpServletRequest request){
		if(isRememberMeAuthenticated()){
			ModelAndView model = new ModelAndView("/translator/login");
			model.addObject("loginUpdate",true);
			return model;
		}else{
			ModelAndView model = new ModelAndView("/translator/editEmail");
			Map<String, ?> inputFlashMap = RequestContextUtils.getInputFlashMap(request);
		    if (inputFlashMap == null || !inputFlashMap.containsKey("changeEmailBean")) {
		    	ChangeEmailBean changeEmailBean = new ChangeEmailBean();
				model.addObject("changeEmailBean",changeEmailBean);
		    }
			return model;
		}
	}
	
	/**
	 * &nbsp&nbsp&nbsp&nbspReturns {@code ModelAndView} page for editing password, if
	 * translator is not authenticated via remember-me authentication,
	 * if is, return login form page for re-entering credentials
	 * <br>&nbsp&nbsp&nbsp&nbspIf in flash attributes doesn't exist {@code changeEmailBean}
	 *  creates one and adds to result view
	 */
	@RequestMapping(value = "/password",method = RequestMethod.GET)
	public ModelAndView editPassword(Principal user,HttpServletRequest request){
		if(isRememberMeAuthenticated()){
			ModelAndView model = new ModelAndView("/translator/login");
			model.addObject("loginUpdate",true);
			return model;
		}else{
			ModelAndView model = new ModelAndView("/translator/editPassword");
			Map<String, ?> inputFlashMap = RequestContextUtils.getInputFlashMap(request);
		    if (inputFlashMap == null || !inputFlashMap.containsKey("changePasswordBean")) {
		    	ChangePasswordBean changePasswordBean = new ChangePasswordBean();
				model.addObject("changePasswordBean",changePasswordBean);
		    }
			return model;
		}
	}
	
	/**
	 * Returns {@link ModelAndView} translator's page for editing profile,
	 * Adds {@link Language#values()} to resulting view, and checks on existing {@code translator}
	 * object in flash attributes, if there is no such object, add one 
	 */
	@RequestMapping(value = "/edit",method = RequestMethod.GET)
	public ModelAndView editProfile(Principal user, HttpServletRequest request){
		
		ModelAndView model = new ModelAndView("/translator/edit");
		Map<String, ?> inputFlashMap = RequestContextUtils.getInputFlashMap(request);
	    if (inputFlashMap == null || !inputFlashMap.containsKey("translator")) {
	    	Translator translatorFromDb = translatorService.getTranslatorByEmail(user.getName());
			model.addObject("translator",translatorFromDb);
	    }
	    model.addObject("languages",Language.values());
		return model;
	}
	
	/**
	 * First checks how user is authenticated, if via remember-me authentication, returns page for
	 * reentering credentials.
	 * <br>Attempts to update translator's email, if email is registered already or password is invalid,
	 * returns page with appropriate error message
	 */
	@RequestMapping(value = "/saveEmail",method = RequestMethod.POST)
	public ModelAndView saveEmail(
								@Valid @ModelAttribute("changeEmailBean") ChangeEmailBean changeEmailBean,
								BindingResult changeEmailResult,
								Principal user,
								HttpServletRequest request,
								RedirectAttributes attr){
		
		if(isRememberMeAuthenticated()){
			//setRememberMeTargetUrlToSession(clientFromDB,request);
			ModelAndView model = new ModelAndView("/translator/login");
			model.addObject("loginUpdate",true);
			return model;
		}
		
		if(changeEmailResult.hasErrors()){
			attr.addFlashAttribute("org.springframework.validation.BindingResult.changeEmailBean", 
										changeEmailResult);
			attr.addFlashAttribute("changeEmailBean",changeEmailBean);
			return new ModelAndView("redirect:/translator/email");
		}
		try {
			translatorService.updateUserEmail(user.getName(), 
										  changeEmailBean.getNewEmail(), 
										  changeEmailBean.getCurrentPassword());
		}catch (InvalidPasswordException e) {
			attr.addFlashAttribute("invalidPassword", e.getMessage());
			return new ModelAndView("redirect:/translator/email");
		}catch (DuplicateEmailException e) {
			attr.addFlashAttribute("duplicateEmail", "This email is registered in system already");
			return new ModelAndView("redirect:/translator/email");
		}
		
		refreshUsername(changeEmailBean.getNewEmail());
		attr.addFlashAttribute("emailSaved", "Your email is saved successfully");
		return new ModelAndView("redirect:/translator/profile");
	}
	/**
	 * First checks how user is authenticated, if via remember-me authentication, returns page for
	 * reentering credentials.
	 * <br>Attempts to update translator's password, if password is invalid,
	 * returns page with appropriate error message
	 * @throws UnsupportedEncodingException 
	 */
	@RequestMapping(value = "/savePassword",method = RequestMethod.POST)
	public ModelAndView savePassword(
			@Valid @ModelAttribute("changePasswordBean") ChangePasswordBean changePasswordBean,
			BindingResult changePasswordResult,
			Principal user,
			HttpServletRequest request,
			RedirectAttributes attr){
		
		if(isRememberMeAuthenticated()){
			//setRememberMeTargetUrlToSession(clientFromDB,request);
			ModelAndView model = new ModelAndView("/translator/login");
			model.addObject("loginUpdate",true);
			return model;
		}
		
		if(changePasswordResult.hasErrors()){
			attr.addFlashAttribute("org.springframework.validation.BindingResult.changePasswordBean", 
					changePasswordResult);
			attr.addFlashAttribute("changePasswordBean",changePasswordBean);
			return new ModelAndView("redirect:/translator/password");
		}
		try {
			translatorService.updateUserPassword(user.getName(), 
											 changePasswordBean.getOldPassword(), 
											 changePasswordBean.getNewPassword());
		} catch (InvalidPasswordException e) {
			attr.addFlashAttribute("wrongOldPassword",e.getMessage());
			return new ModelAndView("redirect:/translator/password");
		}
		
		attr.addFlashAttribute("passSaved","Your password is saved successfully");
		return new ModelAndView("redirect:/translator/profile");
	}
	
	/**
	 * <p>If no errors exist updates the user's profile, adds flash attribute {@code profileSaved} and redirects to {@link #profile(Principal)}
	 * @param editedTranslator - {@code Translator} object with changes made by translator
	 * @param result - {@code BindingResult} object for checking errors
	 * @param user - {@code Principal} object for retrieving {@code Translator} from db
	 */
	@RequestMapping(value = "/saveEdits",method = RequestMethod.POST)
	public ModelAndView saveProfileEdits(
							@Valid @ModelAttribute("translator") Translator editedTranslator,
							BindingResult result,
							@RequestParam(name = "selectedLanguages",required = true) List<String> stringLanguages,
							Principal user,
							RedirectAttributes attr){
		
		if(result.hasErrors()){
			attr.addFlashAttribute("org.springframework.validation.BindingResult.translator", 
					result);
			attr.addFlashAttribute("translator", editedTranslator);
			return new ModelAndView("redirect:/translator/edit");
		}
		
		
		Set<Language> enumLanguages= new LinkedHashSet<>();
		for(String language:stringLanguages){
			enumLanguages.add(Language.valueOf(language));
		}
		editedTranslator.setLanguages(enumLanguages);
		
		translatorService.updateUserProfile(user.getName(), editedTranslator);
		
		attr.addFlashAttribute("profileSaved","Your profile is saved successfully");
		return new ModelAndView("redirect:/translator/profile");
	}
	
	/**
	 * Saves new avatar in DB
	 * 
	 * @param user - {Principal.class} object with name equals to {User.class} object's login
	 * @param file - file chosen to be a avatar
	 * @return user's profile page or page for editing profile if no file is chosen
	 * or file is not image
	 */
	@RequestMapping(value = "/saveAvatar",method = RequestMethod.POST)
	public ModelAndView saveAvatar(Principal user, 
						@RequestParam("file") MultipartFile file,
						RedirectAttributes attr){
		if(!file.isEmpty()){
			try {
				String contentType = file.getContentType();
				if(contentType.startsWith("image/")){
					byte[] avatar = file.getBytes();
					translatorService.updateAvatar(user.getName(), avatar);
				}else{
					attr.addFlashAttribute("error", "Please choose image");
				}
				return new ModelAndView("redirect:/translator/profile");
			} catch (IOException e) {
				attr.addFlashAttribute("error", "Some problem with your avatar, please repeat action");
				return new ModelAndView("redirect:/translator/profile");
			}
		}
		ModelAndView model = new ModelAndView("redirect:/translator/profile");
		return model;
		
	}
	
	/**
	 * Attempts to save new RespondedAd for translator,
	 * <br>if Ad with id={@code adId} doesn't exist, or translator has too much ads,
	 * or he has ACCEPTED ad -- redirects to appropriate page with 
	 * adding appropriate flash attributes 
	 * @param adId
	 * @param user
	 * @param attr
	 */
	@RequestMapping(value="/response",method = RequestMethod.POST)
	public ModelAndView response(@RequestParam(name="id",required = false) long adId,
								Principal user,RedirectAttributes attr){
		logger.info("ad.id={}",adId);
		try {
			translatorService.saveRespondedAd(
					user.getName(),adId,MAX_NUMBER_OF_SENDED_RESPONDED_ADS);
			return new ModelAndView("redirect:/translator/successResponding");
		} catch (NonExistedAdException e) {
			attr.addFlashAttribute("errorUrl", webRootPath+"/ads/"+adId);
			return new ModelAndView("redirect:/exception/invalidAdId");
		} catch (NumberExceedsException e) {
			return new ModelAndView("redirect:/translator/error/exceeding");
		} catch (TranslatorDistraction e) {
			attr.addFlashAttribute("error","First you must end this translate!");
			return new ModelAndView("redirect:/translator/currentOrder");
		}
	}
	
	
	/**
	 * Creates output stream with text file of {@link Ad} object with id={@code adId}
	 * <br>If {@code ad} with {@code adId} doesn't exist, or user don't have access to download it,
	 * returns 404 error page
	 * @param adId
	 * @param response
	 * @param user
	 */
	@RequestMapping("/download/{adId}")
	public ModelAndView downloadFile(@PathVariable("adId")
			Long adId, HttpServletResponse response,Principal user) {
		if(user==null){
			return new ModelAndView("/exception/404");
		}
		Document textFile;
		try {
			textFile = adService.getDocumentForDownloading(adId, user.getName());
			try {
				response.setHeader("Content-Disposition", "inline;filename=\"" +textFile.getFileName()+ "\"");
				OutputStream out = response.getOutputStream();
				response.setContentType(textFile.getContentType());
				out.write(textFile.getFile());
				out.flush();
				out.close();
			
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (NonExistedAdException eq) {
			logger.debug("User want to load file of ad, with id={}, which doesn't exist",adId);
			return new ModelAndView("/exception/404");
		} catch(DownloadFileAccessDenied  e1){
			logger.debug("User with email = {} hasn't access to load file of ad",user.getName());
			return new ModelAndView("/exception/404");
		}
		
		return null;
	}

	/**
	 * Returns page with message about successfull sending response to the client 
	 */
	@RequestMapping(value="/successResponding",method=RequestMethod.GET)
	public ModelAndView successResponding(){
		return new ModelAndView("/translator/successResponding");
	}
	
	/**
	 * Returns page with message, that some id of ad doesn't exist
	 * 
	 * <p><b>NOTE:</b>While redirecting to this method in flash attributes must exists
	 * flash attribute with name={@code errorUrl}, otherwise this method redirects to
	 * translator's profile
	 * 	
	 */
	@RequestMapping(value="/error/invalidAdId",method=RequestMethod.GET)
	public ModelAndView invalidAdId(HttpServletRequest request){
		Map<String, ?> inputFlashMap = RequestContextUtils.getInputFlashMap(request);
	    if (inputFlashMap == null || !inputFlashMap.containsKey("errorUrl")) {
	    	return new ModelAndView("redirect:/translator/profile");
	    }
	    ModelAndView model = new ModelAndView("/exception/invalidAdId");
		return model;
	}
	
	/**
	 * Returns page with message about having maximum number of sended ads 
	 */
	@RequestMapping(value="/error/exceeding",method=RequestMethod.GET)
	public ModelAndView exceeding(){
		return new ModelAndView("/translator/error/exceeding");
	}
	
	/**
	 * Returns {@code ModelAndView} page with {@link RespondedAd}s of this translator
	 * in order from latest to earliest
	 * @param user - {@code Principal} object for retrieving translator from db
	 * @param page - number of page
	 */
	@RequestMapping(value = "/responses",method = RequestMethod.GET)
	public ModelAndView respondedAds(Principal user,
									 @RequestParam(name="page",defaultValue="1",required = false) int page){
		
		Set<RespondedAd> respondedAds = null;
		try {
			respondedAds = translatorService
					.getRespondedAds(user.getName(),page,RESPONDED_ADS_ON_PAGE);
		} catch (WrongPageNumber e) {
			try {
				respondedAds = translatorService
						.getRespondedAds(user.getName(),1,RESPONDED_ADS_ON_PAGE);
			} catch (WrongPageNumber unused) {}
		}
		
		long nunmberOfPages = translatorService
				.getNumberOfPagesForRespondedAds(user.getName(), RESPONDED_ADS_ON_PAGE);
		ModelAndView model = new ModelAndView("/translator/responses");
		model.addObject("respondedAds", respondedAds);
		model.addObject("numberOfPages",nunmberOfPages);
		return model;
	}
		
	/**
	 * Returns {@code ModelAndView} page with ACCEPTED {@link RespondedAd} of user
	 * @param user - {@code Principal} object for retrieving translator from db
	 */
	@RequestMapping(value = "/currentOrder",method = RequestMethod.GET)
	public ModelAndView currentOrder(Principal user){
		RespondedAd respondedAd = translatorService.getCurrentOrder(user.getName());
		ModelAndView model = new ModelAndView("/translator/currentOrder");
		model.addObject("respondedAd",respondedAd);
		return model;
	}
	
	/**
	 * Marks {@link Ad} with {@code adId} as NOTCHECKED.
	 * Redirects to  {@link #currentOrder(Principal)} 
	 * @param user - {@code Principal} object for retrieving translator from db
	 */
	@RequestMapping(value = "/finish",method = RequestMethod.POST)
	public ModelAndView finish(Principal user,
									 @RequestParam(name="id",required = true) long adId){
		logger.debug("adId={},translator emai={}",adId,user.getName());
		boolean marked = translatorService.markAsNotChecked(user.getName(), adId);
		return new ModelAndView("redirect:/translator/currentOrder");
	}
	
	
	
	
}
package ua.translate.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.time.Month;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

import ua.translate.controller.editor.CommentTextEditor;
import ua.translate.controller.support.ControllerHelper;
import ua.translate.model.Client;
import ua.translate.model.Comment;
import ua.translate.model.Language;
import ua.translate.model.ad.Ad;
import ua.translate.model.ad.Currency;
import ua.translate.model.ad.Document;
import ua.translate.model.ad.RespondedAd;
import ua.translate.model.ad.TranslateType;
import ua.translate.model.viewbean.ChangeEmailBean;
import ua.translate.model.viewbean.ChangePasswordBean;
import ua.translate.model.viewbean.ChooseTranslateTypeBean;
import ua.translate.service.AdService;
import ua.translate.service.ClientService;
import ua.translate.service.CommentService;
import ua.translate.service.RespondedAdService;
import ua.translate.service.exception.DuplicateEmailException;
import ua.translate.service.exception.EmailIsConfirmedException;
import ua.translate.service.exception.IllegalActionForAd;
import ua.translate.service.exception.IllegalActionForRejectedAd;
import ua.translate.service.exception.InvalidConfirmationUrl;
import ua.translate.service.exception.NonExistedRespondedAdException;
import ua.translate.service.exception.TooManyAds;
import ua.translate.service.exception.TooManyRefreshings;
import ua.translate.service.exception.TranslatorDistraction;
import ua.translate.service.exception.IllegalActionForAcceptedAd;
import ua.translate.service.exception.WrongPageNumber;
import ua.translate.service.exception.InvalidPasswordException;
import ua.translate.service.exception.NonExistedAdException;

@Controller
@RequestMapping("/client")
public class ClientController extends UserController{

	@Autowired
	private ClientService clientService;
	
	@Autowired
	private AdService adService;
	
	@Autowired
	private RespondedAdService respondedAdService;
	
	@Autowired
	private CommentService commentService;
	
	@Autowired
	ControllerHelper controllerHelper;
	
	Logger logger = LoggerFactory.getLogger(ClientController.class);
	
	@Value("${webRootPath}")
	private String webRootPath;
	
	private static final int RESPONDED_ADS_ON_PAGE=3;
	
	/**
	 * This variable contains minimum number of hours, that must elapse from
	 * last refreshing for allowing next one {@link Ad}
	 */
	private static final long HOURS_BETWEEN_REFRESHING = 12;
	
	/**
	 * This variable contains maximum number of {@link Ad}s, that can have
	 * one client
	 */
	private static final long MAX_NUMBER_OF_ADS = 3;
	
	private static final String[] ALLOWED_CONTENT_TYPES_FOR_TEXT=
		{"application/pdf","application/msword","application/vnd.openxmlformats-officedocument.wordprocessingml.document"};
	
	
	
	@InitBinder
	public void initBinder(WebDataBinder dataBinder){
		dataBinder.registerCustomEditor(String.class, 
										"text", 
										new CommentTextEditor());
	}
	
	/**
	 * Returns {@link ModelAndView} client's profile
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/profile", method = RequestMethod.GET)
	public ModelAndView profile(Principal user) throws UnsupportedEncodingException{
		Client clientFromDB = clientService.getClientByEmail(user.getName());
		ModelAndView model = new ModelAndView("/client/profile");
		model.addObject("client", clientFromDB);
		model.addObject("image", controllerHelper.getAvaForRendering(clientFromDB.getAvatar()));
		return model;
	}
	
	/**
	 * Returns {@link ModelAndView} client's login form
	 * 
	 * <p>If client is authenticated, he is redirected to {@link #profile(Principal)}.
	 * 
	 * <p>If <tt>error</tt> exists and session has targetUrl parameter, 
	 * refreshes value of targetUrl in returned {@code ModelAndView} object
	 * @see #isCurrentAuthenticationAnonymous()	
	 * @see #getRememberMeTargetUrlFromSession(HttpServletRequest)
	 */
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public ModelAndView loginForm(
				@RequestParam(value = "error", required = false) String error,
				HttpServletRequest request){
		if(!isCurrentAuthenticationAnonymous()){
			return new ModelAndView("redirect:/client/profile");
		}
		ModelAndView model = new ModelAndView("/client/login");
		if(error!=null){
			String targetUrl = getRememberMeTargetUrlFromSession(request);
			if(StringUtils.hasText(targetUrl)){	
				model.addObject("targetUrl", targetUrl);
				model.addObject("loginUpdate",true);
			}
		}
		return model;
	}
	
	/**
	 * Returns {@link ModelAndView} client's registration form
	 * with new {@link Client} object for binding fields on page
	 */
	@RequestMapping(value = "/registration",method = RequestMethod.GET)
	public ModelAndView registrationForm(HttpServletRequest request){
		ModelAndView model = new ModelAndView("/client/registration");
		Map<String, ?> inputFlashMap = RequestContextUtils.getInputFlashMap(request);
	    if (inputFlashMap == null || !inputFlashMap.containsKey("client")) {
	    	Client client = new Client();
			model.addObject("client", client);
	    }
		return model;
	}
	
	/**
	 * Checks {@code BindingResult} on errors, if such exists returns {@link #registrationForm()}
	 * <p>Attempts to register {@code Client}, if email is registered already returns {@code registrationForm()} with
	 * user-friendly message
	 * @param client - {@link Client}, retrieved from registration form and valided by Hibernate Validator
	 */
	@RequestMapping(value = "/registrationConfirm",method = RequestMethod.POST)
	public ModelAndView registration(@Valid @ModelAttribute("client") Client client,
								BindingResult result,RedirectAttributes attr){
		if(result.hasErrors()){
			attr.addFlashAttribute("org.springframework.validation.BindingResult.client", 
					result);
			attr.addFlashAttribute("client",client);
			return new ModelAndView("redirect:/client/registration");
		}
		try{
			clientService.registerUser(client);
		}catch (DuplicateEmailException e) {
			attr.addFlashAttribute("error","This email is registered in system already");
			return new ModelAndView("redirect:/client/registration");
		}
		attr.addFlashAttribute("msg","You successfully registered!");
		return new ModelAndView("redirect:/client/login");
	}
	
	/**
	 * Saves confirmation url in data storage,
	 * returns {@code ModelAndView} with info about sending letter for confirmation
	 */
	@RequestMapping(value = "/email-confirm",method = RequestMethod.GET)
	public ModelAndView saveConfirmationEmail(Principal user){
		
		try {
			clientService.saveConfirmationUrl(user.getName());
		} catch (EmailIsConfirmedException e) {
			ModelAndView model = new ModelAndView("/client/sendedLetter");
			model.addObject("error",true);
			return model;
		}
		
		ModelAndView model = new ModelAndView("/client/sendedLetter");
		return model;
	}
	
	/**
	 * Attempts to confirm client's email, and returns page with info about confirmation
	 */
	@RequestMapping(value = "/confirmation",method = RequestMethod.GET)
	public ModelAndView emailConfirmation(@RequestParam("ecu") String confirmationUrl){
		
		String email="";
		try {
			email = clientService.confirmUserEmail(confirmationUrl);
		} catch (InvalidConfirmationUrl e) {
			ModelAndView model = new ModelAndView("/client/emailConfirmed");
			return model;
		}
		
		ModelAndView model = new ModelAndView("/client/emailConfirmed");
		model.addObject("email",email);
		model.addObject("success", true);
		return model;
		
	}
	
	/**
	 * Returns {@code ModelAndView} page for editing email, if
	 * client is not authenticated via remember-me authentication
	 * <p>if is, return login form page for re-entering credentials
	 */
	@RequestMapping(value = "/email",method = RequestMethod.GET)
	public ModelAndView editEmail(Principal user,HttpServletRequest request){
		if(isRememberMeAuthenticated()){
			ModelAndView model = new ModelAndView("/client/login");
			model.addObject("loginUpdate",true);
			return model;
		}else{
			ModelAndView model = new ModelAndView("/client/editEmail");
			Map<String, ?> inputFlashMap = RequestContextUtils.getInputFlashMap(request);
		    if (inputFlashMap == null || !inputFlashMap.containsKey("changeEmailBean")) {
		    	ChangeEmailBean changeEmailBean = new ChangeEmailBean();
				model.addObject("changeEmailBean",changeEmailBean);
		    }
			return model;
		}
	}
	
	/**
	 * Returns {@code ModelAndView} page for editing password, if
	 * client is not authenticated via remember-me authentication
	 * <p>if is, return login form page for re-entering credentials
	 */
	@RequestMapping(value = "/password",method = RequestMethod.GET)
	public ModelAndView editPassword(Principal user,HttpServletRequest request){
		if(isRememberMeAuthenticated()){
			ModelAndView model = new ModelAndView("/client/login");
			model.addObject("loginUpdate",true);
			return model;
		}else{
			ModelAndView model = new ModelAndView("/client/editPassword");
			Map<String, ?> inputFlashMap = RequestContextUtils.getInputFlashMap(request);
		    if (inputFlashMap == null || !inputFlashMap.containsKey("changePasswordBean")) {
		    	ChangePasswordBean changePasswordBean = new ChangePasswordBean();
				model.addObject("changePasswordBean",changePasswordBean);
		    }
			return model;
		}
	}
	
	/**
	 * Returns {@link ModelAndView} client's page for editing profile 
	 */
	@RequestMapping(value = "/edit",method = RequestMethod.GET)
	public ModelAndView editProfile(Principal user, HttpServletRequest request){
		
		ModelAndView model = new ModelAndView("/client/edit");
		Map<String, ?> inputFlashMap = RequestContextUtils.getInputFlashMap(request);
	    if (inputFlashMap == null || !inputFlashMap.containsKey("client")) {
	    	Client clientFromDB = clientService.getClientByEmail(user.getName());
			model.addObject("client",clientFromDB);
	    }
		return model;
	}
	
	/**
	 * Attempts to update client's email, if email is registered already or password is invalid,
	 * returns page with appropriate error messages
	 * @throws UnsupportedEncodingException 
	 */
	@RequestMapping(value = "/saveEmail",method = RequestMethod.POST)
	public ModelAndView saveEmail(
								@Valid @ModelAttribute("changeEmailBean") ChangeEmailBean changeEmailBean,
								BindingResult changeEmailResult,
								Principal user,
								HttpServletRequest request,
								RedirectAttributes attr) throws UnsupportedEncodingException{
		
		if(isRememberMeAuthenticated()){
			//setRememberMeTargetUrlToSession(clientFromDB,request);
			ModelAndView model = new ModelAndView("/client/login");
			model.addObject("loginUpdate",true);
			return model;
		}
		
		if(changeEmailResult.hasErrors()){
			attr.addFlashAttribute("org.springframework.validation.BindingResult.changeEmailBean", 
										changeEmailResult);
			attr.addFlashAttribute("changeEmailBean",changeEmailBean);
			return new ModelAndView("redirect:/client/email");
		}
		try {
			clientService.updateUserEmail(user.getName(), 
										  changeEmailBean.getNewEmail(), 
										  changeEmailBean.getCurrentPassword());
		}catch (InvalidPasswordException e) {
			attr.addFlashAttribute("invalidPassword", e.getMessage());
			return new ModelAndView("redirect:/client/email");
		}catch (DuplicateEmailException e) {
			attr.addFlashAttribute("duplicateEmail", "This email is registered in system already");
			return new ModelAndView("redirect:/client/email");
		}
		
		refreshUsername(changeEmailBean.getNewEmail());
		attr.addFlashAttribute("emailSaved", "Your email is saved successfully");
		return new ModelAndView("redirect:/client/profile");
	}
	/**
     * Attempts to update client's password, if password is invalid,
	 * returns page with appropriate error message
	 * @throws UnsupportedEncodingException 
	 */
	@RequestMapping(value = "/savePassword",method = RequestMethod.POST)
	public ModelAndView savePassword(
			@Valid @ModelAttribute("changePasswordBean") ChangePasswordBean changePasswordBean,
			BindingResult changePasswordResult,
			Principal user,
			HttpServletRequest request,
			RedirectAttributes attr) throws UnsupportedEncodingException{
		
		if(isRememberMeAuthenticated()){
			//setRememberMeTargetUrlToSession(clientFromDB,request);
			ModelAndView model = new ModelAndView("/client/login");
			model.addObject("loginUpdate",true);
			return model;
		}
		
		if(changePasswordResult.hasErrors()){
			attr.addFlashAttribute("org.springframework.validation.BindingResult.changePasswordBean", 
					changePasswordResult);
			attr.addFlashAttribute("changePasswordBean",changePasswordBean);
			return new ModelAndView("redirect:/client/password");
		}
		try {
			clientService.updateUserPassword(user.getName(), 
											 changePasswordBean.getOldPassword(), 
											 changePasswordBean.getNewPassword());
		} catch (InvalidPasswordException e) {
			attr.addFlashAttribute("wrongOldPassword",e.getMessage());
			return new ModelAndView("redirect:/client/password");
		}
		
		attr.addFlashAttribute("passSaved","Your password is saved successfully");
		return new ModelAndView("redirect:/client/profile");
	}
	
	/**
	 * <p>If no errors exist updates the user's profile and redirects to {@link #profile(Principal)}
	 * @param editedClient - {@code Client} object with changes made by client
	 * @param result - {@code BindingResult} object for checking errors
	 * @param user - {@code Principal} object for retrieving {@code Client} from db
	 * @throws UnsupportedEncodingException 
	 */
	@RequestMapping(value = "/saveEdits",method = RequestMethod.POST)
	public ModelAndView saveProfileEdits(
							@Valid @ModelAttribute("client") Client editedClient,
							BindingResult result,
							Principal user,
							RedirectAttributes attr) throws UnsupportedEncodingException{
		
		if(result.hasErrors()){
			attr.addFlashAttribute("org.springframework.validation.BindingResult.client", 
					result);
			attr.addFlashAttribute("client", editedClient);
			return new ModelAndView("redirect:/client/edit");
		}
		
		clientService.updateUserProfile(user.getName(), editedClient);
		
		attr.addFlashAttribute("profileSaved","Your profile is saved successfully");
		return new ModelAndView("redirect:/client/profile");
	}
	
	/**
	 * Saves new avatar in DB if type is acceptable and {@code file} is not empty
	 * 
	 * @param user - {@code Principal} object for retrieving {@code Client} from db
	 * @param file - file chosen to be a avatar
	 * @return {@code ModelAndView} user's profile page if saving was successfull, otherwise page for editing profile 
	 * @throws UnsupportedEncodingException 
	 */
	@RequestMapping(value = "/saveAvatar",method = RequestMethod.POST)
	public ModelAndView saveAvatar(Principal user, 
						@RequestParam("file") MultipartFile file,
						RedirectAttributes attr) throws UnsupportedEncodingException{
		if(!file.isEmpty()){
			try {
				String contentType = file.getContentType();
				if(contentType.startsWith("image/")){
					byte[] avatar = file.getBytes();
					clientService.updateAvatar(user.getName(), avatar);
				}else{
					attr.addFlashAttribute("error", "Please choose image");
				}
				ModelAndView model = new ModelAndView("redirect:/client/profile");
				return model;
			} catch (IOException e) {
				attr.addFlashAttribute("error", "Some problem with your avatar, please repeat action");
				return new ModelAndView("redirect:/client/profile");
			}
		}
		ModelAndView model = new ModelAndView("redirect:/client/profile");
		return model;
	}
	/**
	 * Returns {@code ModelAndView} page with all client's ads
	 * @param user - {@code Principal} object for retrieving client from db
	 */
	@RequestMapping(value = "/ads",method = RequestMethod.GET)
	public ModelAndView ads(Principal user){
		Set<Ad> ads = clientService.getAds(user.getName());
		ModelAndView model = new ModelAndView("/client/ads");
		if(!ads.isEmpty()){
			model.addObject("ads", ads);
		}
		return model;
	}
	
	/**
	 * Returns {@code ModelAndView} page for creating advertisement.
	 * Adds {@link Ad} object for binding and adds flag which shows that
	 * client wants to create advertisement
	 * <p>Adds {@code Map} objects for rendering available languages,currencies and translate types
	 * @param user - {@code Principal} object for retrieving client from db
	 * @see #addMapsToAdView(ModelAndView)
	 */
	@RequestMapping(value = "/adbuilder",method = RequestMethod.GET)
	public ModelAndView adbuilder(@RequestParam(name="page",required=false,defaultValue="1")
																				int page,
								  @ModelAttribute("cTTBean") ChooseTranslateTypeBean 
																chosenTranslateTypeBean,
								  BindingResult result,
								  Principal user,HttpServletRequest request){
		if(result.hasErrors()){
			return new ModelAndView("redirect:/client/adbuilder");
		}
		if(page==1){
			ModelAndView model = new ModelAndView("/client/adbuilderstep1");
			model.addObject("translateTypes", getTranslateTypesForSelect());
			ChooseTranslateTypeBean chooseTranslateType = new ChooseTranslateTypeBean();
			model.addObject("cTTBean",chooseTranslateType);
			return model;
		}
		
		final Map<String, ?> inputFlashMap = RequestContextUtils.getInputFlashMap(request);
	    if (inputFlashMap != null && inputFlashMap.containsKey("translateType") &&
	    		inputFlashMap.containsKey("ad")) {
	    	//if saving of ad failed
	    	//get translate type and return appropriate
	    	//page for editing entered data
	    	TranslateType translateType = 
	    				(TranslateType)inputFlashMap.get("translateType");
	    	if(translateType.equals(TranslateType.ORAL)){
	    		ModelAndView model = new ModelAndView("/client/adbuilderoral");
	    		addMapsToAdView(model);
	    		model.addObject("createAd", true);
	    		return model;
	    	}else if(translateType.equals(TranslateType.WRITTEN)){
	    		ModelAndView model = new ModelAndView("/client/adbuilderwritten");
	    		addMapsToAdView(model);
	    		model.addObject("createAd", true);
	    		return model;
	    	}else{
	    		//unacceptable situation
	    		logger.error("'translateType' variable exists in flash attributes,"
	    				+ " but doesn't equal to ORAL or WRITTEN");
	    	}
	    }
	    
		
		if(page==2){
			//if it is first call of method(without redirecting)
			//with instantiated chosenTranslateTypeBean
			ModelAndView model = new ModelAndView();
			model.addObject("createAd", true);
		    //adding maps for rendering variants languages or currencies
			addMapsToAdView(model);
			if(TranslateType.ORAL.equals(chosenTranslateTypeBean.getTranslateType())){
		    	Ad ad = new Ad();
		    	ad.setTranslateType(TranslateType.ORAL);
				model.addObject("ad",ad);
			    model.setViewName("/client/adbuilderoral");
				return model;
			}else if(TranslateType.WRITTEN.equals(chosenTranslateTypeBean.getTranslateType())){
			    Ad ad = new Ad();
			    ad.setTranslateType(TranslateType.WRITTEN);
				model.addObject("ad",ad);
			    model.setViewName("/client/adbuilderwritten");
				return model;				
			}else{
				//redirecting to adbuilder without page requestparam
				return new ModelAndView("redirect:/client/adbuilder");
			}
		}
		
		return new ModelAndView("redirect:/client/adbuilder");
	}
	
	/**
	 * If some errors exist, redirects to {@link #adbuilder(int, ChooseTranslateTypeBean, BindingResult, Principal, HttpServletRequest)}
	 * otherwise saves {@code Ad} of ORAL type in db, returns {@code ModelAndView} page
	 * @param ad - {@code Ad} object with inputted parameters
	 * @param result - {@code BindingResult} for checking errors
	 * @param user - {@code Principal} object for retrieving client from db
	 */
	@RequestMapping(value = "/saveOralAd", method = RequestMethod.POST)
	public ModelAndView saveOralAd(@Valid @ModelAttribute("ad") Ad ad,
								BindingResult result,
								Principal user,
								RedirectAttributes attr){
		if(result.hasErrors()){
			attr.addFlashAttribute("org.springframework.validation.BindingResult.ad", 
					result);
			attr.addFlashAttribute("translateType",ad.getTranslateType());
			attr.addFlashAttribute("ad",ad);
			return new ModelAndView("redirect:/client/adbuilder?page=2&translateType="+ad.getTranslateType());
		}
		Long adId;
		try {
			adId = adService.saveAd(ad, user.getName(),MAX_NUMBER_OF_ADS);
			attr.addFlashAttribute("adUrl",webRootPath+"/ads/"+adId);
			attr.addFlashAttribute("adId",adId);
			return new ModelAndView("redirect:/client/success");
		} catch (TooManyAds e) {
			attr.addFlashAttribute("error","Simultaneously you can have only 3 advertisements");
			return new ModelAndView("redirect:/client/ads");
		}

		
	}
	
	/**
	 * If some errors exist, redirects to {@link #adbuilder(int, ChooseTranslateTypeBean, BindingResult, Principal, HttpServletRequest)}
	 * otherwise saves {@code Ad} of WRITTEN type in db, returns {@code ModelAndView} page
	 * @param ad - {@code Ad} object with inputted parameters
	 * @param result - {@code BindingResult} for checking errors
	 * @param user - {@code Principal} object for retrieving client from db
	 */
	@RequestMapping(value = "/saveWrittenAd", method = RequestMethod.POST)
	public ModelAndView saveWrittenAd(@Valid @ModelAttribute("ad") Ad ad,
								BindingResult result,
								@RequestParam(name="multipartFile",required=false)
										MultipartFile file,
								Principal user,
								RedirectAttributes attr){
		if(result.hasErrors()){
			attr.addFlashAttribute("org.springframework.validation.BindingResult.ad", 
					result);
			attr.addFlashAttribute("translateType",ad.getTranslateType());
			attr.addFlashAttribute("ad",ad);
			attr.addFlashAttribute("multipartFile",file);
			return new ModelAndView("redirect:/client/adbuilder?page=2&translateType="+ad.getTranslateType());
		}
		if(!file.isEmpty()){
			if(!isfileContentTypeAllowed(file)){
				attr.addFlashAttribute("error", "Please choose file with .pdf or .doc(.docx) extension");
				attr.addFlashAttribute("translateType",ad.getTranslateType());
				attr.addFlashAttribute("ad",ad);
				return new ModelAndView("redirect:/client/adbuilder?page=2&translateType="+TranslateType.WRITTEN);
			}
			try {
				byte[] fileWithText = file.getBytes();
				ad.setDocument(
						new Document(ad,fileWithText,file.getOriginalFilename(),file.getContentType()));
			} catch (IOException e) {
				attr.addFlashAttribute("error", "Some problem with your file, please repeat action");
				attr.addFlashAttribute("translateType",ad.getTranslateType());
				attr.addFlashAttribute("ad",ad);
				return new ModelAndView("redirect:/client/adbuilder?page=2&translateType="+TranslateType.WRITTEN);
			}
		}else{
			attr.addFlashAttribute("error", "Please choose file");
			attr.addFlashAttribute("ad",ad);
			attr.addFlashAttribute("translateType",ad.getTranslateType());
			return new ModelAndView("redirect:/client/adbuilder?page=2&translateType="+TranslateType.WRITTEN);
		}
		Long adId;
		try {
			adId = adService.saveAd(ad, user.getName(),MAX_NUMBER_OF_ADS);
			attr.addFlashAttribute("adUrl",webRootPath+"/ads/"+adId);
			attr.addFlashAttribute("adId",adId);
			return new ModelAndView("redirect:/client/success");
		} catch (TooManyAds e) {
			attr.addFlashAttribute("error","Simultaneously you can have only 3 advertisements");
			return new ModelAndView("redirect:/client/ads");
		}

		
	}
	
	
	
	/**
	 * Deletes {@code Ad}, if such exists and it status is not accepted, 
	 * and redirects to {@link #ads(Principal)}
	 * @param adId - id of {@code Ad} object
	 */
	@RequestMapping(value = "/ads/delete", method = RequestMethod.POST)
	public ModelAndView deleteAd(@RequestParam("adId") long adId,Principal user,
								  RedirectAttributes attr){
		ModelAndView model = new ModelAndView("redirect:/client/ads");
		
		try {
			adService.deleteById(adId);
			attr.addFlashAttribute("msg", "Adversiment successfully deleted");
		} catch (NonExistedAdException e) {
			attr.addFlashAttribute("error", "You can't delete non existed ad");
		} catch (IllegalActionForAcceptedAd e) {
			attr.addFlashAttribute("error", "Sorry, but you can't delete advertisement, "
					+ "which has Accepted status");
		}
		
		return model;
	}
	
	/**
	 * Refreshes {@link Ad#getPublicationDateTime()}, if such exists and it status is not accepted, 
	 * and redirects to {@link #ads(Principal)}
	 * @param adId - id of {@code Ad} object
	 */
	@RequestMapping(value = "/ads/refresh", method = RequestMethod.POST)
	public ModelAndView refreshPubDateOfAd(@RequestParam("adId") long adId,Principal user,
								  RedirectAttributes attr){
		ModelAndView model = new ModelAndView("redirect:/client/ads");
		
		try {
			adService.refreshPubDate(user.getName(), adId, HOURS_BETWEEN_REFRESHING);
			attr.addFlashAttribute("msg", "Date is successfully refreshed");
		} catch (NonExistedAdException e) {
			attr.addFlashAttribute("error", "You can't delete non existed ad");
		} catch (IllegalActionForAcceptedAd e) {
			attr.addFlashAttribute("error", "Sorry, but you can't delete advertisement, "
					+ "which has Accepted status");
		} catch (TooManyRefreshings e) {
			attr.addFlashAttribute("error", "Sorry, but you can refresh advertisement only "
					+ "one time in " + HOURS_BETWEEN_REFRESHING + " hours");
		}
		
		return model;
	}
	
	/**
	 * If flash attribute {@code adUrl} and {@code adId} exist, returns page with message about successful 
	 * creation of Ad, otherwise redirects to {@link #ads(Principal)} 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/success", method = RequestMethod.GET)
	public ModelAndView createdAd(HttpServletRequest request){
		Map<String, ?> inputFlashMap = RequestContextUtils.getInputFlashMap(request);
	    if (inputFlashMap == null || !inputFlashMap.containsKey("adUrl")
	    							|| !inputFlashMap.containsKey("adId")) {
	    	return new ModelAndView("redirect:/client/ads");
	    }
		return new ModelAndView("/client/createdAd");
	}
	
	/**
	 * Returns page for editing existed advertisement
	 * 
	 * <p>If Ad doesn't exist or his status doesn't allow to edit this Ad,
	 * redirects to {@link #ads(Principal)} with added flash attribute {@code error}
	 * 
	 * @param adId - id of {@code Ad} object
	 * @see ClientController#addMapsToAdView(ModelAndView)
	 */
	@RequestMapping(value = "/ads/edit", method = RequestMethod.GET)
	public ModelAndView editAd(@RequestParam("adId") long adId,Principal user,
								RedirectAttributes attr,HttpServletRequest request){
		
		ModelAndView errorView= new ModelAndView("redirect:/client/ads");
		
		final Map<String, ?> inputFlashMap = RequestContextUtils.getInputFlashMap(request);
	    if (inputFlashMap != null && inputFlashMap.containsKey("translateType") &&
	    		inputFlashMap.containsKey("ad")) {
	    	//if saving of edits failed or was successful
	    	//get translate type and return appropriate
	    	//page for editing
	    	TranslateType translateType = 
	    				(TranslateType)inputFlashMap.get("translateType");
	    	if(translateType.equals(TranslateType.ORAL)){
	    		ModelAndView model = new ModelAndView("/client/adbuilderoral");
	    		addMapsToAdView(model);
	    		model.addObject("createAd", false);
	    		return model;
	    	}else if(translateType.equals(TranslateType.WRITTEN)){
	    		ModelAndView model = new ModelAndView("/client/adbuilderwritten");
	    		addMapsToAdView(model);
	    		model.addObject("createAd", false);
	    		return model;
	    	}else{
	    		//unacceptable situation
	    		logger.error("'translateType' variable exists in flash attributes,"
	    				+ " but doesn't equal to ORAL or WRITTEN");
	    	}
	    }
		try {
			ModelAndView model = null;
			Ad ad = adService.getForUpdating(user.getName(),adId);
			if(ad.getTranslateType().equals(TranslateType.WRITTEN)){
				model = new ModelAndView("/client/adbuilderwritten");
			}else if(ad.getTranslateType().equals(TranslateType.ORAL)){
				model = new ModelAndView("/client/adbuilderoral");
			}else{
				//unacceptable situation
				logger.error("field 'translateType' of retrieved ad "
	    				+ " doesn't equal to ORAL or WRITTEN");
			}
			model.addObject("ad", ad);
			model.addObject("createAd", false);
			addMapsToAdView(model);
			return model;
		}  catch (NonExistedAdException e) {
			attr.addFlashAttribute("error","You can't edit non existed advertisement");
		} catch (IllegalActionForAcceptedAd e) {
			attr.addFlashAttribute("error","You can't edit advertisement, which has Accepted status");
		} catch (IllegalActionForAd e) {
			attr.addFlashAttribute("error","For editing advertisement, you must reject responses to it");
		}
		return errorView;
		
	}
	
	/**
	 * Checks {@code BindingResult} for errors, if such exists, 
	 * redirects to {@link #editAd(long, Principal, RedirectAttributes, HttpServletRequest)}(for entering valid data)
	 * with added flash attributes {@code ad}, {@code result} and {@code translateType}
	 * 
	 * <p>If data is valid updates old ad and returns page for editing with message 
	 * about successful updating
	 * 
	 * If Ad doesn't exist or his status doesn't allow to edit this Ad,
	 * redirects to {@link #ads(Principal)} with added flash attribute {@code error}
	 * @param adId
	 * @param editedAd
	 * @param result
	 * @param user
	 */
	@RequestMapping(value = "/saveOralAdEdits",method = RequestMethod.POST)
	public ModelAndView saveOralAdEdits(
							@Valid @ModelAttribute("ad") Ad editedAd,
							BindingResult result,
							Principal user,
							RedirectAttributes attr){
		if(result.hasErrors()){
			attr.addFlashAttribute("org.springframework.validation.BindingResult.ad", 
					result);
			attr.addFlashAttribute("ad",editedAd);
			attr.addFlashAttribute("translateType",editedAd.getTranslateType());
			return new ModelAndView("redirect:/client/ads/edit?adId="+editedAd.getId());
		}
		ModelAndView errorView= new ModelAndView("redirect:/client/ads");
		
		try {
			Ad updatedAd = adService.updateAd(user.getName(),editedAd);
			attr.addFlashAttribute("Editmsg", "Advertisement is edited successfully");
			attr.addFlashAttribute("ad",updatedAd);
			attr.addFlashAttribute("translateType",editedAd.getTranslateType());
			return new ModelAndView("redirect:/client/ads/edit?adId="+updatedAd.getId());
		} catch (NonExistedAdException e) {
			attr.addFlashAttribute("error","You can't edit non existed advertisement");
		} catch (IllegalActionForAcceptedAd e) {
			attr.addFlashAttribute("error","You can't edit advertisement, which has Accepted status");
		} catch (IllegalActionForAd e) {
			attr.addFlashAttribute("error","For editing advertisement, you must reject responses to it");
		}
			
		return errorView;
		
	}
	/**
	 * This method is similar to {@link #saveOralAdEdits(Ad, BindingResult, Principal, RedirectAttributes)},
	 * <br>difference is that this method checks if {@code file} is empty or it is invalid file,
	 * in this case redirects to {@link #editAd(long, Principal, RedirectAttributes, HttpServletRequest)}
	 * @param editedAd
	 * @param result
	 * @param user
	 * @param attr
	 */
	@RequestMapping(value = "/saveWrittenAdEdits",method = RequestMethod.POST)
	public ModelAndView saveWrittenAdEdits(
							@Valid @ModelAttribute("ad") Ad editedAd,
							BindingResult result,
							@RequestParam(name="multipartFile",required=false)
							MultipartFile file,
							Principal user,
							RedirectAttributes attr){
		if(result.hasErrors()){
			attr.addFlashAttribute("org.springframework.validation.BindingResult.ad", 
					result);
			attr.addFlashAttribute("ad",editedAd);
			attr.addFlashAttribute("translateType",editedAd.getTranslateType());
			return new ModelAndView("redirect:/client/ads/edit?adId="+editedAd.getId());
		}
		ModelAndView errorView= new ModelAndView("redirect:/client/ads");
		
		if(!file.isEmpty()){
			if(!isfileContentTypeAllowed(file)){
				attr.addFlashAttribute("error", "Please choose file with .pdf or .doc(.docx) extension");
				attr.addFlashAttribute("translateType",editedAd.getTranslateType());
				attr.addFlashAttribute("ad",editedAd);
				return new ModelAndView("redirect:/client/ads/edit?adId="+editedAd.getId());
			}
			try {
				byte[] fileWithText = file.getBytes();
				editedAd.setDocument(
						new Document(editedAd,fileWithText,file.getOriginalFilename(),file.getContentType()));
			} catch (IOException e) {
				attr.addFlashAttribute("ad",editedAd);
				attr.addFlashAttribute("translateType",editedAd.getTranslateType());
				attr.addFlashAttribute("error", "Some problem with your file, please repeat action");
				return new ModelAndView("redirect:/client/ads/edit?adId="+editedAd.getId());
			}
		}
		
		//If all data is valid, attempt update Ad
		try {
			Ad updatedAd = adService.updateAd(user.getName(),editedAd);
			attr.addFlashAttribute("Editmsg", "Advertisement is edited successfully");
			attr.addFlashAttribute("ad",updatedAd);
			attr.addFlashAttribute("translateType",editedAd.getTranslateType());
			return new ModelAndView("redirect:/client/ads/edit?adId="+updatedAd.getId());
		} catch (NonExistedAdException e) {
			attr.addFlashAttribute("error","You can't edit non existed advertisement");
		} catch (IllegalActionForAcceptedAd e) {
			attr.addFlashAttribute("error","You can't edit advertisement, which has Accepted status");
		} catch (IllegalActionForAd e) {
			attr.addFlashAttribute("error","For editing advertisement, you must reject responses to it");
		}
			
		return errorView;
		
	}
	
	/**
	 * Returns {@code ModelAndView} page with {@link RespondedAd}s of this client
	 * in order from latest to earliest
	 * @param user - {@code Principal} object for retrieving client from db
	 * @return
	 */
	@RequestMapping(value = "/responses",method = RequestMethod.GET)
	public ModelAndView respondedAds(Principal user,
									 @RequestParam(name="page",defaultValue="1",required = false) int page){
		
		Set<RespondedAd> respondedAds = null;
		try {
			respondedAds = clientService
					.getRespondedAds(user.getName(),page,RESPONDED_ADS_ON_PAGE);
		} catch (WrongPageNumber e) {
			try {
				respondedAds = clientService
						.getRespondedAds(user.getName(),1,RESPONDED_ADS_ON_PAGE);
			} catch (WrongPageNumber unused) {}
		}
		
		long nunmberOfPages = clientService
				.getNumberOfPagesForRespondedAds(user.getName(), RESPONDED_ADS_ON_PAGE);
		ModelAndView model = new ModelAndView("/client/responses");
		model.addObject("respondedAds", respondedAds);
		model.addObject("numberOfPages",nunmberOfPages);
		return model;
	}
	
	/**
	 * If {@code RespondedAd} with {@code respondedAdId} exists, rejects it,
	 * in any case redirects to {@link #respondedAds(Principal)}
	 */
	@RequestMapping(value = "/reject",method = RequestMethod.POST)
	public ModelAndView rejectRespondedAd(@RequestParam(name="id") long respondedAdId,
										  Principal user){
		try {
			respondedAdService.reject(user.getName(),respondedAdId);
		} catch (NonExistedRespondedAdException | IllegalActionForAcceptedAd e) {
			return new ModelAndView("redirect:/client/responses");
		} 
		return new ModelAndView("redirect:/client/responses");
	}
	
	/**
	 * If {@code RespondedAd} with {@code respondedAdId} exists,this respondedAd
	 * hasn't ACCEPTED status,and translator, related to respondedAd, isn't busy, 
	 * accepts it
	 * <p>If  translator is busy, redirects to appropriate page with message about that,
	 * otherwise redirects to {@link #respondedAds(Principal, int)}
	 */
	@RequestMapping(value = "/accept",method = RequestMethod.POST)
	public ModelAndView acceptRespondedAd(@RequestParam("id") long respondedAdId,
										Principal user){
		
		try {
			respondedAdService.accept(user.getName(),respondedAdId);
		} catch (NonExistedRespondedAdException | IllegalActionForAcceptedAd |IllegalActionForRejectedAd e) {
			return new ModelAndView("redirect:/client/responses");
		} catch (TranslatorDistraction e) {
			return new ModelAndView("redirect:/client/error/busyTranslator");
		} 
		return new ModelAndView("redirect:/client/responses");
	}
	
	/**
	 * Returns page with message, that translator executes another order in that moment
	 */
	@RequestMapping(value = "/error/busyTranslator",method = RequestMethod.GET)
	public ModelAndView busyTranslator(){
		return new ModelAndView("/client/error/busyTranslator");
	}
	
	/**
	 * Attempts to add comment to {@link Translator} with id={@code translatorId}
	 * Redirects to profile of this translator
	 * @param comment
	 * @param result
	 * @param translatorId
	 * @param user
	 * @param attr
	 */
	@RequestMapping(value = "/addComment",method = RequestMethod.POST)
	public ModelAndView addComment(@Valid @ModelAttribute("comment") Comment comment,
									BindingResult result,
									@RequestParam(name = "translatorId",required=true) long translatorId,
									Principal user,
									RedirectAttributes attr){
		if(result.hasErrors()){
			attr.addFlashAttribute("comment", comment);
			attr.addFlashAttribute(
					"org.springframework.validation.BindingResult.comment", result);
			return new ModelAndView(
					"redirect:/translators/"+translatorId);
		}
		commentService.save(comment,user.getName(),translatorId);
		return new ModelAndView(
				"redirect:/translators/"+translatorId);
	}
	
	
	
	/**
	 * Adds maps of {@link Language}, {@link Currency}
	 * to {@code model} for rendering available variants to the client while
	 * creating advertisement
	 * @see #getCurrenciesForSelect()
	 * @see #getLanguagesForSelect()
	 */
  private void addMapsToAdView(ModelAndView model){
  	model.addObject("languages", getLanguagesForSelect());
	model.addObject("currencies", getCurrenciesForSelect());
  }
  
  
  
  
  /**
   * Returns {@code Map}, where keys and values - numbers from 1 to 31
   */
  private Map<String,String> getDays(){
	  Map<String,String> days= new HashMap<>();
	  for(int i=1;i<=31;i++){
		  days.put(i+"", i+"");
	  }
	  return days;
  }
  
  /**
   * Returns {@code Map}, where keys - numbers from 1 to 12, values - months
   * from {@link Month#January} to {@link Month#DECEMBER} 
   */
  private Map<String,String> getMonth(){
	  Map<String,String> months= new HashMap<>();
	  for(int i=1;i<=12;i++){
		  months.put(i+"", Month.of(i)+"");
	  }
	  return months;
  }
  
  /**
   * Returns {@code Map}, where keys and value - numbers from 1900 to 2016
   */
  private Map<String,String> getYears(){
	  Map<String,String> months= new HashMap<>();
	  for(int i=1900;i<=2016;i++){
		  months.put(i+"", i+"");
	  }
	  return months;
  }
  
  /**
   * Checks if ContentType of {@code file} with text is allowed for uploading
   * <br>Allowed content types are placed in {@link #ALLOWED_CONTENT_TYPES_FOR_TEXT}
   * @param file
   */
  private boolean isfileContentTypeAllowed(MultipartFile file){
	  logger.debug("Content type of text file is: " + file.getContentType());
	  return Arrays.asList(ALLOWED_CONTENT_TYPES_FOR_TEXT)
			  	   .contains(file.getContentType());
  }
} 
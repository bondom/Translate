package ua.translate.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import ua.translate.controller.support.ControllerHelper;
import ua.translate.model.Translator;
import ua.translate.model.Language;
import ua.translate.model.Translator;
import ua.translate.model.ad.Ad;
import ua.translate.model.ad.ResponsedAd;
import ua.translate.model.viewbean.ChangeEmailBean;
import ua.translate.model.viewbean.ChangePasswordBean;
import ua.translate.service.TranslatorService;
import ua.translate.service.exception.DuplicateEmailException;
import ua.translate.service.exception.EmailIsConfirmedException;
import ua.translate.service.exception.InvalidConfirmationUrl;
import ua.translate.service.exception.InvalidPasswordException;
import ua.translate.service.exception.NonExistedAdException;
import ua.translate.service.exception.WrongPageNumber;
import ua.translate.service.impl.AdServiceImpl;

@Controller
@RequestMapping("/translator")
public class TranslatorController extends UserController{

	@Autowired
	private TranslatorService translatorService;
	
	@Autowired
	ControllerHelper controllerHelper;
	
	Logger logger = LoggerFactory.getLogger(TranslatorController.class);
	
	private static final int RESPONSED_ADS_ON_PAGE=3;
	
	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public ModelAndView init(){
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
	public ModelAndView profile(Principal user,
								@RequestParam(value="psuccess",required = false) String psuccess) throws UnsupportedEncodingException{
		Translator translatorFromDB = (Translator) translatorService.getUserByEmail(user.getName());
		ModelAndView model = new ModelAndView("/translator/profile");
		model.addObject("translator", translatorFromDB);
		model.addObject("image", controllerHelper.getAvaForRendering(translatorFromDB.getAvatar()));
		if(psuccess!=null){
			model.addObject("passSaved","Your password is saved successfully");
		}
		return model;
	}
	
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
	
	@RequestMapping(value = "/registration",method = RequestMethod.GET)
	public ModelAndView getRegistrationForm(){
		ModelAndView model = new ModelAndView("/translator/registration");
		Translator translator = new Translator();
		model.addObject("translator", translator);
		model.addObject("languages",Language.values());
		return model;
	}
	
	@RequestMapping(value = "/registrationConfirm",method = RequestMethod.POST)
	public ModelAndView registration(@Valid @ModelAttribute("translator") Translator translator,
									@RequestParam(name = "selectedLanguages",required = true) List<String> stringLanguages,
								BindingResult result){
		if(result.hasErrors()){
			ModelAndView model = new ModelAndView("/translator/registration");
			model.addObject("languages",Language.values());
			return model;
		}
		
		Set<Language> enumLanguages= new LinkedHashSet<>();
		for(String language:stringLanguages){
			enumLanguages.add(Language.valueOf(language));
		}
		translator.setLanguages(enumLanguages);
		try {
			translatorService.registerUser(translator);
			ModelAndView loginView = new ModelAndView("/translator/login");
			loginView.addObject("resultRegistration", 
									"Success registration!");
			return loginView;
		} catch (DuplicateEmailException e) {
			ModelAndView registrationView = new ModelAndView("/translator/registration");
			registrationView.addObject("resultRegistration",e.getMessage());
			return registrationView;
		}
	}
	
	/**
	 * Saves confirmation url in data storage,
	 * returns {@code ModelAndView} with info about sending letter for confirmation
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
		
		ModelAndView model = new ModelAndView("/translator/sendedLetter");
		return model;
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
	 * Returns {@code ModelAndView} page for editing email, if
	 * translator is not authenticated via remember-me authentication
	 * <p>if is, return login form page for re-entering credentials
	 */
	@RequestMapping(value = "/email",method = RequestMethod.GET)
	public ModelAndView editEmail(Principal user){
		if(isRememberMeAuthenticated()){
			ModelAndView model = new ModelAndView("/translator/login");
			model.addObject("loginUpdate",true);
			return model;
		}else{
			ModelAndView model = new ModelAndView("/translator/editEmail");
			ChangeEmailBean changeEmailBean = new ChangeEmailBean();
			model.addObject("changeEmailBean",changeEmailBean);
			return model;
		}
	}
	
	/**
	 * Returns {@code ModelAndView} page for editing password, if
	 * translator is not authenticated via remember-me authentication
	 * <p>if is, return login form page for re-entering credentials
	 */
	@RequestMapping(value = "/password",method = RequestMethod.GET)
	public ModelAndView editPassword(Principal user){
		if(isRememberMeAuthenticated()){
			ModelAndView model = new ModelAndView("/translator/login");
			model.addObject("loginUpdate",true);
			return model;
		}else{
			ModelAndView model = new ModelAndView("/translator/editPassword");
			ChangePasswordBean changePasswordBean = new ChangePasswordBean();
			model.addObject("changePasswordBean",changePasswordBean);
			return model;
		}
	}
	
	/**
	 * Returns {@link ModelAndView} translator's page for editing profile
	 */
	@RequestMapping(value = "/edit",method = RequestMethod.GET)
	public ModelAndView editProfile(Principal user, HttpServletRequest request){
		Translator translatorFromDB = translatorService.getTranslatorByEmail(user.getName());
	
		ModelAndView model = new ModelAndView("/translator/edit");
		model.addObject("translator", translatorFromDB);
		model.addObject("languages",Language.values());
		return model;
	}
	
	/**
	 * Attempts to update translator's email, if email is registered already or password is invalid,
	 * returns page with appropriate error messages
	 * @throws UnsupportedEncodingException 
	 */
	@RequestMapping(value = "/saveEmail",method = RequestMethod.POST)
	public ModelAndView saveEmail(
			@Valid @ModelAttribute("changeEmailBean") ChangeEmailBean changeEmailBean,
			BindingResult changeEmailResult,
			Principal user,
			HttpServletRequest request) throws UnsupportedEncodingException{
		
		if(isRememberMeAuthenticated()){
			//setRememberMeTargetUrlToSession(translatorFromDB,request);
			ModelAndView model = new ModelAndView("/translator/login");
			model.addObject("loginUpdate",true);
			return model;
		}
		
		if(changeEmailResult.hasErrors()){
			ModelAndView model = new ModelAndView("/translator/editEmail");
			return model;
		}
		try {
			translatorService.updateUserEmail(user.getName(), 
										  changeEmailBean.getNewEmail(), 
										  changeEmailBean.getCurrentPassword());
		}catch (InvalidPasswordException e) {
			ModelAndView model = new ModelAndView("/translator/editEmail");
			model.addObject("invalidPassword",e.getMessage());
			return model;
		}catch (DuplicateEmailException e) {
			ModelAndView model = new ModelAndView("/translator/editEmail");
			model.addObject("duplicateEmail","This email is registered in system already");
			return model;
		}
		
		refreshUsername(changeEmailBean.getNewEmail());
		Translator translatorFromDB = translatorService.getTranslatorByEmail(user.getName());
		ModelAndView model = new ModelAndView("/translator/profile");
		model.addObject("translator", translatorFromDB);
		model.addObject("emailSaved","Your email is saved successfully");
		model.addObject("image", controllerHelper.getAvaForRendering(translatorFromDB.getAvatar()));
		return model;
	}
	/**
    * Attempts to update translator's password, if password is invalid,
	 * returns page with appropriate error message
	 * @throws UnsupportedEncodingException 
	 */
	@RequestMapping(value = "/savePassword",method = RequestMethod.POST)
	public ModelAndView savePassword(
			@Valid @ModelAttribute("changePasswordBean") ChangePasswordBean changePasswordBean,
			BindingResult changePasswordResult,
			Principal user,
			HttpServletRequest request) throws UnsupportedEncodingException{
		
		if(isRememberMeAuthenticated()){
			//setRememberMeTargetUrlToSession(translatorFromDB,request);
			ModelAndView model = new ModelAndView("/translator/login");
			model.addObject("loginUpdate",true);
			return model;
		}
		
		if(changePasswordResult.hasErrors()){
			ModelAndView model = new ModelAndView("/translator/editPassword");
			return model;
		}
		try {
			translatorService.updateUserPassword(user.getName(), 
											 changePasswordBean.getOldPassword(), 
											 changePasswordBean.getNewPassword());
		} catch (InvalidPasswordException e) {
			ModelAndView model = new ModelAndView("/translator/editPassword");
			model.addObject("wrongOldPassword",e.getMessage());
			return model;
		}
		
		ModelAndView model = new ModelAndView("redirect:/translator/profile?psuccess");
		return model;
	}
	
	/**
	 * <p>If no errors exist updates the user's profile and redirects to {@link #profile(Principal)}
	 * @param editedTranslator - {@code Translator} object with changes made by translator
	 * @param result - {@code BindingResult} object for checking errors
	 * @param user - {@code Principal} object for retrieving {@code Translator} from db
	 * @throws UnsupportedEncodingException 
	 */
	@RequestMapping(value = "/saveEdits",method = RequestMethod.POST)
	public ModelAndView saveProfileEdits(
							@Valid @ModelAttribute("translator") Translator editedTranslator,
							BindingResult result,
							@RequestParam(name = "selectedLanguages",required = true) List<String> stringLanguages,
							Principal user) throws UnsupportedEncodingException{
		
		if(result.hasErrors()){
			ModelAndView model = new ModelAndView("/translator/edit");
			model.addObject("languages",Language.values());
			result.getAllErrors().forEach(error ->
					logger.debug("{}:{}",error.getObjectName(),error.getDefaultMessage()));
			return model;
		}
		
		Set<Language> enumLanguages= new LinkedHashSet<>();
		for(String language:stringLanguages){
			enumLanguages.add(Language.valueOf(language));
		}
		editedTranslator.setLanguages(enumLanguages);
		
		translatorService.updateUserProfile(user.getName(), editedTranslator);
		
		Translator translatorFromDB = translatorService.getTranslatorByEmail(user.getName());
		ModelAndView model = new ModelAndView("/translator/profile");
		model.addObject("translator", translatorFromDB);
		model.addObject("profileSaved","Your profile is saved successfully");
		model.addObject("image", controllerHelper.getAvaForRendering(translatorFromDB.getAvatar()));
		return model;
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
						@RequestParam("file") MultipartFile file){
		if(!file.isEmpty()){
			try {
				String contentType = file.getContentType();
				if(contentType.startsWith("image/")){
					byte[] avatar = file.getBytes();
					translatorService.updateAvatar(user.getName(), avatar);
				}
				ModelAndView model = new ModelAndView("redirect:/translator/profile");
				return model;
			} catch (IOException e) {
				ModelAndView model = new ModelAndView("/translator/profile");
				model.addObject("error", "Some problem with your avatar, please repeat action");
				return model;
			}
		}
		ModelAndView model = new ModelAndView("redirect:/translator/profile");
		return model;
		
	}
	
	@RequestMapping(value="/response")
	public ModelAndView response(@RequestParam("adId") long adId, 
								Principal user){
		try {
			translatorService.saveResponsedAd(user.getName(),adId);
			return new ModelAndView("/translator/sr");
		} catch (NonExistedAdException e) {
			ModelAndView model = new ModelAndView("/adsForAll");
			model.addObject("error","Advertisement with such id doesn't exist, please try other");
			return model;
		}
	}
	
	/**
	 * Returns {@code ModelAndView} page with {@link ResponsedAd}s of this client
	 * in order from latest to earliest
	 * @param user - {@code Principal} object for retrieving client from db
	 * @return
	 */
	@RequestMapping(value = "/responses",method = RequestMethod.GET)
	public ModelAndView responsedAds(Principal user,
									 @RequestParam(name="page",defaultValue="1",required = false) int page){
		
		Set<ResponsedAd> responsedAds = null;
		try {
			responsedAds = translatorService
					.getResponsedAds(user.getName(),page,RESPONSED_ADS_ON_PAGE);
		} catch (WrongPageNumber e) {
			try {
				responsedAds = translatorService
						.getResponsedAds(user.getName(),1,RESPONSED_ADS_ON_PAGE);
			} catch (WrongPageNumber unused) {}
		}
		
		long nunmberOfPages = translatorService
				.getNumberOfPagesForResponsedAds(user.getName(), RESPONSED_ADS_ON_PAGE);
		ModelAndView model = new ModelAndView("/translator/responses");
		model.addObject("responsedAds", responsedAds);
		model.addObject("numberOfPages",nunmberOfPages);
		return model;
	}
		
	
	
}
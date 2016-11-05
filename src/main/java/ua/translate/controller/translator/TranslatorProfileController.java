package ua.translate.controller.translator;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

import ua.translate.controller.UserController;
import ua.translate.controller.support.ControllerHelper;
import ua.translate.model.Language;
import ua.translate.model.Translator;
import ua.translate.model.viewbean.ChangeEmailBean;
import ua.translate.model.viewbean.ChangePasswordBean;
import ua.translate.service.TranslatorService;
import ua.translate.service.exception.DuplicateEmailException;
import ua.translate.service.exception.EmailIsConfirmedException;
import ua.translate.service.exception.InvalidConfirmationUrl;
import ua.translate.service.exception.InvalidPasswordException;


/**
 * Contains handler methods for executing
 * translator's authentication and actions with
 * his account, such as :
 * <ul>
 * 	<li>Editing email,password,profile info</li>
 * 	<li>Confirmation email</li>
 * </ul> 
 * 
 * @author Yuriy Phediv
 *
 */
@Controller
@RequestMapping("/translator")
public class TranslatorProfileController extends UserController{
	
	@Autowired
	private TranslatorService translatorService;
	
	@Autowired
	private ControllerHelper controllerHelper;
	
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
	
	
	
	
	
}

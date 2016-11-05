package ua.translate.controller.client;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

import ua.translate.controller.UserController;
import ua.translate.controller.support.ControllerHelper;
import ua.translate.model.Client;
import ua.translate.model.viewbean.ChangeEmailBean;
import ua.translate.model.viewbean.ChangePasswordBean;
import ua.translate.service.ClientService;
import ua.translate.service.exception.DuplicateEmailException;
import ua.translate.service.exception.EmailIsConfirmedException;
import ua.translate.service.exception.InvalidConfirmationUrl;
import ua.translate.service.exception.InvalidPasswordException;

/**
 * Contains handler methods for executing
 * client's authentication, registration and actions with
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
@RequestMapping("/client")
public class ProfileClientController extends UserController {
	
	@Autowired
	private ClientService clientService;
	
	@Autowired
	ControllerHelper controllerHelper;
	
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
}

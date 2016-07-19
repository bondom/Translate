package ua.translate.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

import ua.translate.model.Translator;
import ua.translate.model.User;
import ua.translate.model.UserRole;
import ua.translate.model.ad.Language;
import ua.translate.service.UserService;

@Controller
@RequestMapping("/translator")
public class TranslatorController {

	@Autowired
	@Qualifier("translatorService")
	private UserService<Translator> translatorService;
	
	@Autowired
	private AuthenticationTrustResolver authenticationTrustResolver;
	
	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public ModelAndView init(){
		return new ModelAndView("/translator/init");
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
		if(translatorFromDB.getAvatar() == null){
			System.out.println("OOPS");
			/**
			 * Поменять!!!
			 */
		}else{
			model.addObject("image", convertImageForRendering(translatorFromDB.getAvatar()));
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
			return new ModelAndView("/translator/registration");
		}
		//String[] stringLanguages = request.getParameterValues("selectedLanguages");
		List<Language> enumLanguages= new ArrayList<>();
		for(String language:stringLanguages){
			enumLanguages.add(Language.valueOf(language));
		}
		translator.setLanguages(enumLanguages);
		if(translatorService.registerUser(translator)){
			ModelAndView loginView = new ModelAndView("/translator/login");
			loginView.addObject("resultRegistration", 
									"Success registration!");
			return loginView;
		}else{
			//Registration failed
			ModelAndView registrationView = new ModelAndView("/translator/registration");
			registrationView.addObject("resultRegistration", 
									"User with the same login or email "
									+ "is registered in system already");
			return registrationView;
		}
	}
	/**
	 * @param user
	 * @return page for editing a user's profile
	 */
	@RequestMapping(value = "/edit",method = RequestMethod.GET)
	public ModelAndView editPage(Principal user, HttpServletRequest request){
		Translator translatorFromDB = (Translator) translatorService.getUserByEmail(user.getName());
		if(isRememberMeAuthenticated()){
			//setRememberMeTargetUrlToSession(translatorFromDB,request);
			ModelAndView model = new ModelAndView("/translator/login");
			model.addObject("loginUpdate",true);
			model.addObject("translator", translatorFromDB);
			return model;
		}else{
			ModelAndView model = new ModelAndView("/translator/edit");
			model.addObject("translator", translatorFromDB);
			return model;
		}
	}
	
	/**
	 * Checks if new email exist and
	 * if some errors exist returns initial page(for editing user's profile)
	 * 
	 * If no errors exist updates the user and returns user's profile page
	 * @param newUser - {User.class} object with changes made by user
	 * @param result
	 * @param user - {Principal.class} object with name equals to {User.class} object's login
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	@RequestMapping(value = "/saveEdits",method = RequestMethod.POST)
	public ModelAndView saveEdits(
			@Valid @ModelAttribute("translator") Translator editedTranslator,
			BindingResult result,
			Principal user) throws UnsupportedEncodingException{
		if(result.hasErrors()){
			return new ModelAndView("/translator/edit");
		}
		Translator translator = translatorService.editUserProfile(user.getName(), editedTranslator);
		if(translator == null){
			ModelAndView model = new ModelAndView("/translator/edit");
			model.addObject("emailExists","Such email is registered in system already");
			return model;
		}else{
			ModelAndView editedProfile = new ModelAndView("/translator/profile");
			editedProfile.addObject("translator", translator);
			if(translator.getAvatar()!=null){
				editedProfile.addObject("image", convertImageForRendering(translator.getAvatar()));
			}
			return editedProfile;
		}
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
					Translator updatedTranslator = (Translator) translatorService.updateAvatar(user.getName(), avatar);
					ModelAndView model = new ModelAndView("/translator/profile");
					model.addObject("translator", updatedTranslator);
					model.addObject("image", convertImageForRendering(updatedTranslator.getAvatar()));
					return model;
				}else{
					Translator translatorFromDB = (Translator) translatorService.getUserByEmail(user.getName());
					ModelAndView model = new ModelAndView("/translator/edit");
					model.addObject("translator", translatorFromDB);
					model.addObject("wrongFile", "Please, choose image.");
					return model;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				/**
				 * Заглушка, поменять!!
				 */
				return new ModelAndView("/translator/exception");
			}
		}else{
			ModelAndView model = new ModelAndView("/translator/edit");
			Translator translatorFromDB = (Translator) translatorService.getUserByEmail(user.getName());
			model.addObject("translator", translatorFromDB);
			return model;
		}
		
	}
	
	private String convertImageForRendering(byte[] image) throws UnsupportedEncodingException{
		byte[] encodeBase64 = Base64.encodeBase64(image); 
		String base64Encoded = new String(encodeBase64,"UTF-8");
		return base64Encoded;
	}
	
	
	private boolean isRememberMeAuthenticated() {
		Authentication authentication = 
                    SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null) {
			return false;
		}
		return RememberMeAuthenticationToken.class.isAssignableFrom(authentication.getClass());
	}
	
	private void setRememberMeTargetUrlToSession(User user, HttpServletRequest request){
		HttpSession session = request.getSession(false);
		if(session!=null){
			UserRole role= user.getRole();
			if(role.equals(UserRole.ROLE_TRANSLATOR)){
				session.setAttribute("targetUrl", "/translator/edit");
			}	
		}
	}
	
	private String getRememberMeTargetUrlFromSession(HttpServletRequest request){
		String targetUrl = "";
		HttpSession session = request.getSession(false);
		if(session!=null){
			targetUrl = session.getAttribute("targetUrl")==null?""
                             :session.getAttribute("targetUrl").toString();
			System.out.println("targetUrl = " + targetUrl);
		}
		return targetUrl;
	}
	
	/**
     * This method returns true if users is already authenticated [logged-in], else false.
     */
    private boolean isCurrentAuthenticationAnonymous() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authenticationTrustResolver.isAnonymous(authentication);
    }
	
	
}
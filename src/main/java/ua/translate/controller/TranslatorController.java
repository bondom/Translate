package ua.translate.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

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

import ua.translate.model.Language;
import ua.translate.model.Translator;
import ua.translate.model.ad.Ad;
import ua.translate.service.TranslatorService;
import ua.translate.service.exception.DuplicateEmailException;
import ua.translate.service.exception.NonExistedAdException;
import ua.translate.service.impl.AdServiceImpl;

@Controller
@RequestMapping("/translator")
public class TranslatorController extends UserController{

	@Autowired
	private TranslatorService translatorService;
	
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
	public ModelAndView profile(Principal user) throws UnsupportedEncodingException{
		Translator translatorFromDB = (Translator) translatorService.getUserByEmail(user.getName());
		ModelAndView model = new ModelAndView("/translator/profile");
		model.addObject("translator", translatorFromDB);
		if(translatorFromDB.getAvatar() != null){
			model.addObject("image", convertAvaForRendering(translatorFromDB.getAvatar()));
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
		//String[] stringLanguages = request.getParameterValues("selectedLanguages");
		List<Language> enumLanguages= new ArrayList<>();
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
	 * <p>If no errors exist updates the user's profile and redirects to {@link #profile(Principal)}
	 * @param editedTranslator - {@code Translator} object with changes made by client
	 * @param result - {@code BindingResult} object for checking errors
	 * @param user - {@code Principal} object for retrieving {@code Client} from db
	 */
	@RequestMapping(value = "/saveEdits",method = RequestMethod.POST)
	public ModelAndView saveProfileEdits(
							@Valid @ModelAttribute("translator") Translator editedTranslator,
							BindingResult result,
							Principal user){
		
		if(result.hasErrors()){
			ModelAndView model = new ModelAndView("/translator/edit");
			return model;
		}
		
		translatorService.updateUserProfile(user.getName(), editedTranslator);
		ModelAndView editedProfile = new ModelAndView("redirect:/translator/profile");
		return editedProfile;
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
		
	
	
}
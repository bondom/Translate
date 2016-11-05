package ua.translate.controller.translator;

import java.time.LocalDate;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

import ua.translate.model.Language;
import ua.translate.model.Translator;
import ua.translate.service.TranslatorService;
import ua.translate.service.exception.DuplicateEmailException;

/**
 * This class is responsible for handling of full process of 
 * {@link Translator} registration.
 * 
 * @author Yuriy Phediv
 *
 */
@Controller
@RequestMapping("/translator")
public class TranslatorRegistrationController {
	
	
	@Autowired
	private TranslatorService translatorService;
	
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
	
	
	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ModelAndView missingParameterHandler(HttpServletRequest request,
						MissingServletRequestParameterException exception) {
		return new ModelAndView("forward:/translator/handleException");
	} 
	
	@RequestMapping(value = "/handleException",method = RequestMethod.POST)
	public ModelAndView redirectToRegistrationPageWithErrorMsg(HttpServletRequest request,
										   RedirectAttributes attr){
		Translator translator = new Translator();
		translator.setFirstName(request.getParameter("firstName"));
		translator.setLastName(request.getParameter("lastName"));
		translator.setCountry(request.getParameter("country"));
		translator.setCity(request.getParameter("city"));
		translator.setEmail(request.getParameter("email"));
		translator.setBirthday(
				convertBirthday(request.getParameter("birthday")));
		
		translator.setPhoneNumber(request.getParameter("phoneNumber"));
		
		attr.addFlashAttribute("resultRegistration", "You must choose at least one language");
		attr.addFlashAttribute("translator",translator);
		return new ModelAndView("redirect:/translator/registration");
	}
	
	/**
	 * Returns String representation of birthday, pattern of which matches
	 * to specified in {@link DateTimeFormat} on 
	 * {@link ua.translate.model.User#getBirthday() User.birthday}, to
	 * LocalDate representation.
	 * If {@code birthday} doesn't match to pattern, specified above,
	 * null is returned
	 * @param birthday
	 */
	private LocalDate convertBirthday(String birthday){
		String birthdayPattern="";
		try {
			birthdayPattern = Translator.class.getSuperclass()
												.getDeclaredField("birthday")
												.getAnnotation(DateTimeFormat.class)
												.pattern();
		} catch (NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
		}
		
		LocalDate parsedDateOfBirth = null;
		try{
			parsedDateOfBirth = LocalDate.parse(
					birthday,
					new DateTimeFormatterBuilder()
						.appendPattern(birthdayPattern)
						.toFormatter());
		}catch(DateTimeParseException unused){}
		return parsedDateOfBirth;
	}
}

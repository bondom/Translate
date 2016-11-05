package ua.translate.controller.admin;

import java.security.Principal;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

import ua.translate.model.settings.Settings;
import ua.translate.service.SettingsService;
import ua.translate.service.exception.InvalidIdentifier;

@Controller
@RequestMapping("/bulbular/settings")
public class ActionsWithSettingsAdminController {

	
	@Autowired private SettingsService settingsService;
	
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView getProjectSettings(HttpServletRequest request){
		final ModelAndView model = new ModelAndView("/admin/settings");

		final Map<String, ?> inputFlashMap = 
				RequestContextUtils.getInputFlashMap(request);
		if(inputFlashMap == null || !inputFlashMap.containsKey("settings")){
			Settings settings;
			try {
				settings = settingsService.getProjectSettings();
				model.addObject("settings", settings);
			} catch (InvalidIdentifier e) {}
		}
		
		return model;
	}	
	
	@RequestMapping(value = "/edit",method = RequestMethod.GET)
	public ModelAndView editSettings(HttpServletRequest request){
		final ModelAndView model = new ModelAndView("/admin/changeSettings");
		
		final Map<String, ?> inputFlashMap = 
				RequestContextUtils.getInputFlashMap(request);
		if(inputFlashMap == null || !inputFlashMap.containsKey("settings")){
			Settings settings;
			try {
				settings = settingsService.getProjectSettings();
				model.addObject("settings", settings);
			} catch (InvalidIdentifier e) {}
		}
		
		return model;
	}
	
	@RequestMapping(value = "/saveEdits",method = RequestMethod.POST)
	public ModelAndView saveEdits(@Valid @ModelAttribute("settings") Settings settings,
								  BindingResult result,Principal user,
								  RedirectAttributes attr){
		if(result.hasErrors()){
			attr.addFlashAttribute("org.springframework.validation.BindingResult.settings", 
					result);
			attr.addFlashAttribute("settings",settings);
			return new ModelAndView("redirect:/bulbular/settings/edit");
		}
		
		settingsService.updateSettings(settings);
		final ModelAndView model = new ModelAndView("redirect:/bulbular/settings");
		attr.addFlashAttribute("settings", settings);
		return model;
	}
}

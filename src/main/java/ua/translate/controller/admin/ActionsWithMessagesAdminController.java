package ua.translate.controller.admin;

import java.security.Principal;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

import ua.translate.model.Order;
import ua.translate.model.UserEntity;
import ua.translate.model.ad.Ad;
import ua.translate.model.ad.Ad.TranslateType;
import ua.translate.model.ad.AdStatusMessage;
import ua.translate.model.ad.WrittenAd;
import ua.translate.model.settings.Settings;
import ua.translate.model.status.AdStatus;
import ua.translate.model.viewbean.AdView;
import ua.translate.service.AdService;
import ua.translate.service.AdStatusMessageService;
import ua.translate.service.WrittenAdService;
import ua.translate.service.exception.AccessDeniedException;
import ua.translate.service.exception.IllegalActionForAd;
import ua.translate.service.exception.InvalidIdentifier;
import ua.translate.service.exception.WrongPageNumber;

@Controller
@RequestMapping("/bulbular")
public class ActionsWithMessagesAdminController {

	
	@Autowired
	private AdStatusMessageService adStatusMessageService;
	
	@RequestMapping(value="/admsgs",method = RequestMethod.GET)
	public ModelAndView getPageWithStatusesAndMessages(){
		Set<AdStatusMessage> adStatusMessages = 
				adStatusMessageService.getAllAdStatusMessages();
		
		ModelAndView model = new ModelAndView("/admin/adStatusesAndMessages");
		model.addObject("adStatusesAndMessages", adStatusMessages);
		return model;
	}
	
	@RequestMapping(value = "/changeMsgs",method = RequestMethod.GET)
	public ModelAndView getPageForChanging(
					@RequestParam(name="adStatus",required = true) AdStatus adStatus,
					@RequestParam(name="translateType",required = true) TranslateType translateType,
					HttpServletRequest request){
		ModelAndView model = new ModelAndView("/admin/changeMessagesForStatus");
		final Map<String, ?> inputFlashMap = 
				RequestContextUtils.getInputFlashMap(request);
		if(inputFlashMap == null || !inputFlashMap.containsKey("adStatusMessage")){
			AdStatusMessage adStatusMessage;
			adStatusMessage = adStatusMessageService
					.getAdStatusMessageByAdStatusAndTranslateType(adStatus,translateType);
			model.addObject("adStatusMessage", adStatusMessage);
		}
		
		return model;
	}
	
	
	@RequestMapping(value = "/saveChanges",method = RequestMethod.POST)
	public ModelAndView changeAdMessageStatus(Principal user,
						@Valid @ModelAttribute("adStatusMessage") AdStatusMessage adStatusMessage,
						BindingResult result,
						RedirectAttributes attr){
		if(result.hasErrors()){
			attr.addFlashAttribute(
					"org.springframework.validation.BindingResult.adStatusMessage", result);
			attr.addFlashAttribute("adStatusMessage", adStatusMessage);
			return new ModelAndView("redirect:/bulbular/changeMsgs?adStatus="+
										adStatusMessage.getAdStatus()+
										"&translateType="+adStatusMessage.getTranslateType());
		}
		
		try {
			adStatusMessageService.updateAdStatusMessage(adStatusMessage);
			attr.addFlashAttribute("success", 
					"Messages for Advertisements with status="+
							adStatusMessage.getAdStatus()+" and translate type="+
							adStatusMessage.getTranslateType()+" are"
							+ " successfully updated");
			return new ModelAndView("redirect:/bulbular/admsgs");
		} catch (InvalidIdentifier e) {
			attr.addFlashAttribute("error", 
					"AdStatusMessagw with AdStatus="+adStatusMessage.getAdStatus()+" and "
							+ " TranslateType = "+adStatusMessage.getTranslateType()+"is"
							+ " undefined");
			return new ModelAndView("redirect:/bulbular/admsgs");
		}
		
	}
	
}

package ua.translate.controller.admin;

import java.security.Principal;
import java.util.LinkedHashSet;
import java.util.Set;

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

import ua.translate.model.Order;
import ua.translate.model.UserEntity;
import ua.translate.model.ad.Ad;
import ua.translate.model.ad.AdStatusMessage;
import ua.translate.model.ad.WrittenAd;
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
public class ActionsWithAdAdminController {

	@Autowired
	private WrittenAdService writtenAdService;
	
	private static int NOT_CHECKED_ADS_ON_PAGE = 3;
	
	
	/**
	 * Returns page with {@link Ad}s, which have NOTCHECKED status, ordered by asc order by 
	 * {@link Ad#getPublicationDateTime()}
	 * @param user
	 * @param page - number of page
	 */
	@RequestMapping(value = "/notCheckedAds", method = RequestMethod.GET)
	public ModelAndView getNotCheckedAds(Principal user,
							   @RequestParam(name="page",defaultValue="1",required=false) 
								int page){
		Set<WrittenAd> notCheckedAds = null;
		try {
			notCheckedAds = writtenAdService.getWrittenAdsByStatusAndOrder
					(page,NOT_CHECKED_ADS_ON_PAGE,AdStatus.NOTCHECKED,Order.DESC);
		} catch (WrongPageNumber e) {
			try {
				notCheckedAds = writtenAdService.getWrittenAdsByStatusAndOrder
						(1,NOT_CHECKED_ADS_ON_PAGE,AdStatus.NOTCHECKED,Order.DESC);
			} catch (WrongPageNumber unused) {}	
		}
		
		long numberOfPages= writtenAdService.
				getNumberOfPagesForWrittenAdsByStatus(AdStatus.NOTCHECKED,NOT_CHECKED_ADS_ON_PAGE);

		ModelAndView model = new ModelAndView("/admin/notCheckedAds");
		model.addObject("notCheckedAds", notCheckedAds);
		model.addObject("numberOfPages",numberOfPages);
		return model;
	}
	
	@RequestMapping(value = "/markAsChecked",method = RequestMethod.POST)
	public ModelAndView markAsChecked(Principal user,
						@RequestParam(name="adId",required = true) long adId,
						RedirectAttributes attr){
		try {
			writtenAdService.markAsChecked(user.getName(), adId);
			attr.addFlashAttribute("success", "Status is successfully changed to CHECKED");
			return new ModelAndView("redirect:/bulbular/notCheckedAds");
		} catch (InvalidIdentifier | IllegalActionForAd | AccessDeniedException e) {
			attr.addFlashAttribute("error", "Error:"+e.getClass());
			return new ModelAndView("redirect:/bulbular/notCheckedAds");
		}
	}
	
	@RequestMapping(value = "/sendForRework",method = RequestMethod.POST)
	public ModelAndView sendForRework(Principal user,
						@RequestParam(name="adId",required = true) long adId,
						@RequestParam(name="message",required = true) String messageForTranslator,
						RedirectAttributes attr){
		try {
			writtenAdService.markForRework(user.getName(), adId, messageForTranslator);
			attr.addFlashAttribute("success", "Status is successfully changed to REWORKING");
		} catch (InvalidIdentifier | IllegalActionForAd | AccessDeniedException e) {
			attr.addFlashAttribute("error", "Error:"+e.getClass());
		}
		return new ModelAndView("redirect:/bulbular/notCheckedAds");
	}
	

	
}

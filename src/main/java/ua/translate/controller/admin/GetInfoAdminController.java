package ua.translate.controller.admin;

import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import ua.translate.model.Order;
import ua.translate.model.ad.Ad;
import ua.translate.model.ad.AdStatusMessage;
import ua.translate.model.ad.ArchievedAd;
import ua.translate.model.ad.OralAd;
import ua.translate.model.ad.WrittenAd;
import ua.translate.model.settings.Settings;
import ua.translate.model.status.AdStatus;
import ua.translate.model.viewbean.SearchAdByStatus;
import ua.translate.service.AdStatusMessageService;
import ua.translate.service.ArchievedAdService;
import ua.translate.service.OralAdService;
import ua.translate.service.SettingsService;
import ua.translate.service.WrittenAdService;
import ua.translate.service.exception.InvalidIdentifier;
import ua.translate.service.exception.WrongPageNumber;

@Controller
@RequestMapping("/bulbular")
public class GetInfoAdminController {
	
	@Autowired
	private WrittenAdService writtenAdService;
	
	@Autowired
	private OralAdService oralAdService;
	
	@Autowired
	private ArchievedAdService archievedAdService;
	
	@Autowired
	private SettingsService settingsService;
	
	@RequestMapping(value = "/adminPage", method = RequestMethod.GET)
	public ModelAndView getAdminPage(){
		return new ModelAndView("/admin/adminPage");
	}
	@RequestMapping(value = "/login", method=RequestMethod.GET)
	public ModelAndView loginForm(){
		return new ModelAndView("/admin/bulbular");
	}
	
	@RequestMapping(value = "/adsw", method = RequestMethod.GET)
	public ModelAndView getWrittenAdsByStatus(
								@RequestParam(name="page",defaultValue="1") 
								int page,
								@Valid @ModelAttribute("search") 
									SearchAdByStatus searchAdByStatus,
								BindingResult result){
		final ModelAndView model = new ModelAndView("/admin/writtenAdsByStatus");
		if(result.hasErrors()){
			//if user invokes this method first time
			searchAdByStatus = new SearchAdByStatus();
			model.addObject("search", searchAdByStatus);
			model.addObject("adStatuses",getAdStatusesForSelect());
			model.addObject("numberOfPages",0);
			return model;
		}
		
		long numberOfPages = 0;
		Set<WrittenAd> ads = null;
		
		Settings settings = settingsService.getProjectSettings();
		final int adsOnPage = settings.getMaxNumberOfAdsOnOnePage();
		
		numberOfPages = writtenAdService.getNumberOfPagesForWrittenAdsByStatus
			(searchAdByStatus.getAdStatus(), adsOnPage);
		try {
			ads = writtenAdService.getWrittenAdsByStatusAndOrder
					(page, adsOnPage, searchAdByStatus.getAdStatus(), Order.DESC);
		} catch (WrongPageNumber e) {
			try {
				ads = writtenAdService.getWrittenAdsByStatusAndOrder
						(1,adsOnPage,searchAdByStatus.getAdStatus(), Order.DESC);
			} catch (WrongPageNumber unused) {}	
		}
			
		model.addObject("ads", ads);
		model.addObject("numberOfPages",numberOfPages);
		model.addObject("adStatuses",getAdStatusesForSelect());
		return model;
	}
	
	@RequestMapping(value = "/adso", method = RequestMethod.GET)
	public ModelAndView getOralAdsByStatus(
								@RequestParam(name="page",defaultValue="1") 
								int page,
								@Valid @ModelAttribute("search") 
									SearchAdByStatus searchAdByStatus,
								BindingResult result){
		final ModelAndView model = new ModelAndView("/admin/oralAdsByStatus");
		if(result.hasErrors()){
			//if user invokes this method first time
			searchAdByStatus = new SearchAdByStatus();
			model.addObject("search", searchAdByStatus);
			model.addObject("adStatuses",getAdStatusesForSelect());
			model.addObject("numberOfPages",0);
			return model;
		}
		
		long numberOfPages = 0;
		Set<OralAd> ads = null;
		
		Settings settings = settingsService.getProjectSettings();
		final int adsOnPage = settings.getMaxNumberOfAdsOnOnePage();
		
		numberOfPages = oralAdService.getNumberOfPagesForOralAdsByStatus
			(searchAdByStatus.getAdStatus(), adsOnPage);
		try {
			ads = oralAdService.getOralAdsByStatusAndOrder
					(page, adsOnPage, searchAdByStatus.getAdStatus(), Order.DESC);
		} catch (WrongPageNumber e) {
			try {
				ads = oralAdService.getOralAdsByStatusAndOrder
						(1,adsOnPage,searchAdByStatus.getAdStatus(), Order.DESC);
			} catch (WrongPageNumber unused) {}	
		}
			
		model.addObject("ads", ads);
		model.addObject("numberOfPages",numberOfPages);
		model.addObject("adStatuses",getAdStatusesForSelect());
		return model;
	}
	
	
	@RequestMapping(value = "/archieve",method = RequestMethod.GET)
	public ModelAndView getArchievedAds(){
		Set<ArchievedAd> archievedAds = 
				archievedAdService.getAllArchievedAdsInDescOrder();
		ModelAndView model = new ModelAndView("/admin/archievedAds");
		model.addObject("archievedAds", archievedAds);
		return model;
	
	}
	
	/**
	 * Returns Map, where keys and values - String representations
	 * of all {@link AdStatus} values
	 */
	private Map<String,String> getAdStatusesForSelect(){
		AdStatus[] statuses = AdStatus.values();
		Map<String,String> statusesMap = new LinkedHashMap<>();
		for(AdStatus status:statuses){
			statusesMap.put(status.name(),status.name());
		}
		return statusesMap;
	}
	
}

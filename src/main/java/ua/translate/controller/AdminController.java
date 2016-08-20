package ua.translate.controller;

import java.security.Principal;
import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import ua.translate.model.User;
import ua.translate.model.ad.Ad;
import ua.translate.model.status.AdStatus;
import ua.translate.model.viewbean.AdView;
import ua.translate.service.AdService;
import ua.translate.service.exception.WrongPageNumber;

@Controller
@RequestMapping("/bulbular")
public class AdminController {

	@Autowired
	private AdService adService;
	
	private static int NOT_CHECKED_ADS_ON_PAGE = 3;
	
	@RequestMapping(value = "/adminPage", method = RequestMethod.GET)
	public ModelAndView getAdminPage(){
		return new ModelAndView("/admin/adminPage");
	}
	@RequestMapping(value = "/login", method=RequestMethod.GET)
	public ModelAndView getLoginForm(){
		return new ModelAndView("/admin/bulbular");
	}
	
	/**
	 * Returns page with {@link Ad}s, which have NOTCHECKED status, ordered by asc order by 
	 * {@link Ad#getPublicationDateTime()}
	 * @param user
	 * @param page - number of page
	 */
	@RequestMapping(value = "notCheckedAds", method = RequestMethod.GET)
	public ModelAndView getNotChechedAds(Principal user,
							   @RequestParam(name="page",defaultValue="1",required=false) 
								int page){
		Set<Ad> notCheckedAds = null;
		try {
			notCheckedAds = adService.getAdsForChecking(page,NOT_CHECKED_ADS_ON_PAGE);
		} catch (WrongPageNumber e) {
			try {
				notCheckedAds = adService.getAdsForChecking(1, NOT_CHECKED_ADS_ON_PAGE);
			} catch (WrongPageNumber unused) {}	
		}
		
		long numberOfPages= adService.
				getNumberOfPagesForAdsByStatus(AdStatus.NOTCHECKED,NOT_CHECKED_ADS_ON_PAGE);

		ModelAndView model = new ModelAndView("/admin/notCheckedAds");
		model.addObject("notCheckedAds", notCheckedAds);
		model.addObject("numberOfPages",numberOfPages);
		return model;
		
	}
	
}

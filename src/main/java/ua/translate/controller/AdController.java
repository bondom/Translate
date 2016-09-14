package ua.translate.controller;

import java.security.Principal;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import ua.translate.controller.support.RespondedAdComparatorByDate;
import ua.translate.controller.support.ControllerHelper;
import ua.translate.model.Translator;
import ua.translate.model.User;
import ua.translate.model.ad.Ad;
import ua.translate.model.ad.RespondedAd;
import ua.translate.model.status.AdStatus;
import ua.translate.model.viewbean.AdView;
import ua.translate.model.viewbean.SearchFilterForAds;
import ua.translate.service.AdService;
import ua.translate.service.TranslatorService;
import ua.translate.service.exception.NonExistedAdException;
import ua.translate.service.exception.IllegalActionForAcceptedAd;
import ua.translate.service.exception.WrongPageNumber;

@Controller
@RequestMapping("/ads")
public class AdController extends UserController {
	
	/**
	 * Represents number of ads, which can be displayed on one page
	 */
	private static final int ADS_ON_PAGE=6;
	
	/**
	 * String representation of not chosen option in selected tag,
	 * that is no filter for property is chosen
	 */
	private static final String OPTION_NOT_CHOSEN = "All";
	
	private static Logger logger = LoggerFactory.getLogger(AdController.class);
	
	@Value("${webRootPath}")
	private String webRootPath;
	
	@Autowired
	AdService adService;
	
	@Autowired
	TranslatorService translatorService;
	
	@Autowired
	ControllerHelper controllerHelper;
	
	@RequestMapping(value = "/{adId}", method = RequestMethod.GET)
	public ModelAndView ad(@PathVariable("adId") long adId){
		try {
			Ad ad = adService.getForShowing(adId);
			ModelAndView model = new ModelAndView("/showedAd");
			model.addObject("ad", ad);
			return model;
		}catch (NonExistedAdException |IllegalActionForAcceptedAd e) {
			ModelAndView model = new ModelAndView("/exception/invalidAdId");
			model.addObject("errorUrl", webRootPath+"/ads/"+adId);
			return model;
		}
	}
	
	/**
	 * Returns page with {@link Ad}s, which have SHOWED status, ordered by desc order by 
	 * {@link Ad#getPublicationDateTime()}.
	 * User can use filter for getting Ad with some specific parameters.
	 * <p>Properties of {@link SearchFilterForAds} {@code searchBean} represent this filter
	 * @param user
	 * @param page - number of page
	 */
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView ads(Principal user,
							   @RequestParam(name="page",defaultValue="1",required=false) 
								int page,
							   @Valid @ModelAttribute("search") SearchFilterForAds searchBean,
							   BindingResult result){
		final ModelAndView model = new ModelAndView("/publicAds");
		boolean extendedSearch = true;
		if(result.hasErrors()){
			//if user invokes this method first time
			extendedSearch = false;
		}
		if(extendedSearch &&
				!languagesAreDifferent(searchBean.getInitLanguage(),
									  searchBean.getResultLanguage(),
									  OPTION_NOT_CHOSEN)){
			//reset extendedSearch, because filter is wrong
			extendedSearch = false;
			model.addObject("error", "Languages must be different");
		}
		
		Set<Ad> ads = null;
		long numberOfPages = 0;
		if(extendedSearch){
			//search with chosen user's parameters
			try {
				numberOfPages = adService
						.getNumberOfPagesForAdsByStatusAndFilter
							(AdStatus.SHOWED, ADS_ON_PAGE, searchBean, OPTION_NOT_CHOSEN);
				ads = adService.getAdsForShowingByFilter(
						page, ADS_ON_PAGE, searchBean,OPTION_NOT_CHOSEN);
				
			}catch(WrongPageNumber e) {
				try {
					ads = adService.getAdsForShowingByFilter(
						1, ADS_ON_PAGE, searchBean,OPTION_NOT_CHOSEN);
				}catch (WrongPageNumber unused) {}	
			}catch(IllegalArgumentException e){
				logger.error("{}:{}",e.getClass(),e.getMessage());
				return new ModelAndView("redirect:/ads");
			}
		}else{
			//simple search without filters
			numberOfPages = adService
					.getNumberOfPagesForAdsByStatus(AdStatus.SHOWED,ADS_ON_PAGE);
			try {
				ads = adService.getAdsForShowing(page,ADS_ON_PAGE);
			} catch (WrongPageNumber e) {
				try {
					ads = adService.getAdsForShowing(1, ADS_ON_PAGE);
				} catch (WrongPageNumber unused) {}	
			}
		}
		
		//Creating Set of AdView objects for rendering ad, publication date and  - 
		//message with responding time, if user is translator, which responded on some ad
		Set<AdView> adsForRendering = new LinkedHashSet<>();
		
		ads.forEach(ad->{
			User userFromDB = null;
			if(user!=null){
				userFromDB = translatorService.getUserByEmail(user.getName());
			}
			AdView adView = getAdViewForRendering(userFromDB, ad);
			adsForRendering.add(adView);
		});
		
		model.addObject("adsView", adsForRendering);
		model.addObject("numberOfPages",numberOfPages);
		
		//adding search bean
		if (!extendedSearch) {
			SearchFilterForAds search = 
					new SearchFilterForAds(OPTION_NOT_CHOSEN,OPTION_NOT_CHOSEN,
							OPTION_NOT_CHOSEN,OPTION_NOT_CHOSEN,OPTION_NOT_CHOSEN,
							OPTION_NOT_CHOSEN, 0, 0);
			model.addObject("search",search);
	    }else{
	    	model.addObject("search",searchBean);
	    }
		
		Map<String,String> languages = getLanguagesForSelect();
		Map<String,String> currencies= getCurrenciesForSelect();
		Map<String,String> translateTypes= getTranslateTypesForSelect();
		Map<String,String> countries = getCountriesForSelect();
		Map<String,String> cities = getCitiesForSelect();
		
		//adding default values
		languages.put(OPTION_NOT_CHOSEN,"All");
		currencies.put(OPTION_NOT_CHOSEN,"All");
		translateTypes.put(OPTION_NOT_CHOSEN, "All");
		countries.put(OPTION_NOT_CHOSEN, "All");
		cities.put(OPTION_NOT_CHOSEN, "All");
		
		//adding collections for selected tag
		model.addObject("languages", new TreeMap<>(languages));
		model.addObject("currencies", new TreeMap<>(currencies));
		model.addObject("translateTypes", new TreeMap<>(translateTypes));
		model.addObject("countries", new TreeMap<>(countries));
		model.addObject("cities", new TreeMap<>(cities));
		return model;
		
	}
	
	
	/**
	 * Returns {@link AdView} {@code adView} for rendering on page<p>
	 * If {@link User}, requested page with advertisements is {@link Translator}, and he responded on this {@code ad} ever,
	 * this method returns {@code adView} with setted {@code adView.respondingTime} field,
	 * else that field is {@code null}
	 * 
	 * @param userFromDB - {@code User}, retrieved from data storage
	 * @param ad - {@link Ad}, which should be rendered
	 */
	private AdView getAdViewForRendering(User userFromDB,Ad ad){
		AdView adView;
		String relativePublishingTime = controllerHelper.
				getStringRelativeTime(ad.getPublicationDateTime());
		if(userFromDB!=null && 
				userFromDB instanceof Translator){
			Set<RespondedAd> respondedAds = ad.getRespondedAds();
			
			//Translator can response on one Ad several times,
			//and we must show latest response on this Ad
			Set<RespondedAd> sortedRespondedAds = new TreeSet<>(new RespondedAdComparatorByDate());	
			sortedRespondedAds.addAll(respondedAds);

			Optional<RespondedAd> optionalRespondedAd= 
					sortedRespondedAds.stream()
								.filter(rad -> rad.getTranslator().equals(userFromDB))
								.findFirst();
			if(optionalRespondedAd.isPresent()){
				logger.debug("User, who requested page with all advertisements,"
						+ "is Translator, and he responded on Ad with name '{}'",ad.getName());
				RespondedAd responsedByAuthTranslator = optionalRespondedAd.get();
				adView = new AdView(
						ad,relativePublishingTime,
						responsedByAuthTranslator.getDateTimeOfResponse());
			}else{
				logger.debug("User, who requested page with all advertisements,"
						+ "is Translator, but he never responded on Ad with name '{}'",ad.getName());
				adView = new AdView(ad, relativePublishingTime);
			}
			
		}else{
			logger.debug("User, who requested page with all advertisements, is not Translator");
			adView = new AdView(ad, relativePublishingTime);
		}
		return adView;
	}
	
	/**
	 * Checks if {@code initLangauge} doesn't equal to {@code resultLanguage},
	 * except cases when at least one of them equals to {@code noLanguage} or to {@code null}
	 * @param noLanguage - String ,which represents situation, when user chose no language
	 */
	private boolean languagesAreDifferent(String initLanguage,
										  String resultLanguage,
										  String noLanguage){
		if(initLanguage==null || resultLanguage==null){
			return true;
		}
		
		if(initLanguage.equals("")|| resultLanguage.equals("")){
			return true;
		}
		
		if(initLanguage.equals(noLanguage) || resultLanguage.equals(noLanguage)){
			return true;
		}
		if(initLanguage.equals(resultLanguage)){
			return false;
		}
		return true;
	}
}

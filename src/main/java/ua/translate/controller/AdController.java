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
import org.springframework.beans.factory.annotation.Qualifier;
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
import ua.translate.model.Order;
import ua.translate.model.Translator;
import ua.translate.model.UserEntity;
import ua.translate.model.ad.Ad;
import ua.translate.model.ad.Ad.TranslateType;
import ua.translate.model.ad.OralAd;
import ua.translate.model.ad.RespondedAd;
import ua.translate.model.ad.WrittenAd;
import ua.translate.model.settings.Settings;
import ua.translate.model.status.AdStatus;
import ua.translate.model.viewbean.AdView;
import ua.translate.model.viewbean.SearchFilterForAds;
import ua.translate.model.viewbean.SearchFilterForOralAds;
import ua.translate.model.viewbean.SearchFilterForWrittenAds;
import ua.translate.service.AbstractAdService;
import ua.translate.service.AdService;
import ua.translate.service.OralAdService;
import ua.translate.service.SettingsService;
import ua.translate.service.TranslatorService;
import ua.translate.service.WrittenAdService;
import ua.translate.service.exception.IllegalActionForAd;
import ua.translate.service.exception.InvalidIdentifier;
import ua.translate.service.exception.WrongPageNumber;

@Controller
public class AdController extends UserController {
	
	
	/**
	 * String representation of not chosen option in selected tag,
	 * that is no filter for property is chosen
	 */
	private static final String OPTION_NOT_CHOSEN = "All";
	
	private static Logger logger = LoggerFactory.getLogger(AdController.class);
	
	@Value("${webRootPath}")
	private String webRootPath;
	
	@Autowired
	private OralAdService oralAdService;
	
	@Autowired
	private WrittenAdService writtenAdService;
	
	@Autowired
	@Qualifier("defaultImpl")
	private AdService adService;
	
	@Autowired
	private TranslatorService translatorService;
	
	@Autowired
	private ControllerHelper controllerHelper;
	
	@Autowired
	private SettingsService settingsService;
	
	@RequestMapping(value = "/ads/{adId}", method = RequestMethod.GET)
	public ModelAndView ad(@PathVariable("adId") long adId){
		try {
			Ad ad = adService.get(adId);
			if(TranslateType.ORAL.equals(ad.getTranslateType())){
				OralAd adForRendering = (OralAd)ad;
				ModelAndView model = new ModelAndView("/oralAd");
				model.addObject("ad", adForRendering);
				return model;
			}
			
			if(TranslateType.WRITTEN.equals(ad.getTranslateType())){
				WrittenAd adForRendering = (WrittenAd)ad;
				ModelAndView model = new ModelAndView("/writtenAd");
				model.addObject("ad", adForRendering);
				return model;
			}
			//unacceptable situation
			logger.error("Ad with id={} exists in data storage, but its"
					+ " translateType={}",adId,ad.getTranslateType()); 
			return new ModelAndView("/adsw");
			
		}catch (InvalidIdentifier e) {
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
	@RequestMapping(value = "/adso",method = RequestMethod.GET)
	public ModelAndView oralAds(Principal user,
							   @RequestParam(name="page",defaultValue="1",required=false) 
								int page,
							   @Valid @ModelAttribute("search") SearchFilterForOralAds searchBean,
							   BindingResult result){
		final ModelAndView model = new ModelAndView("/oralAds");
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
		
		Set<OralAd> ads = null;
		long numberOfPages = 0;
		
		Settings settings = settingsService.getProjectSettings();
		final int adsOnPage = settings.getMaxNumberOfAdsOnOnePage();
		
		if(extendedSearch){
			//search with chosen user's parameters
			try {
				numberOfPages = oralAdService
						.getNumberOfPagesForOralAdsByStatusAndFilter
							(AdStatus.SHOWED, adsOnPage, searchBean, OPTION_NOT_CHOSEN);
				ads = oralAdService.getOralAdsForShowingByFilter(
						page, adsOnPage, searchBean,OPTION_NOT_CHOSEN);
				
			}catch(WrongPageNumber e) {
				try {
					ads = oralAdService.getOralAdsForShowingByFilter(
						1, adsOnPage, searchBean,OPTION_NOT_CHOSEN);
				}catch (WrongPageNumber unused) {}	
			}catch(IllegalArgumentException e){
				logger.error("{}:{}",e.getClass(),e.getMessage());
				return new ModelAndView("redirect:/ads");
			}
		}else{
			//simple search without filters
			numberOfPages = oralAdService
					.getNumberOfPagesForOralAdsByStatus(AdStatus.SHOWED,adsOnPage);
			try {
				ads = oralAdService.getOralAdsByStatusAndOrder
								(page,adsOnPage,AdStatus.SHOWED, Order.DESC);
			} catch (WrongPageNumber e) {
				try {
					ads = oralAdService.getOralAdsByStatusAndOrder
							(1,adsOnPage,AdStatus.SHOWED, Order.DESC);
				} catch (WrongPageNumber unused) {}	
			}
		}
		
		//Creating Set of AdView objects for rendering ad, publication date and  - 
		//message with responding time, if user is translator, which responded on some ad
		Set<AdView> adsForRendering = new LinkedHashSet<>();
		
		ads.forEach(ad->{
			UserEntity userFromDB = null;
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
					new SearchFilterForOralAds(OPTION_NOT_CHOSEN,
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
	 * Returns page with {@link Ad}s, which have SHOWED status, ordered by desc order by 
	 * {@link Ad#getPublicationDateTime()}.
	 * User can use filter for getting Ad with some specific parameters.
	 * <p>Properties of {@link SearchFilterForAds} {@code searchBean} represent this filter
	 * @param user
	 * @param page - number of page
	 */
	@RequestMapping(value = "/adsw",method = RequestMethod.GET)
	public ModelAndView writtenAds(Principal user,
							   @RequestParam(name="page",defaultValue="1") 
								int page,
							   @Valid @ModelAttribute("search") SearchFilterForWrittenAds searchBean,
							   BindingResult result){
		final ModelAndView model = new ModelAndView("/writtenAds");
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
		
		
		Set<WrittenAd> ads = null;
		long numberOfPages = 0;
		Settings settings = settingsService.getProjectSettings();
		final int adsOnPage = settings.getMaxNumberOfAdsOnOnePage();
		
		if(extendedSearch){
			//search with chosen user's parameters
			try {
				numberOfPages = writtenAdService
						.getNumberOfPagesForWrittenAdsByStatusAndFilter
							(AdStatus.SHOWED, adsOnPage, searchBean, OPTION_NOT_CHOSEN);
				ads = writtenAdService.getWrittenAdsForShowingByFilter(
						page, adsOnPage, searchBean,OPTION_NOT_CHOSEN);
				
			}catch(WrongPageNumber e) {
				try {
					ads = writtenAdService.getWrittenAdsForShowingByFilter(
						1, adsOnPage, searchBean,OPTION_NOT_CHOSEN);
				}catch (WrongPageNumber unused) {}	
			}catch(IllegalArgumentException e){
				logger.error("{}:{}",e.getClass(),e.getMessage());
				return new ModelAndView("redirect:/ads");
			}
		}else{
			//simple search without filters
			numberOfPages = writtenAdService
					.getNumberOfPagesForWrittenAdsByStatus(AdStatus.SHOWED,adsOnPage);
			try {
				ads = writtenAdService.getWrittenAdsByStatusAndOrder
								(page,adsOnPage,AdStatus.SHOWED, Order.DESC);
			} catch (WrongPageNumber e) {
				try {
					ads = writtenAdService.getWrittenAdsByStatusAndOrder
							(1,adsOnPage,AdStatus.SHOWED, Order.DESC);
				} catch (WrongPageNumber unused) {}	
			}
		}
		
		//Creating Set of AdView objects for rendering ad, publication date and  - 
		//message with responding time, if user is translator, which responded on some ad
		Set<AdView> adsForRendering = new LinkedHashSet<>();
		
		ads.forEach(ad->{
			UserEntity userFromDB = null;
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
			SearchFilterForWrittenAds search = 
					new SearchFilterForWrittenAds(OPTION_NOT_CHOSEN,
							OPTION_NOT_CHOSEN,OPTION_NOT_CHOSEN,0, 0);
			model.addObject("search",search);
	    }else{
	    	model.addObject("search",searchBean);
	    }
		
		Map<String,String> languages = getLanguagesForSelect();
		Map<String,String> currencies= getCurrenciesForSelect();
		Map<String,String> translateTypes= getTranslateTypesForSelect();
		
		//adding default values
		languages.put(OPTION_NOT_CHOSEN,"All");
		currencies.put(OPTION_NOT_CHOSEN,"All");
		translateTypes.put(OPTION_NOT_CHOSEN, "All");
		
		//adding collections for selected tag
		model.addObject("languages", new TreeMap<>(languages));
		model.addObject("currencies", new TreeMap<>(currencies));
		model.addObject("translateTypes", new TreeMap<>(translateTypes));
		return model;
		
	}
	
	/**
	 * Returns {@link AdView} {@code adView} for rendering on page<p>
	 * If {@link UserEntity}, requested page with advertisements is {@link Translator}, and he responded on this {@code ad} ever,
	 * this method returns {@code adView} with setted {@code adView.respondingTime} field,
	 * else that field is {@code null}
	 * 
	 * @param userFromDB - {@code User}, retrieved from data storage
	 * @param ad - {@link Ad}, which should be rendered
	 */
	private AdView getAdViewForRendering(UserEntity userFromDB,Ad ad){
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

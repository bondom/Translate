package ua.translate.controller;

import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import ua.translate.controller.support.AdComparatorByDate;
import ua.translate.controller.support.ResponsedAdComparatorByDate;
import ua.translate.controller.support.ControllerHelper;
import ua.translate.model.Translator;
import ua.translate.model.User;
import ua.translate.model.ad.Ad;
import ua.translate.model.ad.ResponsedAd;
import ua.translate.model.viewbean.AdView;
import ua.translate.service.AdService;
import ua.translate.service.TranslatorService;
import ua.translate.service.exception.NonExistedAdException;
import ua.translate.service.exception.UnacceptableActionForAcceptedAd;

@Controller
@RequestMapping("/ads")
public class AdController {
	
	
	private static Logger logger = LoggerFactory.getLogger(AdController.class);
	
	@Value("${webRootPath}")
	private String webRootPath;
	
	@Autowired
	AdService adService;
	
	@Autowired
	TranslatorService translatorService;
	
	@RequestMapping(value = "/{adId}", method = RequestMethod.GET)
	public ModelAndView ad(@PathVariable("adId") long adId){
		try {
			Ad ad = adService.getForShowing(adId);
			ModelAndView model = new ModelAndView("/showedAd");
			LocalDate creationDate = ad.getPublicationDateTime().toLocalDate();
			
			model.addObject("ad", ad);
			model.addObject("creationDate",creationDate);
			return model;
		}catch (NonExistedAdException |UnacceptableActionForAcceptedAd e) {
			ModelAndView model = new ModelAndView("/exception/invalidAdId");
			model.addObject("errorUrl", webRootPath+"/ads/"+adId);
			return model;
		}
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView allAds(Principal user){

		Set<Ad> ads = adService.getAdsForShowing();
		Set<AdView> adsForRendering = new TreeSet<>(new AdComparatorByDate());
		
		
		ads.forEach(ad->{
			User userFromDB = null;
			if(user!=null){
				userFromDB = translatorService.getUserByEmail(user.getName());
			}
			AdView adView = getAdViewForRendering(userFromDB, ad);
			adsForRendering.add(adView);
		});
		
		ModelAndView model = new ModelAndView("/publicAds");
		model.addObject("adsView", adsForRendering);
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
		String relativePublishingTime = ControllerHelper.
				getStringRelativeTime(ad.getPublicationDateTime());
		if(userFromDB!=null && 
				userFromDB instanceof Translator){
			Set<ResponsedAd> responsedAds = ad.getResponsedAds();
			
			//Translator can response on one Ad several times,
			//and we must show latest response on this Ad
			Set<ResponsedAd> sortedResponsedAds = new TreeSet<>(new ResponsedAdComparatorByDate());	
			sortedResponsedAds.addAll(responsedAds);

			Optional<ResponsedAd> optionalResponsedAd= 
					sortedResponsedAds.stream()
								.filter(rad -> rad.getTranslator().equals(userFromDB))
								.findFirst();
			if(optionalResponsedAd.isPresent()){
				logger.debug("User, who requested page with all advertisements,"
						+ "is Translator, and he responded on Ad with name '{}'",ad.getName());
				ResponsedAd responsedByAuthTranslator = optionalResponsedAd.get();
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
	
	
	
}

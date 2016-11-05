package ua.translate.service.impl;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ua.translate.dao.ClientDao;
import ua.translate.dao.OralAdDao;
import ua.translate.model.Client;
import ua.translate.model.Language;
import ua.translate.model.Order;
import ua.translate.model.Translator;
import ua.translate.model.ad.Ad;
import ua.translate.model.ad.Ad.TranslateType;
import ua.translate.model.ad.Currency;
import ua.translate.model.ad.OralAd;
import ua.translate.model.ad.RespondedAd;
import ua.translate.model.ad.WrittenAd;
import ua.translate.model.searchbean.SearchOralAdBean;
import ua.translate.model.status.AdStatus;
import ua.translate.model.status.RespondedAdStatus;
import ua.translate.model.viewbean.SearchFilterForOralAds;
import ua.translate.service.exception.DuplicateAdException;
import ua.translate.service.exception.IllegalActionForAd;
import ua.translate.service.exception.InsufficientFunds;
import ua.translate.service.exception.InvalidIdentifier;
import ua.translate.service.exception.TooEarlyPaying;
import ua.translate.service.BalanceService;
import ua.translate.service.OralAdService;
import ua.translate.service.exception.WrongPageNumber;

@Service
@Transactional(propagation = Propagation.REQUIRED)
public class OralAdServiceImpl extends OralAdService {
	
	private static Logger logger = LoggerFactory.getLogger(OralAdServiceImpl.class);
	
	@Autowired
	private ClientDao clientDao;
	
	@Autowired
	private OralAdDao adDao;
	
	@Autowired
	private BalanceService balanceService;
	
	/**
	 * It is default number of ads on one page
	 */
	private static final int DEFAULT_NUMBER_ADS_ON_PAGE=3;
	
	private static final int DEFAULT_REST_PERCENT = 100-RespondedAdServiceImpl.DEFAULT_PLEDGE;
	
	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public OralAd updateOralAd(String email, OralAd updatedAd)
												throws InvalidIdentifier, 
													   IllegalActionForAd,
													   DuplicateAdException{
		OralAd adFromDb= adDao.getOralAdById(updatedAd.getId());
		if(adFromDb==null){
			throw new InvalidIdentifier();
		}
		
		Client authClient = clientDao.getClientByEmail(email);
		if(!adFromDb.getClient().equals(authClient)){
			throw new InvalidIdentifier();
		}
		if(!adFromDb.getStatus().equals(AdStatus.SHOWED)){
			throw new IllegalActionForAd("Attempting to update OralAd without SHOWED status");
		}
		if(adContainsSendedResponses(adFromDb)){
			throw new IllegalActionForAd("Attempting to update OralAd with sended RespondedAds");
		}
		
		updatedAd.setClient(adFromDb.getClient());
		updatedAd.setPublicationDateTime(adFromDb.getPublicationDateTime());
		updatedAd.setStatus(AdStatus.SHOWED);
		updatedAd.setRespondedAds(adFromDb.getRespondedAds());
		
		if(!isAdOriginal(updatedAd, authClient)){
			throw new DuplicateAdException();
		}
		OralAd persistedAd = (OralAd) adDao.merge(updatedAd);
		
		return persistedAd;
	}
	
	@Override
	public Set<OralAd> getOralAdsByStatusAndOrder(int page,int numberAdsOnPage,
			AdStatus adStatus, Order order) throws WrongPageNumber{
		if(page<1){
			throw new WrongPageNumber();
		}
		if(numberAdsOnPage<1){
			//default value is used
			logger.debug("numberAdsOnPage={} less then 1, default value is used={}",numberAdsOnPage,
					DEFAULT_NUMBER_ADS_ON_PAGE);
			numberAdsOnPage = DEFAULT_NUMBER_ADS_ON_PAGE;
		}
		Set<Ad> ads = adDao.
				getAdsByTranslateTypeAndStatusAndOrder(page, 
													   numberAdsOnPage, 
													   TranslateType.ORAL, 
													   adStatus, order);
		Set<OralAd> oralAds = new LinkedHashSet<>();
		ads.forEach(ad ->{
			oralAds.add((OralAd)ad);
		});
		return oralAds;
	}
	

	
	@Override
	public Set<OralAd> getOralAdsForShowingByFilter(int page, int numberAdsOnPage,
			SearchFilterForOralAds searchFilter,String valueWithoutFilter)
			throws WrongPageNumber {
		if(page<1){
			throw new WrongPageNumber();
		}
		if(numberAdsOnPage<1){
			//default value is used
			logger.debug("numberAdsOnPage={} less then 1, default value is used={}",numberAdsOnPage,
					DEFAULT_NUMBER_ADS_ON_PAGE);
			numberAdsOnPage = DEFAULT_NUMBER_ADS_ON_PAGE;
		}
		String countryValue = searchFilter.getCountry();
		String cityValue = searchFilter.getCity();
		String currencyValue = searchFilter.getCurrency();
		String resultLanguageValue = searchFilter.getResultLanguage();
		String initLanguageValue = searchFilter.getInitLanguage();
		int minCostValue= searchFilter.getMinCost();
		int maxCostValue= searchFilter.getMaxCost();
		
		//parameters, which will be transferred to dao layer for making request
		Currency currency = null;
		Language resultLanguage = null;
		Language initLanguage = null;
		String country = null;
		String city = null;
		int minCost = minCostValue;
		int maxCost = maxCostValue;
		
		if(!valueWithoutFilter.equals(currencyValue)){
			currency = Currency.valueOf(currencyValue);
		}
		
		if(!valueWithoutFilter.equals(resultLanguageValue)){
			resultLanguage = Language.valueOf(resultLanguageValue);
		}
		
		if(!valueWithoutFilter.equals(initLanguageValue)){
			initLanguage = Language.valueOf(initLanguageValue);
		}
		
		if(!valueWithoutFilter.equals(countryValue)){
			country = countryValue;
		}
		
		if(!valueWithoutFilter.equals(cityValue)){
			city = cityValue;
		}
		
		SearchOralAdBean searchAdBean = 
				new SearchOralAdBean(currency, resultLanguage, initLanguage, 
								 country, city, minCost, maxCost);
		Set<OralAd> ads = adDao.getFilteredOralAdsForShowing(page, numberAdsOnPage, 
														searchAdBean);
		return ads;
	}
	
	@Override
	public long getNumberOfPagesForOralAdsByStatus(AdStatus adStatus,int numberOfAds) {
		if(numberOfAds<1){
			//default value is used
			logger.debug("numberAdsOnPage={} less then 1, default value is used={}",
					numberOfAds, DEFAULT_NUMBER_ADS_ON_PAGE);
			numberOfAds = DEFAULT_NUMBER_ADS_ON_PAGE;
		}
		long numberOfShowedAds = adDao
				.getNumberOfAdsByStatusAndTranslateType(adStatus, TranslateType.ORAL);
		long numberOfPages = (long) Math.ceil(((double)numberOfShowedAds)/numberOfAds);
		return numberOfPages;
	}
	
	@Override
	public long getNumberOfPagesForOralAdsByStatusAndFilter(AdStatus adStatus, 
														int numberOfAds, 
														SearchFilterForOralAds searchFilter,
														String valueWithoutFilter) {
		if(numberOfAds<1){
			//default value is used
			logger.debug("numberAdsOnPage={} less then 1, default value is used={}",
					numberOfAds, DEFAULT_NUMBER_ADS_ON_PAGE);
			numberOfAds = DEFAULT_NUMBER_ADS_ON_PAGE;
		}
		String countryValue = searchFilter.getCountry();
		String cityValue = searchFilter.getCity();
		String currencyValue = searchFilter.getCurrency();
		String resultLanguageValue = searchFilter.getResultLanguage();
		String initLanguageValue = searchFilter.getInitLanguage();
		int minCostValue= searchFilter.getMinCost();
		int maxCostValue= searchFilter.getMaxCost();
		
		//parameters, which will be transferred to dao layer for making request
		Currency currency = null;
		Language resultLanguage = null;
		Language initLanguage = null;
		String country = null;
		String city = null;
		int minCost = minCostValue;
		int maxCost = maxCostValue;
		
		if(!valueWithoutFilter.equals(currencyValue)){
			currency = Currency.valueOf(currencyValue);
		}
		
		if(!valueWithoutFilter.equals(resultLanguageValue)){
			resultLanguage = Language.valueOf(resultLanguageValue);
		}
		
		if(!valueWithoutFilter.equals(initLanguageValue)){
			initLanguage = Language.valueOf(initLanguageValue);
		}
		
		if(!valueWithoutFilter.equals(countryValue)){
			country = countryValue;
		}
		
		if(!valueWithoutFilter.equals(cityValue)){
			city = cityValue;
		}
		
		SearchOralAdBean searchAdBean = 
				new SearchOralAdBean(currency, resultLanguage, initLanguage, 
								  country, city, minCost, maxCost);
		long numberOfShowedAds = adDao.getNumberOfOralAdsByStatusAndFilter(adStatus, searchAdBean);
		long numberOfPages = (long) Math.ceil(((double)numberOfShowedAds)/numberOfAds);
		return numberOfPages;
	}

	@Override
	public void transferRestPriceAndChangeStatusAndIncrementExecutedAds(String email, long adId, int restPercent)
			throws InvalidIdentifier, IllegalActionForAd, InsufficientFunds,TooEarlyPaying {
		final Client client = clientDao.getClientByEmail(email);
		final OralAd oralAd = adDao.getOralAdById(adId);
		if(oralAd == null || !oralAd.getClient().equals(client)){
			throw new InvalidIdentifier();
		}
		if(!AdStatus.ACCEPTED.equals(oralAd.getStatus())){
			throw new IllegalActionForAd();
		}
		LocalDateTime finish = oralAd.getFinishDateTime();
		LocalDateTime now = LocalDateTime.now();
		if(finish.isAfter(now)){
			throw new TooEarlyPaying();
		}
		Translator translator = oralAd.getTranslator();
		translator.setNumberOfExecutedAds((short) (translator.getNumberOfExecutedAds()+1));
		oralAd.setStatus(AdStatus.PAYED);
		
		
		int amount = 0;
		final double clientBalance = client.getBalance();
		final int adCost = oralAd.getCost();
		if(restPercent>0){
			if((clientBalance - adCost*restPercent/100)<0){
				throw new InsufficientFunds();
			}
		}else{
			if((clientBalance - adCost*DEFAULT_REST_PERCENT/100)>0){
				throw new InsufficientFunds();
			}
		}
		
		if(restPercent>0){
			amount = oralAd.getCost()*restPercent/100;
		}else{
			amount = oralAd.getCost()*DEFAULT_REST_PERCENT/100;
		}
	
		balanceService.transferMoneyFromClientToTranslator(client, translator, amount);
		
		
	}
	

	
}

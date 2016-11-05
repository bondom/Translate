package ua.translate.service.impl;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ua.translate.dao.AdStatusMessageDao;
import ua.translate.dao.ClientDao;
import ua.translate.dao.TranslatorDao;
import ua.translate.dao.UserDao;
import ua.translate.dao.WrittenAdDao;
import ua.translate.model.Client;
import ua.translate.model.Language;
import ua.translate.model.Order;
import ua.translate.model.Translator;
import ua.translate.model.UserEntity;
import ua.translate.model.UserEntity.UserRole;
import ua.translate.model.ad.Ad;
import ua.translate.model.ad.Ad.TranslateType;
import ua.translate.model.ad.AdStatusMessage;
import ua.translate.model.ad.Currency;
import ua.translate.model.ad.Document;
import ua.translate.model.ad.ResultDocument;
import ua.translate.model.ad.WrittenAd;
import ua.translate.model.searchbean.SearchWrittenAdBean;
import ua.translate.model.status.AdStatus;
import ua.translate.model.viewbean.SearchFilterForWrittenAds;
import ua.translate.service.BalanceService;
import ua.translate.service.WrittenAdService;
import ua.translate.service.exception.IllegalActionForAd;
import ua.translate.service.exception.InsufficientFunds;
import ua.translate.service.exception.InvalidIdentifier;
import ua.translate.service.exception.AccessDeniedException;
import ua.translate.service.exception.DownloadFileAccessDenied;
import ua.translate.service.exception.DuplicateAdException;
import ua.translate.service.exception.WrongPageNumber;

@Service
@Transactional(propagation = Propagation.REQUIRED)
public class WrittenAdServiceImpl extends WrittenAdService {
	
	private static Logger logger = LoggerFactory.getLogger(WrittenAdServiceImpl.class);
	
	@Autowired
	private ClientDao clientDao;
	
	@Autowired
	private TranslatorDao translatorDao;
	
	@Autowired
	private WrittenAdDao adDao;
	
	@Autowired
	private AdStatusMessageDao adStatusMessageDao;
	
	@Autowired
	private BalanceService balanceService;
	/**
	 * It is default number of ads on one page
	 */
	private static final int DEFAULT_NUMBER_ADS_ON_PAGE=3;
	
	private static final int DEFAULT_REST_PERCENT = 100-RespondedAdServiceImpl.DEFAULT_PLEDGE;

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public WrittenAd updateWrittenAdByClient(String email, WrittenAd updatedAd) 
												throws InvalidIdentifier, 
													   IllegalActionForAd,
													   DuplicateAdException{
		WrittenAd adFromDb=adDao.getWrittenAdById(updatedAd.getId());
		if(adFromDb==null){
			throw new InvalidIdentifier();
		}
		Client authClient = clientDao.getClientByEmail(email);
		if(!adFromDb.getClient().equals(authClient)){
			throw new InvalidIdentifier();
		}
		if(!adFromDb.getStatus().equals(AdStatus.SHOWED)){
			logger.debug("Attempting to update WrittenAd without SHOWED status");
			throw new IllegalActionForAd();
		}
		if(adContainsSendedResponses(adFromDb)){
			logger.debug("Attempting to update WrittenAd with sended RespondedAds");
			throw new IllegalActionForAd();
		}
		
		//if client didn't choose new file
		if(updatedAd.getDocument() == null){
			updatedAd.setDocument(adFromDb.getDocument());
		}
		updatedAd.setClient(adFromDb.getClient());
		updatedAd.setPublicationDateTime(adFromDb.getPublicationDateTime());
		updatedAd.setStatus(AdStatus.SHOWED);
		updatedAd.setRespondedAds(adFromDb.getRespondedAds());
		
		if(!isAdOriginal(updatedAd, authClient)){
			throw new DuplicateAdException();
		}
		WrittenAd persistedAd = (WrittenAd)adDao.merge(updatedAd);
		
		return persistedAd;
	}

	
	
	@Override
	public Set<WrittenAd> getWrittenAdsByStatusAndOrder(int page,int numberAdsOnPage,
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
		
		Set<WrittenAd> writtenAds= new LinkedHashSet<>();
		
		Set<Ad> ads = adDao
				.getAdsByTranslateTypeAndStatusAndOrder(page, 
														numberAdsOnPage, 
														TranslateType.WRITTEN,
														adStatus, order);
		ads.forEach(ad ->{
			writtenAds.add((WrittenAd)ad);
		});
		
		return writtenAds;
	}

	
	@Override
	public Set<WrittenAd> getWrittenAdsForShowingByFilter(int page, int numberAdsOnPage,
			SearchFilterForWrittenAds searchFilter,String valueWithoutFilter)
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
		String currencyValue = searchFilter.getCurrency();
		String resultLanguageValue = searchFilter.getResultLanguage();
		String initLanguageValue = searchFilter.getInitLanguage();
		int minCostValue= searchFilter.getMinCost();
		int maxCostValue= searchFilter.getMaxCost();
		
		//parameters, which will be transferred to dao layer for making request
		Currency currency = null;
		Language resultLanguage = null;
		Language initLanguage = null;
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
		SearchWrittenAdBean searchWrittenAdBean = 
				new SearchWrittenAdBean(currency, resultLanguage, initLanguage, 
								 minCost, maxCost);
		Set<WrittenAd> ads = adDao.getFilteredWrittenAdsForShowing(page, numberAdsOnPage, 
												searchWrittenAdBean);
		return ads;
	}
	
	
	@Override
	public long getNumberOfPagesForWrittenAdsByStatus(AdStatus adStatus,int numberAdsOnPage) {
		if(numberAdsOnPage<1){
			//default value is used
			logger.debug("numberAdsOnPage={} less then 1, default value is used={}",
					numberAdsOnPage, DEFAULT_NUMBER_ADS_ON_PAGE);
			numberAdsOnPage = DEFAULT_NUMBER_ADS_ON_PAGE;
		}
		long numberOfAds = adDao.
				getNumberOfAdsByStatusAndTranslateType(adStatus, TranslateType.WRITTEN);
		long numberOfPages = (long) Math.ceil(((double)numberOfAds)/numberAdsOnPage);
		return numberOfPages;
	}
	
	
	@Override
	public long getNumberOfPagesForWrittenAdsByStatusAndFilter(AdStatus adStatus, 
														int numberAdsOnPage, 
														SearchFilterForWrittenAds searchFilter,
														String valueWithoutFilter) {
		if(numberAdsOnPage<1){
			//default value is used
			logger.debug("numberAdsOnPage={} less then 1, default value is used={}",
					numberAdsOnPage, DEFAULT_NUMBER_ADS_ON_PAGE);
			numberAdsOnPage = DEFAULT_NUMBER_ADS_ON_PAGE;
		}
		String currencyValue = searchFilter.getCurrency();
		String resultLanguageValue = searchFilter.getResultLanguage();
		String initLanguageValue = searchFilter.getInitLanguage();
		int minCostValue= searchFilter.getMinCost();
		int maxCostValue= searchFilter.getMaxCost();
		
		//parameters, which will be transferred to dao layer for making request
		Currency currency = null;
		Language resultLanguage = null;
		Language initLanguage = null;
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
		
		SearchWrittenAdBean searchAdBean = 
				new SearchWrittenAdBean(currency, resultLanguage, initLanguage, 
								 minCost, maxCost);
		long numberOfAds = adDao.getNumberOfWrittenAdsByStatusAndFilter(adStatus, searchAdBean);
		long numberOfPages = (long) Math.ceil(((double)numberOfAds)/numberAdsOnPage);
		return numberOfPages;
	}

	@Override
	public boolean saveResultDocAndMarkAsNotChecked(String email, long adId,
												final ResultDocument resultDocument) 
														throws InvalidIdentifier, IllegalActionForAd{
		if(resultDocument == null){
			return false;
		}
		
		final WrittenAd ad = adDao.getWrittenAdById(adId);
		if(ad==null){
			throw new InvalidIdentifier();
		}
		if(!AdStatus.ACCEPTED.equals(ad.getStatus()) && !AdStatus.REWORKING.equals(ad.getStatus())){
			throw new IllegalActionForAd();
		}
		final Translator translator = translatorDao.getTranslatorByEmail(email);
		if(ad.getTranslator().equals(translator)){
			ad.setStatus(AdStatus.NOTCHECKED);
			ad.setResultDocument(resultDocument);
			resultDocument.setAd(ad);
			return true;
		}
		
		
		throw new InvalidIdentifier();
	}



	@Override
	public void markAsChecked(String adminEmail, long adId)
			throws InvalidIdentifier, IllegalActionForAd, AccessDeniedException {
		UserEntity userEntity = translatorDao.getUserEntityByEmail(adminEmail);
		if(userEntity == null || 
				!UserRole.ROLE_ADMIN.equals(userEntity.getRole())){
			throw new AccessDeniedException();
		}
		WrittenAd ad = adDao.getWrittenAdById(adId);
		if(ad == null){
			throw new InvalidIdentifier();
		}
		if(!AdStatus.NOTCHECKED.equals(ad.getStatus())){
			throw new IllegalActionForAd();
		}
		
		ad.setStatus(AdStatus.CHECKED);
		
	}

	@Override
	public void markForRework(String adminEmail, long adId, String messageForDownloader)
			throws InvalidIdentifier, IllegalActionForAd, AccessDeniedException {
		UserEntity userEntity = translatorDao.getUserEntityByEmail(adminEmail);
		if(userEntity == null || 
				!UserRole.ROLE_ADMIN.equals(userEntity.getRole())){
			throw new AccessDeniedException();
		}
		WrittenAd ad = adDao.getWrittenAdById(adId);
		if(ad == null){
			throw new InvalidIdentifier();
		}
		if(!AdStatus.NOTCHECKED.equals(ad.getStatus())){
			throw new IllegalActionForAd();
		}
		ad.setStatus(AdStatus.REWORKING);
		final ResultDocument resultDoc = ad.getResultDocument();
		if(messageForDownloader != null && !messageForDownloader.equals("")){
			resultDoc.setMessageForDownloader(messageForDownloader);
		}else{
			AdStatusMessage adStatusMessage = 
					adStatusMessageDao.getAdStatusMessageByStatusAndTranslateType(AdStatus.REWORKING,TranslateType.WRITTEN);
			resultDoc.setMessageForDownloader(
					adStatusMessage.getMessageForTranslator());
		}
		
	}

	@Override
	@Transactional(rollbackFor = {InsufficientFunds.class})
	public void transferRestPriceAndChangeStatusAndIncrementExecutedAds(String email, long adId, int restPercent)
			throws InvalidIdentifier, IllegalActionForAd, InsufficientFunds {
		final Client client = clientDao.getClientByEmail(email);
		final WrittenAd writtenAd = adDao.getWrittenAdById(adId);
		if(writtenAd == null || !writtenAd.getClient().equals(client)){
			throw new InvalidIdentifier();
		}
		if(!AdStatus.CHECKED.equals(writtenAd.getStatus())){
			throw new IllegalActionForAd();
		}
		
		writtenAd.setStatus(AdStatus.PAYED);
		
		
		Translator translator = writtenAd.getTranslator();
		translator.setNumberOfExecutedAds((short) (translator.getNumberOfExecutedAds()+1));
		
		int amount = 0;
		final double clientBalance = client.getBalance();
		final int adCost = writtenAd.getCost();
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
			amount = writtenAd.getCost()*restPercent/100;
		}else{
			amount = writtenAd.getCost()*DEFAULT_REST_PERCENT/100;
		}
	
		balanceService.transferMoneyFromClientToTranslator(client, translator, amount);
		
	}



	@Override
	public Set<WrittenAd> getAllWrittenAdsByStatuses(Set<AdStatus> statuses) {
		return adDao.getAllWrittenAdsByStatuses(statuses);
	}



	@Override
	public void updateWrittenAd(WrittenAd writtenAd) {
		adDao.update(writtenAd);
	}

	
}

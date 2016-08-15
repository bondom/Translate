package ua.translate.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.persistence.Embeddable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ua.translate.controller.AdController;
import ua.translate.controller.support.ControllerHelper;
import ua.translate.controller.support.ResponsedAdComparatorByDate;
import ua.translate.dao.AdDao;
import ua.translate.dao.ClientDao;
import ua.translate.logging.service.ClientServiceAspect;
import ua.translate.model.Client;
import ua.translate.model.Translator;
import ua.translate.model.User;
import ua.translate.model.ad.Ad;
import ua.translate.model.ad.ResponsedAd;
import ua.translate.model.status.AdStatus;
import ua.translate.model.status.ResponsedAdStatus;
import ua.translate.model.viewbean.AdView;
import ua.translate.service.AdService;
import ua.translate.service.exception.IllegalActionForAd;
import ua.translate.service.exception.NonExistedAdException;
import ua.translate.service.exception.UnacceptableActionForAcceptedAd;
import ua.translate.service.exception.WrongPageNumber;

@Service
@Transactional(propagation = Propagation.REQUIRED)
public class AdServiceImpl implements AdService {
	
	private static Logger logger = LoggerFactory.getLogger(AdController.class);
	
	@Autowired
	private ClientDao clientDao;
	
	@Autowired
	private AdDao adDao;
	
	@Override
	public Ad get(long id) throws NonExistedAdException {
		Ad ad = adDao.get(id);
		if(ad==null){
			throw new NonExistedAdException();
		}
		return ad;
	}
	
	
	@Override
	public void deleteById(long id) throws NonExistedAdException,UnacceptableActionForAcceptedAd{
		Ad ad = get(id);
		if(ad.getStatus().equals(AdStatus.ACCEPTED)){
			throw new UnacceptableActionForAcceptedAd();
		}
		Set<ResponsedAd> responsedAds= ad.getResponsedAds();
		Client client = ad.getClient();
		
		//For every ResponsedAd we must delete associations with translator and client
		//But while doing this we MUSTN'T modificate responsedAd
		//Because:
		//1.ConcurrentModificationException
		//2.If to delete from responsedAd link to translator,
		//  object is not equal to object from client collection of ResponsedAds 
		responsedAds.stream().forEach(rad->{
			logger.debug("{},{},{},{}",rad.getId(),rad.getAd(),rad.getClient(),rad.getTranslator());
			Translator translator = rad.getTranslator();

			translator.removeResponsedAd(rad);
			client.removeResponsedAd(rad);
		});
		client.removeAd(ad);
	}
	@Override
	public long saveAd(Ad ad, String email){
		Client client = clientDao.getClientByEmail(email);
		ad.setPublicationDateTime(LocalDateTime.now());
		ad.setStatus(AdStatus.SHOWED);
		client.addAd(ad);
		Long adId = adDao.save(ad);
		return adId;
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Ad updateAd(long adId, Ad updatedAd) throws NonExistedAdException, 
													   UnacceptableActionForAcceptedAd, 
													   IllegalActionForAd {
		Ad adFromDb= get(adId);
		if(adFromDb==null){
			throw new NonExistedAdException();
		}
		if(adFromDb.getStatus().equals(AdStatus.ACCEPTED)){
			throw new UnacceptableActionForAcceptedAd();
		}
		if(adContainsSendedResponses(adFromDb)){
			throw new IllegalActionForAd();
		}
		
		updatedAd.setClient(adFromDb.getClient());
		updatedAd.setPublicationDateTime(adFromDb.getPublicationDateTime());
		updatedAd.setStatus(AdStatus.SHOWED);
		updatedAd.setResponsedAds(adFromDb.getResponsedAds());
		
		Ad persistedAd = adDao.merge(updatedAd);
		
		return persistedAd;
	}

	@Override
	public Set<Ad> getAdsForShowing(int page,int numberAdsOnPage) throws WrongPageNumber{
		if(page<1){
			throw new WrongPageNumber();
		}
		Set<Ad> adsForShowing = adDao.getAdsForShowing(page,numberAdsOnPage);
		return adsForShowing;
	}

	@Override
	public Ad getForShowing(long id) throws NonExistedAdException, UnacceptableActionForAcceptedAd {
		Ad ad = adDao.get(id);
		if(ad==null){
			throw new NonExistedAdException();
		}
		
		if(ad.getStatus()!=AdStatus.SHOWED){
			throw new UnacceptableActionForAcceptedAd();
		}
		
		return ad;
	}

	@Override
	public Ad getForUpdating(String email,long id) throws NonExistedAdException, 
														  UnacceptableActionForAcceptedAd,
														  IllegalActionForAd{
		Ad ad = adDao.get(id);
		if(ad==null){
			throw new NonExistedAdException();
		}
		Client authClient = clientDao.getClientByEmail(email);
		if(!ad.getClient().equals(authClient)){
			throw new NonExistedAdException();
		}
		
		if(ad.getStatus()!=AdStatus.SHOWED){
			throw new UnacceptableActionForAcceptedAd();
		}
		
		if(adContainsSendedResponses(ad)){
			throw new IllegalActionForAd();
		}
		return ad;
	}


	@Override
	public long getNumberOfPagesForShowedAds(int numberOfAds) {
		long numberOfShowedAds = adDao.getNumberOfShowedAds();
		long numberOfPages = (long) Math.ceil(((double)numberOfShowedAds)/numberOfAds);
		return numberOfPages;
	}
	
	/**
	 * Checks if {@code ad} contains at least one {@link ResponsedAd} {@code responsedAd}
	 * with {@code status=SENDED}
	 * @return true if {@code ad} contains at least one {@link ResponsedAd} {@code responsedAd}
	 * with {@code status=SENDED}, otherwise false
	 */
	private boolean adContainsSendedResponses(Ad ad){
		Set<ResponsedAd> responsedAds = ad.getResponsedAds();
		boolean sendedResponsedAdsExist  =  responsedAds.stream().anyMatch(
				rad -> rad.getStatus().equals(ResponsedAdStatus.SENDED));
		return sendedResponsedAdsExist;
	}
	
	
}

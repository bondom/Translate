package ua.translate.service.impl;

import java.time.LocalDateTime;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ua.translate.controller.AdController;
import ua.translate.dao.AdDao;
import ua.translate.dao.ClientDao;
import ua.translate.model.Client;
import ua.translate.model.Translator;
import ua.translate.model.ad.Ad;
import ua.translate.model.ad.RespondedAd;
import ua.translate.model.status.AdStatus;
import ua.translate.model.status.RespondedAdStatus;
import ua.translate.service.AdService;
import ua.translate.service.exception.IllegalActionForAd;
import ua.translate.service.exception.NonExistedAdException;
import ua.translate.service.exception.IllegalActionForAcceptedAd;
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
	public void deleteById(long id) throws NonExistedAdException,IllegalActionForAcceptedAd{
		Ad ad = get(id);
		if(ad.getStatus().equals(AdStatus.ACCEPTED)){
			throw new IllegalActionForAcceptedAd();
		}
		Set<RespondedAd> respondedAds= ad.getRespondedAds();
		Client client = ad.getClient();
		
		//For every RespondedAd we must delete associations with translator and client
		//But while doing this we MUSTN'T modificate respondedAd
		//Because:
		//1.ConcurrentModificationException
		//2.If to delete from respondedAd link to translator,
		//  object is not equal to object from client collection of RespondedAds 
		respondedAds.stream().forEach(rad->{
			logger.debug("{},{},{},{}",rad.getId(),rad.getAd(),rad.getClient(),rad.getTranslator());
			Translator translator = rad.getTranslator();

			translator.removeRespondedAd(rad);
			client.removeRespondedAd(rad);
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
	public Ad updateAd(String email, long adId, Ad updatedAd) throws NonExistedAdException, 
													   IllegalActionForAcceptedAd, 
													   IllegalActionForAd {
		Ad adFromDb= get(adId);
		if(adFromDb==null){
			throw new NonExistedAdException();
		}
		Client authClient = clientDao.getClientByEmail(email);
		if(!adFromDb.getClient().equals(authClient)){
			throw new NonExistedAdException();
		}
		if(adFromDb.getStatus().equals(AdStatus.ACCEPTED)){
			throw new IllegalActionForAcceptedAd();
		}
		if(adContainsSendedResponses(adFromDb)){
			throw new IllegalActionForAd();
		}
		
		updatedAd.setClient(adFromDb.getClient());
		updatedAd.setPublicationDateTime(adFromDb.getPublicationDateTime());
		updatedAd.setStatus(AdStatus.SHOWED);
		updatedAd.setRespondedAds(adFromDb.getRespondedAds());
		
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
	public Set<Ad> getAdsForChecking(int page, int numberAdsOnPage) throws WrongPageNumber {
		if(page<1){
			throw new WrongPageNumber();
		}
		Set<Ad> adsForChecking = adDao.getAdsForChecking(page,numberAdsOnPage);
		return adsForChecking;
	}
	
	@Override
	public Ad getForShowing(long id) throws NonExistedAdException, IllegalActionForAcceptedAd {
		Ad ad = adDao.get(id);
		if(ad==null){
			throw new NonExistedAdException();
		}
		
		if(ad.getStatus()!=AdStatus.SHOWED){
			throw new IllegalActionForAcceptedAd();
		}
		
		return ad;
	}

	@Override
	public Ad getForUpdating(String email,long id) throws NonExistedAdException, 
														  IllegalActionForAcceptedAd,
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
			throw new IllegalActionForAcceptedAd();
		}
		
		if(adContainsSendedResponses(ad)){
			throw new IllegalActionForAd();
		}
		return ad;
	}


	@Override
	public long getNumberOfPagesForAdsByStatus(AdStatus adStatus,int numberOfAds) {
		long numberOfShowedAds = adDao.getNumberOfAdsByStatus(adStatus);
		long numberOfPages = (long) Math.ceil(((double)numberOfShowedAds)/numberOfAds);
		return numberOfPages;
	}
	
	/**
	 * Checks if {@code ad} contains at least one {@link RespondedAd} {@code respondedAd}
	 * with SENDED status
	 * @return true if {@code ad} contains at least one {@code respondedAd}
	 * with SENDED status, otherwise false
	 */
	private boolean adContainsSendedResponses(Ad ad){
		Set<RespondedAd> respondedAds = ad.getRespondedAds();
		boolean sendedRespondedAdsExist  =  respondedAds.stream().anyMatch(
				rad -> rad.getStatus().equals(RespondedAdStatus.SENDED));
		return sendedRespondedAdsExist;
	}


	
	
}

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
import ua.translate.model.viewbean.AdView;
import ua.translate.service.AdService;
import ua.translate.service.exception.NonExistedAdException;
import ua.translate.service.exception.UnacceptableActionForAcceptedAd;

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
	public Ad updateAd(long adId, Ad updatedAd) throws NonExistedAdException, UnacceptableActionForAcceptedAd {
		Ad adFromDb= get(adId);
		if(adFromDb==null){
			throw new NonExistedAdException();
		}
		if(adFromDb.getStatus().equals(AdStatus.ACCEPTED)){
			throw new UnacceptableActionForAcceptedAd();
		}
		adFromDb.setName(updatedAd.getName());
		adFromDb.setDescription(updatedAd.getDescription());

		adFromDb.setInitLanguage(updatedAd.getInitLanguage());
		adFromDb.setResultLanguage(updatedAd.getResultLanguage());

		adFromDb.setTranslateType(updatedAd.getTranslateType());
		adFromDb.setCity(updatedAd.getCity());
		adFromDb.setCountry(updatedAd.getCountry());
		adFromDb.setCost(updatedAd.getCost());
		adFromDb.setEndDate(updatedAd.getEndDate());
		adFromDb.setCurrency(updatedAd.getCurrency());
		adFromDb.setFile(updatedAd.getFile());
		adFromDb.setPublicationDateTime(LocalDateTime.now());
		return adFromDb;
	}

	@Override
	public Set<Ad> getAdsForShowing() {
		Set<Ad> adsForShowing = adDao.getAdsForShowing();
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
	public Ad getForUpdating(long id) throws NonExistedAdException, UnacceptableActionForAcceptedAd {
		return getForShowing(id);
	}
	


	
	
}

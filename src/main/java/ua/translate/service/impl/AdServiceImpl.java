package ua.translate.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

import javax.persistence.Embeddable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ua.translate.dao.AdDao;
import ua.translate.dao.ClientDao;
import ua.translate.model.Client;
import ua.translate.model.ResponsedAd;
import ua.translate.model.Translator;
import ua.translate.model.ad.Ad;
import ua.translate.model.status.AdStatus;
import ua.translate.service.AdService;
import ua.translate.service.exception.NonExistedAdException;

@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class AdServiceImpl implements AdService {
	
	
	@Autowired
	private ClientDao clientDao;
	
	@Autowired
	private AdDao adDao;
	
	@Override
	public Ad get(long id) throws NonExistedAdException {
		return adDao.get(id);
	}
	
	@Override
	public List<Ad> getAllAds(){
		return adDao.getAllAds();
	}
	
	@Override
	public void deleteById(long id) throws NonExistedAdException{
		Ad ad = get(id);
		CopyOnWriteArrayList<ResponsedAd> list = new CopyOnWriteArrayList<>(ad.getResponsedAds());
		list.forEach(rad->{
			Translator translator = rad.getTranslator();
			translator.removeResponsedAd(rad);
			ad.removeResponsedAd(rad);
			/**
			 * This client object has two ResponseAd objects with the same Id
			 * why????????
			 */
			Client client = rad.getClient();
			client.removeResponsedAd(rad);
			
		});
		
		Client client = ad.getClient();
		client.removeAd(ad);
	}
	@Override
	public long saveAd(Ad ad, String email){
		Client client = clientDao.getClientByEmail(email);
		ad.setCreationDateTime(LocalDateTime.now());
		ad.setStatus(AdStatus.CREATED);
		ad.setClient(client);
		Long adId = adDao.save(ad);
		return adId;
	}
	
	@Override
	public Ad updateAd(long adId, Ad updatedAd) throws NonExistedAdException {
		Ad adFromDb= get(adId);
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
		adFromDb.setCreationDateTime(LocalDateTime.now());
		return adFromDb;
	}
	
	
}

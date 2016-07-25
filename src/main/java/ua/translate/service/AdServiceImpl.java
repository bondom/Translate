package ua.translate.service;

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

import ua.translate.dao.AbstractDao;
import ua.translate.dao.AdDao;
import ua.translate.dao.UserDao;
import ua.translate.model.Client;
import ua.translate.model.Translator;
import ua.translate.model.ad.Ad;
import ua.translate.model.ad.AdStatus;
import ua.translate.model.ad.ResponsedAd;

@Service("adService")
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class AdServiceImpl {
	
	Logger logger = Logger.getLogger(AdServiceImpl.class.getName());
	
	@Autowired
	@Qualifier("clientDao")
	private UserDao<Client> clientDao;
	
	@Autowired
	AdDao adDao;
	
	
	public long saveAd(Ad ad, String email){
		Client client = (Client)clientDao.getUserByEmail(email);
		ad.setCreationDateTime(LocalDateTime.now());
		ad.setStatus(AdStatus.CREATED);
		ad.setClient(client);
		Long adId = ((AbstractDao<Long, Ad>)adDao).save(ad);
		return adId;
	}
	
	public Ad updateAd(Ad ad){
		Ad adFromDB = get(ad.getId());
		adFromDB.setName(ad.getName());
		adFromDB.setDescription(ad.getDescription());

		adFromDB.setInitLanguage(ad.getInitLanguage());
		adFromDB.setResultLanguage(ad.getResultLanguage());

		adFromDB.setTranslateType(ad.getTranslateType());
		adFromDB.setCity(ad.getCity());
		adFromDB.setCountry(ad.getCountry());
		adFromDB.setCost(ad.getCost());
		adFromDB.setEndDate(ad.getEndDate());
		adFromDB.setCurrency(ad.getCurrency());
		adFromDB.setFile(ad.getFile());
		adFromDB.setCreationDateTime(LocalDateTime.now());
		return adFromDB;
	}
	public Ad get(long id){
		return ((AbstractDao<Long, Ad>)adDao).get(id);
	}
	public List<Ad> getAllAds(){
		return adDao.getAllAds();
	}
	
	public void deleteById(long id){
		Ad ad = get(id);
		if(ad!=null){
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
	}
}

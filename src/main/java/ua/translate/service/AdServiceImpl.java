package ua.translate.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ua.translate.dao.AbstractDao;
import ua.translate.dao.AdDao;
import ua.translate.dao.UserDao;
import ua.translate.model.Client;
import ua.translate.model.ad.Ad;
import ua.translate.model.ad.AdStatus;

@Service("adService")
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class AdServiceImpl {
	
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
	
	public Ad get(long id){
		return ((AbstractDao<Long, Ad>)adDao).get(id);
	}
	
	public List<Ad> getAllAds(){
		return adDao.getAllAds();
	}
}

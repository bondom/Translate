package org.dream.university.service;

import java.time.LocalDateTime;
import java.util.List;

import org.dream.university.dao.AbstractDao;
import org.dream.university.dao.AdDao;
import org.dream.university.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.dream.university.model.Client;
import org.dream.university.model.ad.Ad;
import org.dream.university.model.ad.AdStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("adService")
@Transactional(propagation = Propagation.REQUIRED)
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

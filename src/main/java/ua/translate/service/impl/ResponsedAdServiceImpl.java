package ua.translate.service.impl;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ua.translate.dao.ResponsedAdDao;
import ua.translate.model.ad.Ad;
import ua.translate.model.ad.ResponsedAd;
import ua.translate.model.status.AdStatus;
import ua.translate.model.status.ResponsedAdStatus;
import ua.translate.service.ResponsedAdService;
import ua.translate.service.exception.NonExistedResponsedAdException;

@Service
@Transactional(propagation = Propagation.REQUIRED )
public class ResponsedAdServiceImpl implements ResponsedAdService {

	@Autowired
	private ResponsedAdDao responsedAdDao;

	public ResponsedAd get(long id) throws NonExistedResponsedAdException{
		ResponsedAd responsedAd = responsedAdDao.get(id);
		if(responsedAd==null){
			throw new NonExistedResponsedAdException();
		}
		return responsedAd;
	}
	
	public void accept(long id) throws NonExistedResponsedAdException{
		ResponsedAd responsedAd = get(id);
		if(responsedAd == null){
			throw new NonExistedResponsedAdException();
		}
		Ad mainAd = responsedAd.getAd();
		mainAd.setStatus(AdStatus.ACCEPTED);
		Set<ResponsedAd> responsedAds = mainAd.getResponsedAds();
		responsedAds.forEach(rad->{
			if(responsedAd.equals(rad)){
				rad.setStatus(ResponsedAdStatus.ACCEPTED);
			}else{
				rad.setStatus(ResponsedAdStatus.REJECTED);
			}
		});
	}
	
	public void reject(long id) throws NonExistedResponsedAdException{
		ResponsedAd responsedAd= get(id);
		
		if(responsedAd == null){
			throw new NonExistedResponsedAdException();
		}
		responsedAd.setStatus(ResponsedAdStatus.REJECTED);
	}

}


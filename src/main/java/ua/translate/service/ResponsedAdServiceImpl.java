package ua.translate.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ua.translate.dao.AbstractDao;
import ua.translate.dao.ResponsedAdDao;
import ua.translate.model.ad.Ad;
import ua.translate.model.ad.ResponsedAd;
import ua.translate.model.ad.ResponsedAdStatus;

@Service("responsedAdService")
@Transactional(propagation = Propagation.REQUIRED )
public class ResponsedAdServiceImpl {

	@Autowired
	private ResponsedAdDao responsedAdDao;

	public ResponsedAd get(long id){
		return ((AbstractDao<Long, ResponsedAd>)responsedAdDao).get(id);
	}
	
	public void accept(ResponsedAd responsedAd){
		Ad mainAd = responsedAd.getAd();
		List<ResponsedAd> responsedAds = responsedAdDao.getResponsedAdsByAd(mainAd);
		responsedAds.forEach(rad->{
			if(responsedAd.equals(rad)){
				rad.setStatus(ResponsedAdStatus.ACCEPTED);
			}else{
				rad.setStatus(ResponsedAdStatus.REJECTED);
			}
		});
		//responsedAdDao.reject();
	}
	
	public void reject(long id){
		ResponsedAd responsedAdFromDB = get(id);
		responsedAdFromDB.setStatus(ResponsedAdStatus.REJECTED);
	}

}


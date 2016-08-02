package ua.translate.dao;

import java.util.List;

import ua.translate.model.ResponsedAd;
import ua.translate.model.ad.Ad;

public interface ResponsedAdDao extends AbstractDao<Long, ResponsedAd>{
	
	/**
	 * Returns list of {@link ResponsedAd}s, associated to {@code ad}
	 */
	public List<ResponsedAd> getResponsedAdsByAd(Ad ad);
}

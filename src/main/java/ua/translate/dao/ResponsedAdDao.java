package ua.translate.dao;

import java.util.List;

import ua.translate.model.ad.Ad;
import ua.translate.model.ad.ResponsedAd;

public interface ResponsedAdDao {
	public List<ResponsedAd> getResponsedAdsByAd(Ad ad);
}

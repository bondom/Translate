package ua.translate.dao;

import java.util.List;

import ua.translate.model.ad.Ad;

public interface AdDao extends AbstractDao<Long, Ad>{
	public List<Ad> getAllAds();
}

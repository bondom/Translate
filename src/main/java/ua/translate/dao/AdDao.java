package ua.translate.dao;

import java.util.List;

import ua.translate.model.ad.Ad;

public interface AdDao {
	public List<Ad> getAllAds();
	public void deleteById(long id);
}

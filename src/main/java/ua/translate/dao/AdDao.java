package ua.translate.dao;

import java.util.List;
import java.util.Set;

import ua.translate.model.ad.Ad;

public interface AdDao extends AbstractDao<Long, Ad>{
	public Set<Ad> getAdsForShowing();
}

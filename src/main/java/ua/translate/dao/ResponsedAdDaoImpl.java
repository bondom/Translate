package ua.translate.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import ua.translate.model.ad.Ad;
import ua.translate.model.ad.ResponsedAd;

@Repository
public class ResponsedAdDaoImpl extends AbstractDao<Long, ResponsedAd> 
								implements ResponsedAdDao{

	@Autowired
	SessionFactory sessionFactory;

	@Override
	public List<ResponsedAd> getResponsedAdsByAd(Ad ad) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.getNamedQuery("responsedAdsByAd");
		query.setParameter("ad", ad);
		List<ResponsedAd> responsedAds = query.list();
		return responsedAds;
	}
	

}

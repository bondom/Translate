package org.dream.university.dao;

import java.util.List;

import org.dream.university.model.User;
import org.dream.university.model.ad.Ad;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("adDao")
public class AdDaoImpl extends AbstractDao<Integer, Ad> implements AdDao {
	
	@Autowired
	SessionFactory sessionFactory;
	

	@Override
	public List<Ad> getAllAds() {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.getNamedQuery("allAds");
		List<Ad> ads = (List<Ad>)query.list();
		return ads;
	}

}

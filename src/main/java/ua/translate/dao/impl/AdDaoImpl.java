package ua.translate.dao.impl;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import ua.translate.dao.AdDao;
import ua.translate.model.Client;
import ua.translate.model.User;
import ua.translate.model.ad.Ad;

@Repository
public class AdDaoImpl implements AdDao{
	
	@Autowired
	SessionFactory sessionFactory;
	

	@Override
	public List<Ad> getAllAds() {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.getNamedQuery("getAllAds");
		List<Ad> ads = (List<Ad>)query.list();
		return ads;
	}


	@Override
	public Long save(Ad ad) {
		Session session = sessionFactory.getCurrentSession();
		return (Long) session.save(ad);
	}

	@Override
	public Ad get(Long id) {
		Session session = sessionFactory.getCurrentSession();
		Ad ad = session.get(Ad.class, id);
		return ad;
	}

	@Override
	public void delete(Ad ad) {
		Session session = sessionFactory.getCurrentSession();
		session.delete(ad);
		
	}

	@Override
	public Ad update(Ad ad) {
		Session session = sessionFactory.getCurrentSession();
		session.update(ad);
		return ad;
	}



}

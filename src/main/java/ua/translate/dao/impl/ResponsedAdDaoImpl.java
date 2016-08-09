package ua.translate.dao.impl;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import ua.translate.dao.ResponsedAdDao;
import ua.translate.model.ad.Ad;
import ua.translate.model.ad.ResponsedAd;

@Repository
public class ResponsedAdDaoImpl implements ResponsedAdDao{

	@Autowired
	SessionFactory sessionFactory;

	@Override
	public Long save(ResponsedAd responsedAd) {
		Session session = sessionFactory.getCurrentSession();
		return (Long) session.save(responsedAd);
	}

	@Override
	public ResponsedAd get(Long id) {
		Session session = sessionFactory.getCurrentSession();
		ResponsedAd responsedAd = session.get(ResponsedAd.class, id);
		return responsedAd;
	}

	@Override
	public void delete(ResponsedAd responsedAd) {
		Session session = sessionFactory.getCurrentSession();
		session.delete(responsedAd);
		
	}

	@Override
	public ResponsedAd update(ResponsedAd responsedAd) {
		Session session = sessionFactory.getCurrentSession();
		session.update(responsedAd);
		return responsedAd;
	}
	

}

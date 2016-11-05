package ua.translate.dao;


import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;

import ua.translate.model.ad.Ad;

public class AdDao implements AbstractDao<Long, Ad>{
	
	@Autowired
	SessionFactory sessionFactory;

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
	
	
	@Override
	public void flush() throws ConstraintViolationException{
		Session session = sessionFactory.getCurrentSession();
		session.flush();
	}
	
	public Ad merge(Ad ad) {
		Session session = sessionFactory.getCurrentSession();
		Ad updatedAd = (Ad) session.merge(ad);
		return updatedAd;
	}
	
	/**
	 * Adds Restriction to {@code criteria} only if {@code value} is not null
	 */
	protected void addRestrictionIfNotNull(Criteria criteria, String propertyName, Object value) {
	    if (value != null) {
	        criteria.add(Restrictions.eq(propertyName, value));
	    }
	}

	@Override
	public void clear() {
		Session session = sessionFactory.getCurrentSession();
		session.clear();
	}
	
}

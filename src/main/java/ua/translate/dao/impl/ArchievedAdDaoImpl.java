package ua.translate.dao.impl;

import java.util.LinkedHashSet;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import ua.translate.dao.ArchievedAdDao;
import ua.translate.model.ad.ArchievedAd;

@Repository
public class ArchievedAdDaoImpl implements ArchievedAdDao{

	@Autowired
	SessionFactory sessionFactory;
	
	@Override
	public Long save(ArchievedAd t) {
		Session session = sessionFactory.getCurrentSession();
		return (Long) session.save(t);
	}

	@Override
	public ArchievedAd get(Long id) {
		Session session = sessionFactory.getCurrentSession();
		ArchievedAd archievedAd = session.get(ArchievedAd.class, id);
		return archievedAd;
	}

	@Override
	public void delete(ArchievedAd entity) {
		Session session = sessionFactory.getCurrentSession();
		session.delete(entity);
	}

	@Override
	public ArchievedAd update(ArchievedAd t) {
		Session session = sessionFactory.getCurrentSession();
		session.update(t);
		return t;
	}

	@Override
	public void flush() throws ConstraintViolationException {
		Session session = sessionFactory.getCurrentSession();
		session.flush();
	}

	@Override
	public void clear() {
		Session session = sessionFactory.getCurrentSession();
		session.clear();
	}
	
	@Override
	public ArchievedAd getArchievedAdByAdId(long adId) {
		Criteria criteria = 
				sessionFactory.getCurrentSession()
							  .createCriteria(ArchievedAd.class,"archieve")
							  .createAlias("archieve.ad", "ad")
							  .add(Restrictions.eq("ad.id", adId));
		ArchievedAd archievedAd = (ArchievedAd)criteria.uniqueResult();
		return archievedAd;
	}

	@Override
	public Set<ArchievedAd> getAllArchievedAdsInDescOrder() {
		Criteria criteria = 
				sessionFactory.getCurrentSession().createCriteria(ArchievedAd.class);
		criteria.addOrder(Order.desc("creatingDateTime"));
		
		@SuppressWarnings("unchecked")
		Set<ArchievedAd> resultSet = new LinkedHashSet<>(criteria.list());
		return resultSet;
	}

}

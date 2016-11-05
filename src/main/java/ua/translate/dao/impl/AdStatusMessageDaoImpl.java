package ua.translate.dao.impl;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import ua.translate.dao.AdStatusMessageDao;
import ua.translate.model.ad.Ad.TranslateType;
import ua.translate.model.ad.AdStatusMessage;
import ua.translate.model.status.AdStatus;

@Repository
public class AdStatusMessageDaoImpl implements AdStatusMessageDao{

	@Autowired
	SessionFactory sessionFactory;
	
	@Override
	public Long save(AdStatusMessage t) {
		Session session = sessionFactory.getCurrentSession();
		return (Long) session.save(t);
	}

	@Override
	public AdStatusMessage get(Long id) {
		Session session = sessionFactory.getCurrentSession();
		return session.get(AdStatusMessage.class, id);
	}

	@Override
	public void delete(AdStatusMessage entity) {
		Session session = sessionFactory.getCurrentSession();
		session.delete(entity);
	}

	@Override
	public AdStatusMessage update(AdStatusMessage t) {
		Session session = sessionFactory.getCurrentSession();
		session.update(t);
		return t;
	}

	@Override
	public void clear() {
		Session session = sessionFactory.getCurrentSession();
		session.clear();
	}
	
	@Override
	public void flush() throws ConstraintViolationException {
		Session session = sessionFactory.getCurrentSession();
		session.flush();
	}

	@Override
	public AdStatusMessage getAdStatusMessageByStatusAndTranslateType(AdStatus adStatus,
																	  TranslateType translateType) {
		Criteria criteria = 
				sessionFactory.getCurrentSession().createCriteria(AdStatusMessage.class);
		criteria.add(Restrictions.eq("adStatus", adStatus));
		criteria.add(Restrictions.eq("translateType", translateType));
		AdStatusMessage adStatusMessage = (AdStatusMessage)criteria.uniqueResult();
		return adStatusMessage;
	}

	@Override
	public Set<AdStatusMessage> getAllAdStatusMessages() {
		Criteria criteria = 
				sessionFactory.getCurrentSession().createCriteria(AdStatusMessage.class);
		@SuppressWarnings("unchecked")
		List<AdStatusMessage> adStatusMessages = (List<AdStatusMessage>)criteria.list();
		Set<AdStatusMessage> resultSet = new LinkedHashSet<>(adStatusMessages);
		return resultSet;
	}

}

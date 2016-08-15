package ua.translate.dao.impl;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Query;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import ua.translate.dao.TranslatorDao;
import ua.translate.model.Translator;
import ua.translate.model.status.AdStatus;

@Repository
public class TranslatorDaoImpl  extends TranslatorDao{

	@Autowired
	SessionFactory sessionFactory;
	
	@Override
	public Long save(Translator t) {
		Session session = sessionFactory.getCurrentSession();
		return (Long) session.save(t);
	}

	@Override
	public Translator get(Long id) {
		Session session = sessionFactory.getCurrentSession();
		Translator client = session.get(Translator.class, id);
		return client;
	}

	@Override
	public void delete(Translator entity) {
		Session session = sessionFactory.getCurrentSession();
		session.delete(entity);
		
	}

	@Override
	public Translator update(Translator t) {
		Session session = sessionFactory.getCurrentSession();
		session.update(t);
		return t;
	}
	
	@Override
	public void flush() throws ConstraintViolationException{
		Session session = sessionFactory.getCurrentSession();
		session.flush();
	}
	
	@Override
	public Translator getTranslatorByEmail(String email) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.getNamedQuery("translatorByEmail");
		query.setParameter("email", email);
		Translator translator = (Translator)query.uniqueResult();
		return translator;
	}

	@Override
	public Set<Translator> getTranslators(int page,int numberTranslatorsOnPage) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.getNamedQuery("getTranslatorsDescOrderByPubTime");
		
		query.setMaxResults(numberTranslatorsOnPage);
		final int firstResult = numberTranslatorsOnPage*(page-1);
		query.setFirstResult(firstResult);
		
		List<Translator> translators = (List<Translator>)query.list();
		Set<Translator> setOfTranslators = new LinkedHashSet<>(translators);
		return setOfTranslators;
	}

	@Override
	public long getNumberOfTranslators() {
		Session session = sessionFactory.getCurrentSession();
		ScrollableResults scrollableResults = session
				.getNamedQuery("getTranslatorsDescOrderByPubTime")
				.scroll();

		scrollableResults.last();
		long totalRecords = scrollableResults.getRowNumber()+1;
		scrollableResults.close();
		return totalRecords;
	}

}

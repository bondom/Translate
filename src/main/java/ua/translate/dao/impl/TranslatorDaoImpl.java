package ua.translate.dao.impl;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import ua.translate.dao.TranslatorDao;
import ua.translate.model.Translator;

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
	public Translator getTranslatorByEmail(String email) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.getNamedQuery("translatorByEmail");
		query.setParameter("email", email);
		Translator translator = (Translator)query.uniqueResult();
		return translator;
	}

}

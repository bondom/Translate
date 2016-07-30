package ua.translate.dao;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import ua.translate.model.Client;
import ua.translate.model.Translator;

@Repository("translatorDao")
public class TranslatorDaoImpl extends AbstractDao<Integer,Translator> implements UserDao<Translator> {

	@Autowired
	SessionFactory sessionFactory;
	
	@Override
	public Translator getUserByEmail(String email) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.getNamedQuery("translatorByEmail");
		query.setParameter("email", email);
		Translator translator = (Translator)query.uniqueResult();
		return translator;
	}

	@Override
	public Translator getUserByConfirmedUrl(String email) {
		// TODO Auto-generated method stub
		return null;
	}

}

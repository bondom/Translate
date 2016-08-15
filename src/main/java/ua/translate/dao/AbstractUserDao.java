package ua.translate.dao;

import java.io.Serializable;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ua.translate.model.Client;
import ua.translate.model.User;

public abstract class AbstractUserDao<PK extends Serializable,T>  implements AbstractDao<PK, T>{

	@Autowired
	SessionFactory sessionFactory;
	
	/**
	 * Gets user from db by email
	 * @return {@link User}, or {code null}, if user with that email is not registered
	 */
	public User getUserByEmail(String email){
	    Session session = sessionFactory.getCurrentSession();
		Query query = session.getNamedQuery("userByEmail");
		query.setParameter("email", email);
		User user = (User)query.uniqueResult();
		return user;
	}
	
	/**
	 * Gets user from db by confirmed Url
	 * @return {@link User}, or {code null}, if user with that confirmed url is not registered
	 */
	public User getUserByConfirmationUrl(String confirmationUrl){
		Session session = sessionFactory.getCurrentSession();
		Query query = session.getNamedQuery("userByConfirmationUrl");
		query.setParameter("confirmationUrl", confirmationUrl);
		User user= (User)query.uniqueResult();
		return user;
	}

	
	
}

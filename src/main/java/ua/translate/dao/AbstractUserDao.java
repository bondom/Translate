package ua.translate.dao;

import java.io.Serializable;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ua.translate.model.User;
import ua.translate.model.UserEntity;

public abstract class AbstractUserDao<PK extends Serializable,T>  implements AbstractDao<PK, T>{

	@Autowired
	SessionFactory sessionFactory;
	
	/**
	 * Gets {@link UserEntity} from db by email
	 * @return {@code UserEntity}, or {@code null}, if user with that email is not registered
	 */
	public UserEntity getUserEntityByEmail(String email){
	    Session session = sessionFactory.getCurrentSession();
		Query query = session.getNamedQuery("userByEmail");
		query.setParameter("email", email);
		UserEntity user = (UserEntity)query.uniqueResult();
		return user;
	}
	
	/**
	 * Gets {@link User} from db by email
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
	 * Gets {@link User} from db by confirmed Url
	 * @return {@link User}, or {code null}, if user with that 
	 * confirmation url is not registered
	 */
	public User getUserByConfirmationUrl(String confirmationUrl){
		Session session = sessionFactory.getCurrentSession();
		Query query = session.getNamedQuery("userByConfirmationUrl");
		query.setParameter("confirmationUrl", confirmationUrl);
		User user= (User)query.uniqueResult();
		return user;
	}

	
	
}

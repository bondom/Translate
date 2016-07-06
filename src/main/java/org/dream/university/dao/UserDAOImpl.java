package org.dream.university.dao;

import org.dream.university.model.User;
import org.dream.university.model.UserStatus;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class UserDAOImpl implements UserDAO{

	@Autowired
	SessionFactory sessionFactory;
	
	public UserDAOImpl(){}
	
	
	@Override
	public User getUserByLogin(String userName) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.getNamedQuery("userByLogin");
		query.setParameter("userLogin", userName);
		User user = (User)query.uniqueResult(); 
		return user;
	}

	@Override
	public User getUserByEmail(String userEmail) {
	    Session session = sessionFactory.getCurrentSession();
		Query query = session.getNamedQuery("userByEmail");
		query.setParameter("userEmail", userEmail);
		User user = (User)query.uniqueResult();
		return user;
	}

	@Override
	public void create(User user) {
		user.setRole("ROLE_USER");
		user.setUserStatus(UserStatus.ACTIVE);
		Session session = sessionFactory.getCurrentSession();
		session.persist(user);
	}


	@Override
	public User get(int id) {
		Session session = sessionFactory.getCurrentSession();
		User user = session.get(User.class, id);
		return user;
	}

	@Override
	public boolean delete(int id) {
		User user = this.get(id);
		Session session = sessionFactory.getCurrentSession();
		if(user!=null){
			//User with the same id exists in DB table
			session.delete(user);
			return true;
		}else return false;
	}


	@Override
	public User update(User user) {
		user.setRole("ROLE_USER");
		user.setUserStatus(UserStatus.ACTIVE);
		Session session = sessionFactory.getCurrentSession();
		session.update(user);
		return user;
	}
}

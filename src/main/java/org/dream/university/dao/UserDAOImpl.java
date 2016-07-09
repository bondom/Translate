package org.dream.university.dao;

import java.io.IOException;

import org.dream.university.model.User;
import org.dream.university.model.UserStatus;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

@Repository
public class UserDAOImpl implements UserDAO{

	@Autowired
	SessionFactory sessionFactory;
	
	public UserDAOImpl(){}
	
	
	@Override
	public User getUserByLogin(String login) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.getNamedQuery("userByLogin");
		query.setParameter("login", login);
		User user = (User)query.uniqueResult(); 
		return user;
	}

	@Override
	public User getUserByEmail(String email) {
	    Session session = sessionFactory.getCurrentSession();
		Query query = session.getNamedQuery("userByEmail");
		query.setParameter("email", email);
		User user = (User)query.uniqueResult();
		return user;
	}

	@Override
	public void create(User user) {
		user.setRole("ROLE_USER");
		user.setStatus(UserStatus.ACTIVE);
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
		user.setStatus(UserStatus.ACTIVE);
		Session session = sessionFactory.getCurrentSession();
		session.update(user);
		return user;
	}
	
/*	public User setBlobImage(String login, MultipartFile file){
		User userFromDB = getUserByLogin(login);
		Session session = sessionFactory.getCurrentSession();
		session.doWork(connection ->{
			userFromDB.setImage(connection.createBlob());
			userFromDB.getImage().setBytes(1,file.getBytes());
		});
		return update(userFromDB);
	}*/
}

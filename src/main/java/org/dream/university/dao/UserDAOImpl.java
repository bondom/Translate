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

@Repository("userDao")
public class UserDaoImpl extends AbstractDao<Integer, User> implements UserDao<User>{

	@Autowired
	SessionFactory sessionFactory;
	
	public UserDaoImpl(){}
	
	@Override
	public User getUserByEmail(String email) {
	    Session session = sessionFactory.getCurrentSession();
		Query query = session.getNamedQuery("userByEmail");
		query.setParameter("email", email);
		User user = (User)query.uniqueResult();
		return user;
	}

	
}

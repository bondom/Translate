package ua.translate.dao.impl;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import ua.translate.dao.UserDao;
import ua.translate.model.Client;
import ua.translate.model.User;


@Repository
public class UserDaoImpl extends UserDao{

	
	@Autowired
	SessionFactory sessionFactory;
	
	@Override
	public Long save(User t) {
		Session session = sessionFactory.getCurrentSession();
		return (Long) session.save(t);
	}

	@Override
	public User get(Long id) {
		Session session = sessionFactory.getCurrentSession();
		User user = session.get(User.class, id);
		return user;
	}

	@Override
	public void delete(User entity) {
		Session session = sessionFactory.getCurrentSession();
		session.delete(entity);
		
	}

	@Override
	public User update(User t) {
		Session session = sessionFactory.getCurrentSession();
		session.update(t);
		return t;
	}
	
	@Override
	public void flush() throws ConstraintViolationException{
		Session session = sessionFactory.getCurrentSession();
		session.flush();
	}


}

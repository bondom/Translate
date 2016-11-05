package ua.translate.dao.impl;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import ua.translate.dao.ClientDao;
import ua.translate.model.Client;

@Repository
public class ClientDaoImpl extends ClientDao{

	@Autowired
	SessionFactory sessionFactory;
	
	@Override
	public Long save(Client t){
		Session session = sessionFactory.getCurrentSession();
		return (Long) session.save(t);
	}

	@Override
	public Client get(Long id) {
		Session session = sessionFactory.getCurrentSession();
		Client client = session.get(Client.class, id);
		return client;
	}

	@Override
	public void delete(Client entity) {
		Session session = sessionFactory.getCurrentSession();
		session.delete(entity);
		
	}

	@Override
	public void clear() {
		Session session = sessionFactory.getCurrentSession();
		session.clear();
	}
	
	@Override
	public Client update(Client t) {
		Session session = sessionFactory.getCurrentSession();
		session.update(t);
		return t;
	}
	
	public void flush() throws ConstraintViolationException{
		Session session = sessionFactory.getCurrentSession();
		session.flush();
	}

	@Override
	public Client getClientByEmail(String email) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.getNamedQuery("clientByEmail");
		query.setParameter("email", email);
		Client client = (Client)query.uniqueResult();
		return client;
	}


}

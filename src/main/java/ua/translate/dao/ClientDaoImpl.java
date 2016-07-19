package ua.translate.dao;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import ua.translate.model.Client;

@Repository("clientDao")
public class ClientDaoImpl extends AbstractDao<Integer,Client> implements UserDao<Client> {

	@Autowired
	SessionFactory sessionFactory;
	
	@Override
	public Client getUserByEmail(String email) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.getNamedQuery("clientByEmail");
		query.setParameter("email", email);
		Client client = (Client)query.uniqueResult();
		return client;
	}

}

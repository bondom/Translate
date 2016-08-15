package ua.translate.dao.impl;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import ua.translate.dao.AbstractDao;
import ua.translate.model.security.PersistentLogin;

@Repository("tokenRepositoryDao")
@Transactional
public class HibernateTokenRepositoryImpl implements AbstractDao<String,PersistentLogin> ,PersistentTokenRepository{

	
	@Autowired
	SessionFactory sessionFactory;
	
	
	/*!!!!Доделать для разных браузеров!!!!*/
	 
	@Override
	public void createNewToken(PersistentRememberMeToken token) {
		System.out.println("Creating a token");
		PersistentLogin persistentLogin = new PersistentLogin();
		persistentLogin.setUsername(token.getUsername());
		persistentLogin.setSeries(token.getSeries());
	    persistentLogin.setToken(token.getTokenValue());
	    persistentLogin.setLast_used(token.getDate());
	    save(persistentLogin);
		
	}

	@Override
	public void updateToken(String seriesId, String tokenValue, Date lastUsed) {
		System.out.println("Updating a token");
		PersistentLogin persistentLogin = get(seriesId);
	    persistentLogin.setToken(tokenValue);
	    persistentLogin.setLast_used(lastUsed);
	    update(persistentLogin);
	}
	
	
	/**
	 * Доделать для разных браузеров!!!
	 */
	@Override
	public PersistentRememberMeToken getTokenForSeries(String seriesId) {
		try {
            Criteria crit = createEntityCriteria();
            crit.add(Restrictions.eq("series", seriesId));
            PersistentLogin persistentLogin = (PersistentLogin) crit.uniqueResult();
 
            return new PersistentRememberMeToken(persistentLogin.getUsername(), persistentLogin.getSeries(),
                    persistentLogin.getToken(), persistentLogin.getLast_used());
        } catch (Exception e) {
        	return null;
        }
	}

	@Override
	public void removeUserTokens(String username) {
		Criteria crit = createEntityCriteria();
        crit.add(Restrictions.eq("username", username));
        List<PersistentLogin> persistentLogins = (List<PersistentLogin>) crit.list();
        for(PersistentLogin persistenLogin: persistentLogins){
        	delete(persistenLogin);
        }
		
	}

	@Override
	public String save(PersistentLogin t) {
		Session session = sessionFactory.getCurrentSession();
		return (String)session.save(t);
	}

	@Override
	public PersistentLogin get(String id) {
		Session session = sessionFactory.getCurrentSession();
		return session.get(PersistentLogin.class, id);
	}

	@Override
	public void delete(PersistentLogin entity) {
		Session session = sessionFactory.getCurrentSession();
		session.delete(entity);
	}

	@Override
	public PersistentLogin update(PersistentLogin t) {
		Session session = sessionFactory.getCurrentSession();
		session.update(t);
		return t;
	}

	private Criteria createEntityCriteria(){
		return sessionFactory.getCurrentSession().createCriteria(PersistentLogin.class);
	}
	
	public void flush() throws ConstraintViolationException{
		Session session = sessionFactory.getCurrentSession();
		session.flush();
	}
}

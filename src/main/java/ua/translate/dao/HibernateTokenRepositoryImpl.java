package ua.translate.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import ua.translate.model.security.PersistentLogin;

@Repository("tokenRepositoryDao")
@Transactional
public class HibernateTokenRepositoryImpl extends AbstractDao<String,PersistentLogin> implements PersistentTokenRepository{

	
	/**
	 * Доделать для разных браузеров!!!
	 */
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
		System.out.println("Getting a token");
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
		System.out.println("Removing a token");
		Criteria crit = createEntityCriteria();
        crit.add(Restrictions.eq("username", username));
        List<PersistentLogin> persistentLogins = (List<PersistentLogin>) crit.list();
        for(PersistentLogin persistenLogin: persistentLogins){
        	delete(persistenLogin);
        }
		
	}

}

package ua.translate.dao.impl;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Query;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import ua.translate.dao.RespondedAdDao;
import ua.translate.model.Client;
import ua.translate.model.Translator;
import ua.translate.model.ad.RespondedAd;

@Repository
public class RespondedAdDaoImpl implements RespondedAdDao{

	@Autowired
	SessionFactory sessionFactory;

	@Override
	public Long save(RespondedAd respondedAd) {
		Session session = sessionFactory.getCurrentSession();
		return (Long) session.save(respondedAd);
	}

	@Override
	public RespondedAd get(Long id) {
		Session session = sessionFactory.getCurrentSession();
		RespondedAd respondedAd = session.get(RespondedAd.class, id);
		return respondedAd;
	}

	@Override
	public void delete(RespondedAd respondedAd) {
		Session session = sessionFactory.getCurrentSession();
		session.delete(respondedAd);
		
	}

	@Override
	public RespondedAd update(RespondedAd respondedAd) {
		Session session = sessionFactory.getCurrentSession();
		session.update(respondedAd);
		return respondedAd;
	}
	
	@Override
	public void flush() throws ConstraintViolationException{
		Session session = sessionFactory.getCurrentSession();
		session.flush();
	}
	
	@Override
	public void clear() {
		Session session = sessionFactory.getCurrentSession();
		session.clear();
	}
	
	@Override
	public Set<RespondedAd> getRespondedAdsByClient(Client client, int page, int numberRespondedAdsOnPage) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.getNamedQuery("getRadsByClient");
		
		query.setParameter("client", client);
		query.setMaxResults(numberRespondedAdsOnPage);
		final int firstResult = numberRespondedAdsOnPage*(page-1);
		query.setFirstResult(firstResult);
		
		@SuppressWarnings("unchecked")
		List<RespondedAd> respondedAds = (List<RespondedAd>)query.list();
		Set<RespondedAd> setOfRespondedAds = new LinkedHashSet<>(respondedAds);
		return setOfRespondedAds;
	}

	@Override
	public long getNumberOfRespondedAdsByClient(Client client) {
		Session session = sessionFactory.getCurrentSession();
		ScrollableResults scrollableResults = session
				.getNamedQuery("getRadsByClient")
				.setParameter("client", client)
				.scroll();

		scrollableResults.last();
		long totalRecords = scrollableResults.getRowNumber()+1;
		scrollableResults.close();
		return totalRecords;
	}
	
	
	@Override
	public Set<RespondedAd> getRespondedAdsByTranslator(Translator translator, int page, int numberRespondedAdsOnPage) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.getNamedQuery("getRadsByTranslator");
		
		query.setParameter("translator", translator);
		query.setMaxResults(numberRespondedAdsOnPage);
		final int firstResult = numberRespondedAdsOnPage*(page-1);
		query.setFirstResult(firstResult);
		
		@SuppressWarnings("unchecked")
		List<RespondedAd> respondedAds = (List<RespondedAd>)query.list();
		Set<RespondedAd> setOfRespondedAds = new LinkedHashSet<>(respondedAds);
		return setOfRespondedAds;
	}

	@Override
	public long getNumberOfRespondedAdsByTranslator(Translator translator) {
		Session session = sessionFactory.getCurrentSession();
		ScrollableResults scrollableResults = session
				.getNamedQuery("getRadsByTranslator")
				.setParameter("translator", translator)
				.scroll();

		scrollableResults.last();
		long totalRecords = scrollableResults.getRowNumber()+1;
		scrollableResults.close();
		return totalRecords;
	}
	

}

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

import ua.translate.dao.ResponsedAdDao;
import ua.translate.model.Client;
import ua.translate.model.Translator;
import ua.translate.model.ad.Ad;
import ua.translate.model.ad.ResponsedAd;
import ua.translate.model.status.AdStatus;

@Repository
public class ResponsedAdDaoImpl implements ResponsedAdDao{

	@Autowired
	SessionFactory sessionFactory;

	@Override
	public Long save(ResponsedAd responsedAd) {
		Session session = sessionFactory.getCurrentSession();
		return (Long) session.save(responsedAd);
	}

	@Override
	public ResponsedAd get(Long id) {
		Session session = sessionFactory.getCurrentSession();
		ResponsedAd responsedAd = session.get(ResponsedAd.class, id);
		return responsedAd;
	}

	@Override
	public void delete(ResponsedAd responsedAd) {
		Session session = sessionFactory.getCurrentSession();
		session.delete(responsedAd);
		
	}

	@Override
	public ResponsedAd update(ResponsedAd responsedAd) {
		Session session = sessionFactory.getCurrentSession();
		session.update(responsedAd);
		return responsedAd;
	}
	
	@Override
	public void flush() throws ConstraintViolationException{
		Session session = sessionFactory.getCurrentSession();
		session.flush();
	}
	
	@Override
	public Set<ResponsedAd> getResponsedAdsByClient(Client client, int page, int numberResponsedAdsOnPage) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.getNamedQuery("getRadsByClient");
		
		query.setParameter("client", client);
		query.setMaxResults(numberResponsedAdsOnPage);
		final int firstResult = numberResponsedAdsOnPage*(page-1);
		query.setFirstResult(firstResult);
		
		List<ResponsedAd> responsedAds = (List<ResponsedAd>)query.list();
		Set<ResponsedAd> setOfResponsedAds = new LinkedHashSet<>(responsedAds);
		return setOfResponsedAds;
	}

	@Override
	public long getNumberOfResponsedAdsByClient(Client client) {
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
	public Set<ResponsedAd> getResponsedAdsByTranslator(Translator translator, int page, int numberResponsedAdsOnPage) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.getNamedQuery("getRadsByTranslator");
		
		query.setParameter("translator", translator);
		query.setMaxResults(numberResponsedAdsOnPage);
		final int firstResult = numberResponsedAdsOnPage*(page-1);
		query.setFirstResult(firstResult);
		
		List<ResponsedAd> responsedAds = (List<ResponsedAd>)query.list();
		Set<ResponsedAd> setOfResponsedAds = new LinkedHashSet<>(responsedAds);
		return setOfResponsedAds;
	}

	@Override
	public long getNumberOfResponsedAdsByTranslator(Translator translator) {
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

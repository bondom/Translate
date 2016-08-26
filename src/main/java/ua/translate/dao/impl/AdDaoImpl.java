package ua.translate.dao.impl;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import ua.translate.dao.AdDao;
import ua.translate.model.Client;
import ua.translate.model.Language;
import ua.translate.model.User;
import ua.translate.model.ad.Ad;
import ua.translate.model.ad.Currency;
import ua.translate.model.ad.TranslateType;
import ua.translate.model.searchbean.SearchAdBean;
import ua.translate.model.security.PersistentLogin;
import ua.translate.model.status.AdStatus;

@Repository
public class AdDaoImpl implements AdDao{
	
	
	@Autowired
	SessionFactory sessionFactory;

	@Override
	public Long save(Ad ad) {
		Session session = sessionFactory.getCurrentSession();
		return (Long) session.save(ad);
	}

	@Override
	public Ad get(Long id) {
		Session session = sessionFactory.getCurrentSession();
		Ad ad = session.get(Ad.class, id);
		return ad;
	}

	@Override
	public void delete(Ad ad) {
		Session session = sessionFactory.getCurrentSession();
		session.delete(ad);
		
	}

	@Override
	public Ad update(Ad ad) {
		Session session = sessionFactory.getCurrentSession();
		session.update(ad);
		return ad;
	}

	
	@Override
	public Set<Ad> getAdsForShowing(int page,int numberAdsOnPage) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.getNamedQuery("getAdsByStatusAndDescOrderByPubDate");
		
		query.setParameter("status",AdStatus.SHOWED);
		query.setMaxResults(numberAdsOnPage);
		final int firstResult = numberAdsOnPage*(page-1);
		query.setFirstResult(firstResult);
		
		List<Ad> ads = (List<Ad>)query.list();
		Set<Ad> adsSet = new LinkedHashSet<>(ads);
		return adsSet;
	}
	
	@Override
	public Set<Ad> getAdsForChecking(int page, int numberAdsOnPage) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.getNamedQuery("getAdsByStatusAndAscOrderByPubDate");
		
		query.setParameter("status",AdStatus.NOTCHECKED);
		query.setMaxResults(numberAdsOnPage);
		final int firstResult = numberAdsOnPage*(page-1);
		query.setFirstResult(firstResult);
		
		List<Ad> ads = (List<Ad>)query.list();
		Set<Ad> adsSet = new LinkedHashSet<>(ads);
		return adsSet;
	}
	
	@Override
	public Set<Ad> getFilteredAdsForShowing(int page, int numberAdsOnPage,
											SearchAdBean searchAdBean) {
		Criteria criteria = 
				sessionFactory.getCurrentSession().createCriteria(Ad.class);
		String country = searchAdBean.getCountry();
		String city = searchAdBean.getCity();
		TranslateType translateType = searchAdBean.getTranslateType();
		Currency currency = searchAdBean.getCurrency();
		Language initLanguage= searchAdBean.getInitLanguage();
		Language resultLanguage= searchAdBean.getResultLanguage();
		int maxCost = searchAdBean.getMaxCost();
		int minCost = searchAdBean.getMinCost();
		
		addRestrictionIfNotNull(criteria, "country", country);
		addRestrictionIfNotNull(criteria, "city", city);
		addRestrictionIfNotNull(criteria, "translateType", translateType);
		addRestrictionIfNotNull(criteria, "currency", currency);
		addRestrictionIfNotNull(criteria, "initLanguage", initLanguage);
		addRestrictionIfNotNull(criteria, "resultLanguage", resultLanguage);
		criteria.addOrder(Order.desc("publicationDateTime"));
		criteria.add(Restrictions.eq("status", AdStatus.SHOWED));
		if(maxCost!=0){
			criteria.add(Restrictions.between("cost", minCost, maxCost));
		}
		//getting ads for concrete page 
		criteria.setMaxResults(numberAdsOnPage);
		final int firstResult = numberAdsOnPage*(page-1);
		criteria.setFirstResult(firstResult);

		List<Ad> ads = (List<Ad>)criteria.list();
		Set<Ad> adsSet = new LinkedHashSet<>(ads);
		return adsSet;
	}
	

	@Override
	public long getNumberOfAdsByStatus(AdStatus adStatus) {
		Session session = sessionFactory.getCurrentSession();
		ScrollableResults scrollableResults = session
				.getNamedQuery("getAdsByStatusAndDescOrderByPubDate")
				.setParameter("status",adStatus)
				.scroll();

		scrollableResults.last();
		long totalRecords = scrollableResults.getRowNumber()+1;
		scrollableResults.close();
		return totalRecords;
	}
	
	@Override
	public long getNumberOfAdsByStatusAndFilter(AdStatus adStatus, SearchAdBean searchAdBean) {
		Criteria criteria = 
				sessionFactory.getCurrentSession().createCriteria(Ad.class);
		String country = searchAdBean.getCountry();
		String city = searchAdBean.getCity();
		TranslateType translateType = searchAdBean.getTranslateType();
		Currency currency = searchAdBean.getCurrency();
		Language initLanguage= searchAdBean.getInitLanguage();
		Language resultLanguage= searchAdBean.getResultLanguage();
		int maxCost = searchAdBean.getMaxCost();
		int minCost = searchAdBean.getMinCost();
		
		addRestrictionIfNotNull(criteria, "country", country);
		addRestrictionIfNotNull(criteria, "city", city);
		addRestrictionIfNotNull(criteria, "translateType", translateType);
		addRestrictionIfNotNull(criteria, "currency", currency);
		addRestrictionIfNotNull(criteria, "initLanguage", initLanguage);
		addRestrictionIfNotNull(criteria, "resultLanguage", resultLanguage);
		criteria.addOrder(Order.desc("publicationDateTime"));
		criteria.add(Restrictions.eq("status", AdStatus.SHOWED));
		if(maxCost!=0){
			criteria.add(Restrictions.between("cost", minCost, maxCost));
		}
		ScrollableResults scrollableResults = criteria.scroll();
		scrollableResults.last();
		long totalRecords = scrollableResults.getRowNumber()+1;
		scrollableResults.close();
		return totalRecords;
	}

	@Override
	public Ad merge(Ad ad) {
		Session session = sessionFactory.getCurrentSession();
		Ad updatedAd = (Ad) session.merge(ad);
		return updatedAd;
	}

	@Override
	public void flush() throws ConstraintViolationException{
		Session session = sessionFactory.getCurrentSession();
		session.flush();
	}

	/**
	 * Adds Restriction to {@code criteria} only if {@code value} is not null
	 */
	private void addRestrictionIfNotNull(Criteria criteria, String propertyName, Object value) {
	    if (value != null) {
	        criteria.add(Restrictions.eq(propertyName, value));
	    }
	}


}

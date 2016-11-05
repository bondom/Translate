package ua.translate.dao.impl;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.ScrollableResults;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import ua.translate.dao.OralAdDao;
import ua.translate.model.Language;
import ua.translate.model.ad.Ad.TranslateType;
import ua.translate.model.ad.Currency;
import ua.translate.model.ad.OralAd;
import ua.translate.model.searchbean.SearchOralAdBean;
import ua.translate.model.status.AdStatus;

@Repository
public class OralAdDaoImpl extends OralAdDao{
	
	@Autowired
	SessionFactory sessionFactory;
	
	@Override
	public OralAd getOralAdById(long id) {
		Criteria criteria = 
				sessionFactory.getCurrentSession().createCriteria(OralAd.class);
		criteria.add(Restrictions.eq("id", id));
		OralAd oralAd= (OralAd)criteria.uniqueResult();
		return oralAd;
	}
	
	@Override
	public Set<OralAd> getFilteredOralAdsForShowing(int page, int numberAdsOnPage,
											SearchOralAdBean searchAdBean) {
		Criteria criteria = 
				sessionFactory.getCurrentSession().createCriteria(OralAd.class);
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

		@SuppressWarnings("unchecked")
		List<OralAd> ads = (List<OralAd>)criteria.list();
		Set<OralAd> adsSet = new LinkedHashSet<>(ads);
		return adsSet;
	}
	
	@Override
	public long getNumberOfOralAdsByStatusAndFilter
						(AdStatus adStatus, SearchOralAdBean searchAdBean) {
		Criteria criteria = 
				sessionFactory.getCurrentSession().createCriteria(OralAd.class);
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
		criteria.add(Restrictions.eq("status",adStatus));
		if(maxCost!=0){
			criteria.add(Restrictions.between("cost", minCost, maxCost));
		}
		ScrollableResults scrollableResults = criteria.scroll();
		scrollableResults.last();
		long totalRecords = scrollableResults.getRowNumber()+1;
		scrollableResults.close();
		return totalRecords;
	}
	
}

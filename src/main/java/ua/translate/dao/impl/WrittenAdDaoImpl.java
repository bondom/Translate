package ua.translate.dao.impl;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import ua.translate.dao.WrittenAdDao;
import ua.translate.model.Language;
import ua.translate.model.ad.Currency;
import ua.translate.model.ad.OralAd;
import ua.translate.model.ad.WrittenAd;
import ua.translate.model.ad.Ad.TranslateType;
import ua.translate.model.searchbean.SearchOralAdBean;
import ua.translate.model.searchbean.SearchWrittenAdBean;
import ua.translate.model.status.AdStatus;

@Repository
public class WrittenAdDaoImpl extends WrittenAdDao {
	
	@Autowired
	SessionFactory sessionFactory;
	
	@Override
	public WrittenAd getWrittenAdById(long id) {
		Criteria criteria = 
				sessionFactory.getCurrentSession().createCriteria(WrittenAd.class);
		criteria.add(Restrictions.eq("id", id));
		WrittenAd oralAd= (WrittenAd)criteria.uniqueResult();
		return oralAd;
	}
	
	
	@Override
	public Set<WrittenAd> getFilteredWrittenAdsForShowing(int page, int numberAdsOnPage,
											SearchWrittenAdBean searchAdBean) {
		Criteria criteria = 
				sessionFactory.getCurrentSession().createCriteria(WrittenAd.class);
		TranslateType translateType = searchAdBean.getTranslateType();
		Currency currency = searchAdBean.getCurrency();
		Language initLanguage= searchAdBean.getInitLanguage();
		Language resultLanguage= searchAdBean.getResultLanguage();
		int maxCost = searchAdBean.getMaxCost();
		int minCost = searchAdBean.getMinCost();
		
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

		List<WrittenAd> ads = (List<WrittenAd>)criteria.list();
		Set<WrittenAd> adsSet = new LinkedHashSet<>(ads);
		return adsSet;
	}
	
	@Override
	public long getNumberOfWrittenAdsByStatusAndFilter(
			AdStatus adStatus, SearchWrittenAdBean searchAdBean) {
		Criteria criteria = 
				sessionFactory.getCurrentSession().createCriteria(WrittenAd.class);
		TranslateType translateType = searchAdBean.getTranslateType();
		Currency currency = searchAdBean.getCurrency();
		Language initLanguage= searchAdBean.getInitLanguage();
		Language resultLanguage= searchAdBean.getResultLanguage();
		int maxCost = searchAdBean.getMaxCost();
		int minCost = searchAdBean.getMinCost();
		
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


	@Override
	public Set<WrittenAd> getAllWrittenAdsByStatuses(Set<AdStatus> statuses) {
		Criteria criteria = 
				sessionFactory.getCurrentSession().createCriteria(WrittenAd.class);
		List<Criterion> criterionsList = new ArrayList<>();
		statuses.forEach(status ->{
			criterionsList.add(Restrictions.eq("status", status));
		});
		Criterion[] criterionsArray = new Criterion[statuses.size()];
		criteria.add(Restrictions.or(criterionsList.toArray(criterionsArray)));
		List<WrittenAd> list = criteria.list();
		return new LinkedHashSet<>(list);
	}


}

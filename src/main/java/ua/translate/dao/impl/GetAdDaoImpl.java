package ua.translate.dao.impl;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import ua.translate.dao.GetAdDao;
import ua.translate.model.ad.Ad;
import ua.translate.model.ad.Ad.TranslateType;
import ua.translate.model.status.AdStatus;

@Repository("getAdDao")
public class GetAdDaoImpl extends GetAdDao {
	
	@Autowired
	SessionFactory sessionFactory;
	
	
	@Override
	public Set<Ad> getAdsByTranslateTypeAndStatusAndOrder(int page, int numberAdsOnPage, TranslateType translateType,
			AdStatus adStatus, ua.translate.model.Order order) {
		Criteria criteria = 
				sessionFactory.getCurrentSession().createCriteria(Ad.class);
		criteria.add(Restrictions.eq("status", adStatus));
		criteria.add(Restrictions.eq("translateType", translateType));
		if(ua.translate.model.Order.ASC.equals(order)){
			criteria.addOrder(Order.asc("publicationDateTime"));
		}else if(ua.translate.model.Order.DESC.equals(order)){
			criteria.addOrder(Order.desc("publicationDateTime"));
		}
		
		//Getting List of ids of Ads, which match to criteria above
		criteria.setProjection(Projections.distinct(Projections.property("id")));
		criteria.setMaxResults(numberAdsOnPage);
		final int firstResult = numberAdsOnPage*(page-1);
		criteria.setFirstResult(firstResult);
		
		@SuppressWarnings("unchecked")
		List<Long> ids = criteria.list();
		
		if(ids.isEmpty()){
			return new LinkedHashSet<Ad>();
		}
		//Getting List of Ads
		criteria.setProjection(null);
		criteria.setFirstResult(0); 
		criteria.setMaxResults(Integer.MAX_VALUE);
		criteria.add(Restrictions.in("id", ids));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		@SuppressWarnings("unchecked")
		List<Ad> ads = (List<Ad>)criteria.list();
		
		Set<Ad> adsSet = new LinkedHashSet<>(ads);
		return adsSet;
	}


	@Override
	public long getNumberOfAdsByStatusAndTranslateType(AdStatus adStatus, TranslateType translateType) {
		Criteria criteria = 
				sessionFactory.getCurrentSession().createCriteria(Ad.class);
		criteria.add(Restrictions.eq("status",adStatus));
		criteria.add(Restrictions.eq("translateType", translateType));
		Projection idCountProjection = Projections.countDistinct("id");
		criteria.setProjection(idCountProjection);
		
		Long resultCount = (Long)criteria.uniqueResult();
		return resultCount;
	}


	@Override
	public Set<Ad> getAllAdsByStatus(AdStatus adStatus) {
		Criteria criteria = 
				sessionFactory.getCurrentSession().createCriteria(Ad.class);
		criteria.add(Restrictions.eq("status", adStatus));
		
		@SuppressWarnings("unchecked")
		List<Ad> ads = (List<Ad>)criteria.list();
		Set<Ad> adsSet = new LinkedHashSet<>(ads);
		return adsSet;
	}

}

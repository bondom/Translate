package ua.translate.logging.dao;

import java.util.Set;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import ua.translate.model.ad.OralAd;
import ua.translate.model.searchbean.SearchOralAdBean;
import ua.translate.model.status.AdStatus;

@Aspect
@Component
public class OralAdDaoAspect {
	
	Logger logger = LoggerFactory.getLogger(OralAdDaoAspect.class);
	
	@Around("ua.translate.logging.SystemArchitecture.inDaoLayer() &&"
			 + " execution(public * getOralAdById(..)) "
			 + "&& args(adId)")
	public OralAd getOralAdById(ProceedingJoinPoint thisJoinPoint,
								long adId)
								throws Throwable{
												
		String className = thisJoinPoint.getTarget().getClass().getName();
		String methodName = thisJoinPoint.getSignature().getName();
		
		OralAd oralAd= null;
		try {
			oralAd = (OralAd)thisJoinPoint.proceed();
		} catch (Throwable e) {
			logger.error("{}.{}(adId={}):{}",
					className,methodName,adId,e.getClass());
			e.printStackTrace();
			throw e;
		}
		
		logger.debug("{}.{}(adId={}): OralAd=[name={},status={}]",
				className,methodName,adId,oralAd.getName(),oralAd.getStatus());
		
		return oralAd;
	}
	
	@SuppressWarnings("unchecked")
	@Around("ua.translate.logging.SystemArchitecture.inDaoLayer() &&"
			 + " execution(public * getFilteredOralAdsForShowing(..)) "
			 + "&& args(page,numberAdsOnPage,searchAdBean)")
	public Set<OralAd> getFilteredOralAdsForShowing(ProceedingJoinPoint thisJoinPoint,
									int page, int numberAdsOnPage,
									SearchOralAdBean searchAdBean) throws Throwable {
		String className = thisJoinPoint.getTarget().getClass().getName();
		String methodName = thisJoinPoint.getSignature().getName();
		logger.debug("{}.{}: input parameters: page={},numberAdsOnPage={},"
				+ "searchAdBean=[translateType={},initLanguage={},resultLanguage={},"
				+ "currency={},maxCost={},minCost={},country={},city={}]",
				className,methodName,page,numberAdsOnPage,searchAdBean.getTranslateType(),
				searchAdBean.getInitLanguage(),searchAdBean.getResultLanguage(),
				searchAdBean.getCurrency(),searchAdBean.getMaxCost(),searchAdBean.getMinCost(),
				searchAdBean.getCountry(),searchAdBean.getCity());
				
		Set<OralAd> ads = null;
		try{
			ads = (Set<OralAd>) thisJoinPoint.proceed();
		}catch(Throwable ex){
			logger.error("{}.{}(page={},numberOfAdsOnPage={},searchAdBean-'see above' ):{}",
					className,methodName,page,numberAdsOnPage, ex.getClass());
			ex.printStackTrace();
			throw ex;
		}
		
		if(ads.size()>0){
			ads.stream().forEach(ad ->{
				logger.debug("{}.{}(page={},numberOfAdsOnPage={},searchAdBean -'see above'):"
						+ "Ad=[id = {},name='{}',cost='{}',"
						+ "initLanguage={},resultLanguage={}]"
						,className,methodName,
						page,numberAdsOnPage,ad.getId(),
						ad.getName(),ad.getCost(),ad.getInitLanguage(),
						ad.getResultLanguage());
				
			});
		}else logger.debug("{}.{}(page={},numberOfAdsOnPage={},searchAdBean-'see above'):"
				+ " 0 ads exists",className,methodName,page,numberAdsOnPage);
		
		return ads;
	
	}
	
	@Around("ua.translate.logging.SystemArchitecture.inDaoLayer() &&"
			 + " execution(public long getNumberOfOralAdsByStatusAndFilter(..)) "
			 + "&& args(adStatus,searchAdBean)")
	public Long getNumberOfOralAdsByStatusAndFilter(ProceedingJoinPoint thisJoinPoint,
												AdStatus adStatus, 
												SearchOralAdBean searchAdBean)
												throws Throwable{
												
		String className = thisJoinPoint.getTarget().getClass().getName();
		String methodName = thisJoinPoint.getSignature().getName();
		logger.debug("{}.{}: input parameters: ad status={},"
				+ "searchAdBean=[translateType={},initLanguage={},resultLanguage={},"
				+ "currency={},maxCost={},minCost={},country={},city={}]",
				className,methodName,adStatus,searchAdBean.getTranslateType(),
				searchAdBean.getInitLanguage(),searchAdBean.getResultLanguage(),
				searchAdBean.getCurrency(),searchAdBean.getMaxCost(),searchAdBean.getMinCost(),
				searchAdBean.getCountry(),searchAdBean.getCity());
		Long numberOfAds= 0L;
		try {
			numberOfAds = (Long)thisJoinPoint.proceed();
		} catch (Throwable e) {
			logger.error("{}.{}(input parameters - 'see above'):{}",
					className,methodName,e.getClass());
			e.printStackTrace();
			throw e;
		}
		
		logger.debug("{}.{}(input parameters - 'see above'): number of ads={}",
				className,methodName,numberOfAds);
		
		return numberOfAds;
	}
	
	
}

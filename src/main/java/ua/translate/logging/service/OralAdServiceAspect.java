package ua.translate.logging.service;

import java.util.Set;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import ua.translate.model.Order;
import ua.translate.model.ad.Ad;
import ua.translate.model.ad.Document;
import ua.translate.model.ad.ResultDocument;
import ua.translate.model.ad.WrittenAd;
import ua.translate.model.ad.OralAd;
import ua.translate.model.ad.Ad.TranslateType;
import ua.translate.model.status.AdStatus;
import ua.translate.model.viewbean.SearchFilterForAds;
import ua.translate.model.viewbean.SearchFilterForOralAds;

@Aspect
@Component
public class OralAdServiceAspect {
	
	Logger logger = LoggerFactory.getLogger(OralAdServiceAspect.class);
	
	@Around("ua.translate.logging.SystemArchitecture.inServiceLayer() &&"
			 + " execution(public ua.translate.model.ad.OralAd updateOralAd(..)) && args(email,oralAd)")
	public OralAd updateOralAd(ProceedingJoinPoint thisJoinPoint,String email,OralAd oralAd) throws Throwable {
		String className = thisJoinPoint.getTarget().getClass().getName();
		String methodName = thisJoinPoint.getSignature().getName();
		OralAd savedAd= null;
		try {
			savedAd = (OralAd)thisJoinPoint.proceed();
		} catch (Throwable e) {
			logger.error("{}.{}(email={}, OralAd=[id={}]):{}:{}",className,methodName,
					email,oralAd.getId(),e.getClass(),e.getMessage());
			throw e;
		}
		
		logger.info("{}.{}(email={},OralAd=[id={}]): OralAd is successfully updated - [ad name={}]"
				+"",
				className,methodName,email,oralAd.getId(),savedAd.getName());

		return savedAd;
	}
	
	@Around("ua.translate.logging.SystemArchitecture.inServiceLayer() &&"
			 + " execution(public * getOralAdsByStatusAndOrder(..)) && "
			 + "args(page,numberAdsOnPage,adStatus,order)")
	public Set<OralAd> getOralAdsByStatusAndOrder(ProceedingJoinPoint thisJoinPoint,
									   int page,
									   int numberAdsOnPage,
									   AdStatus adStatus,
									   Order order) throws Throwable {
		String className = thisJoinPoint.getTarget().getClass().getName();
		String methodName = thisJoinPoint.getSignature().getName();
		
		Set<OralAd> ads = null;
		try{
			ads = (Set<OralAd>) thisJoinPoint.proceed();
		}catch(Throwable ex){
			logger.error("{}.{}(page={},numberOfAdsOnPage={},status={},order={}):{}",
					className,methodName,page,numberAdsOnPage,adStatus,
					order,ex.getClass());
			throw ex;
		}
		
		if(ads.size()>0){
			ads.stream().forEach(ad ->{
				logger.debug("{}.{}(page={},numberOfAdsOnPage={},status={},order={}):"
						+ "Ad=[id = {},name='{}',cost='{}',"
						+ "initLanguage={},resultLanguage={}]"
						,className,methodName,
						page,numberAdsOnPage,adStatus,order,ad.getId(),
						ad.getName(),ad.getCost(),ad.getInitLanguage(),
						ad.getResultLanguage());
				
			});
		}else logger.debug("{}.{}(page={},numberOfAdsOnPage={},status={},order={}):"
				+ " 0 ads exists",page,numberAdsOnPage,adStatus,order,className,methodName);
		return ads;
	}
	
	@Around("ua.translate.logging.SystemArchitecture.inServiceLayer() &&"
			 + " execution(public * getOralAdsForShowingByFilter(..)) "
			 + "&& args(page,numberAdsOnPage,searchFilter,valueWithoutFilter)")
	public Set<OralAd> getOralAdsForShowingByFilter(ProceedingJoinPoint thisJoinPoint,
									int page, int numberAdsOnPage,
									SearchFilterForOralAds searchFilter,
									String valueWithoutFilter) throws Throwable {
		String className = thisJoinPoint.getTarget().getClass().getName();
		String methodName = thisJoinPoint.getSignature().getName();
		logger.debug("{}.{}: input parameters: page={},numberAdsOnPage={},"
				+ "searchFilter=[currency={},initLanguage={},resultLanguage={},"
				+ "maxCost={},minCost={},country={},city={}],valueWithoutFilter='{}'",
				className,methodName,page,numberAdsOnPage,
				searchFilter.getCurrency(),
				searchFilter.getInitLanguage(),searchFilter.getResultLanguage(),
				searchFilter.getMaxCost(), searchFilter.getMinCost(),
				searchFilter.getCountry(),searchFilter.getCity(),
				valueWithoutFilter);
		Set<OralAd> ads = null;
		try{
			ads = (Set<OralAd>) thisJoinPoint.proceed();
		}catch(Throwable ex){
			logger.error("{}.{}(page={},numberOfAdsOnPage={},searchFilter-'see above' ):{}",
					className,methodName,page,numberAdsOnPage, ex.getClass());
			ex.printStackTrace();
			throw ex;
		}
		
		if(ads.size()>0){
			ads.stream().forEach(ad ->{
				logger.debug("{}.{}(page={},numberOfAdsOnPage={},searchFilter -'see above'):"
						+ "Ad=[id = {},name='{}',cost='{}',"
						+ "initLanguage={},resultLanguage={}]"
						,className,methodName,
						page,numberAdsOnPage,ad.getId(),
						ad.getName(),ad.getCost(),ad.getInitLanguage(),
						ad.getResultLanguage());
			});
		}else logger.debug("{}.{}(page={},numberOfAdsOnPage={},searchFilter-'see above'):"
				+ " 0 ads exists",className,methodName,page,numberAdsOnPage);
		
		return ads;
	
	}
	
	@Around("ua.translate.logging.SystemArchitecture.inServiceLayer() &&"
			 + " execution(public long getNumberOfPagesForOralAdsByStatus(..)) "
			 + "&& args(adStatus,numberOfAds)")
	public Long getNumberOfPagesForOralAdsByStatus(ProceedingJoinPoint thisJoinPoint,
													  AdStatus adStatus,
													  long numberOfAds) throws Throwable {
		String className = thisJoinPoint.getTarget().getClass().getName();
		String methodName = thisJoinPoint.getSignature().getName();
		Long numberOfPages= 0L;
		try {
			numberOfPages = (Long)thisJoinPoint.proceed();
		} catch (Throwable e) {
			logger.error("{}.{}(numberOfAdsOnPage={},status={}):{}:{}",
					className,methodName,numberOfAds,adStatus,e.getClass());
			throw e;
		}
		
		logger.debug("{}.{}(numberOfAdsOnPage={},status={}): number of pages={}",
				className,methodName,numberOfAds,adStatus,numberOfPages);

		return numberOfPages;
	}
	
	@Around("ua.translate.logging.SystemArchitecture.inServiceLayer() &&"
		 + " execution(public long getNumberOfPagesForOralAdsByStatusAndFilter(..)) "
		 + "&& args(adStatus,numberOfAds,searchFilter,valueWithoutFilter)")
	public Long getNumberOfPagesForOralAdsByStatusAndFilter(ProceedingJoinPoint thisJoinPoint,
												AdStatus adStatus, 
												int numberOfAds, 
												SearchFilterForOralAds searchFilter,
												String valueWithoutFilter)
												throws Throwable{
												
		String className = thisJoinPoint.getTarget().getClass().getName();
		String methodName = thisJoinPoint.getSignature().getName();
		logger.debug("{}.{}: input parameters:numberOfAds={},ad status={},"
				+ "searchFilter=[currency={},country={},city={},"
				+ "initLanguage={},resultLanguage={},"
				+ "maxCost={},minCost={}],valueWithoutFilter='{}'",
				className,methodName,numberOfAds,adStatus,
				searchFilter.getCurrency(),searchFilter.getCountry(),
				searchFilter.getCity(),
				searchFilter.getInitLanguage(),searchFilter.getResultLanguage(),
				searchFilter.getMaxCost(), searchFilter.getMinCost(),
				valueWithoutFilter);
		Long numberOfPages= 0L;
		try {
			numberOfPages = (Long)thisJoinPoint.proceed();
		} catch (Throwable e) {
			logger.error("{}.{}:{}",className,methodName,e.getClass());
			throw e;
		}
		
		logger.debug("{}.{}: number of pages={}",
				className,methodName,numberOfPages);
		
		return numberOfPages;
	}
	
	
}

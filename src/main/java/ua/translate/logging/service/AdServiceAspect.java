package ua.translate.logging.service;

import java.util.List;
import java.util.Set;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import ua.translate.model.ad.Ad;
import ua.translate.model.ad.RespondedAd;
import ua.translate.model.status.AdStatus;
import ua.translate.model.viewbean.SearchFilterForAds;

@Aspect
@Component
public class AdServiceAspect {
	
	Logger logger = LoggerFactory.getLogger(AdServiceAspect.class);
	
	@Around("ua.translate.logging.SystemArchitecture.inServiceLayer() &&"
			 + " execution(public long saveAd(..)) && args(ad,email)")
	public long saveAd(ProceedingJoinPoint thisJoinPoint,Ad ad,String email) throws Throwable {
		String className = thisJoinPoint.getTarget().getClass().getName();
		String methodName = thisJoinPoint.getSignature().getName();
		long adId= 0L;
		try {
			adId = (long)thisJoinPoint.proceed();
		} catch (Throwable e) {
			logger.error("{}.{}:{}:{}",className,methodName,e.getClass(),e.getMessage());
			throw e;
		}
		
		logger.info("{}.{}(email={}): Ad is successfully saved:name='{}',status='{}',clientId='{}'",
				className,methodName,email,ad.getName(),ad.getStatus(),ad.getClient().getId());

		return adId;
	}
	
	@Around("ua.translate.logging.SystemArchitecture.inServiceLayer() &&"
			 + " execution(public ua.translate.model.ad.Ad updateAd(..)) && args(email,adId,..)")
	public Ad updateAd(ProceedingJoinPoint thisJoinPoint,String email,long adId) throws Throwable {
		String className = thisJoinPoint.getTarget().getClass().getName();
		String methodName = thisJoinPoint.getSignature().getName();
		Ad savedAd= null;
		try {
			savedAd = (Ad)thisJoinPoint.proceed();
		} catch (Throwable e) {
			logger.error("{}.{}(email={},adId={}):{}:{}",className,methodName,
					email,adId,e.getClass(),e.getMessage());
			throw e;
		}
		
		logger.info("{}.{}(email={},adId={}): Ad is successfully updated - [ad name={}]"
				+"",
				className,methodName,email,adId,savedAd.getName());

		return savedAd;
	}
	
	@AfterReturning(pointcut = "ua.translate.logging.SystemArchitecture.inServiceLayer() &&"
			 + " execution(public * getAdsFor*(..))",returning = "ads")
	public void getAdsForShowingOrChecking(JoinPoint thisJoinPoint,Set<Ad> ads) throws Throwable {
		String className = thisJoinPoint.getTarget().getClass().getName();
		String methodName = thisJoinPoint.getSignature().getName();
		if(ads.size()>0){
			ads.stream().forEach(ad ->{
				logger.info("{}.{}:Ad=[id = {},name='{}',status= '{}',cost='{}',"
						+ "initLanguage={},resultLanguage={},translateType={},"
						,className,methodName, ad.getId(),
						ad.getName(),ad.getStatus(),ad.getCost(),ad.getInitLanguage(),
						ad.getResultLanguage(),ad.getTranslateType());
			});
		}else logger.info("{}.{}: 0 ads exists",className,methodName);
	}
	
	@Before("ua.translate.logging.SystemArchitecture.inServiceLayer() &&"
			 + " execution(public * getAdsForShowingByFilter(..)) "
			 + "&& args(page,numberAdsOnPage,searchFilter,valueWithoutFilter)")
	public void getAdsForShowingByFilter(JoinPoint thisJoinPoint,
										int page, int numberAdsOnPage,
										SearchFilterForAds searchFilter,
										String valueWithoutFilter) throws Throwable {
		String className = thisJoinPoint.getTarget().getClass().getName();
		String methodName = thisJoinPoint.getSignature().getName();
		logger.debug("{}.{}: input parameters: page={},numberAdsOnPage={},"
				+ "searchFilter=[country={},city={},currency={},"
				+ "translateType={},initLanguage={},resultLanguage={},"
				+ "maxCost={},minCost={}],valueWithoutFilter='{}'",
				className,methodName,page,numberAdsOnPage,
				searchFilter.getCountry(),searchFilter.getCity(),
				searchFilter.getCurrency(),searchFilter.getTranslateType(),
				searchFilter.getInitLanguage(),searchFilter.getResultLanguage(),
				searchFilter.getMaxCost(), searchFilter.getMinCost(),
				valueWithoutFilter);
		
	}
	
	@Around("ua.translate.logging.SystemArchitecture.inServiceLayer() &&"
			 + " execution(public long getNumberOfPagesForAdsByStatus(..)) "
			 + "&& args(adStatus,numberOfAds)")
	public Long getNumberOfPagesForAdsByStatus(ProceedingJoinPoint thisJoinPoint,AdStatus adStatus,long numberOfAds) throws Throwable {
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
			 + " execution(public long getNumberOfPagesForAdsByStatusAndFilter(..)) "
			 + "&& args(adStatus,numberOfAds,searchFilter,valueWithoutFilter)")
	public Long getNumberOfPagesForAdsByStatusAndFilter(ProceedingJoinPoint thisJoinPoint,
														AdStatus adStatus, 
														int numberOfAds, 
														SearchFilterForAds searchFilter,
														String valueWithoutFilter)
														throws Throwable{
														
		String className = thisJoinPoint.getTarget().getClass().getName();
		String methodName = thisJoinPoint.getSignature().getName();
		logger.debug("{}.{}: input parameters:numberOfAds={},"
				+ "searchFilter=[country={},city={},currency={},"
				+ "translateType={},initLanguage={},resultLanguage={},"
				+ "maxCost={},minCost={}],valueWithoutFilter='{}'",
				className,methodName,numberOfAds,
				searchFilter.getCountry(),searchFilter.getCity(),
				searchFilter.getCurrency(),searchFilter.getTranslateType(),
				searchFilter.getInitLanguage(),searchFilter.getResultLanguage(),
				searchFilter.getMaxCost(), searchFilter.getMinCost(),
				valueWithoutFilter);
		Long numberOfPages= 0L;
		try {
			numberOfPages = (Long)thisJoinPoint.proceed();
		} catch (Throwable e) {
			logger.error("{}.{}:{}:{}",className,methodName,e.getClass());
			throw e;
		}
		
		logger.debug("{}.{}: number of pages={}",
				className,methodName,numberOfPages);

		return numberOfPages;
	}
	@Around("ua.translate.logging.SystemArchitecture.inServiceLayer() &&"
			 + " execution(public ua.translate.model.ad.Ad getForShowing(..)) && args(adId)")
	public Ad getForShowing(ProceedingJoinPoint thisJoinPoint,long adId) throws Throwable {
		String className = thisJoinPoint.getTarget().getClass().getName();
		String methodName = thisJoinPoint.getSignature().getName();
		Ad ad= null;
		try {
			ad = (Ad)thisJoinPoint.proceed();
		} catch (Throwable e) {
			logger.error("{}.{}(adId={}):{}:{}",className,methodName,adId,e.getClass());
			throw e;
		}
		
		logger.debug("{}.{}(adId={}): Ad is successfully retrieved",
				className,methodName,adId);

		return ad;
	}
	
	@Around("ua.translate.logging.SystemArchitecture.inServiceLayer() &&"
			 + " execution(public ua.translate.model.ad.Ad getForUpdating(..)) && args(email,adId)")
	public Ad getForUpdating(ProceedingJoinPoint thisJoinPoint,String email,long adId) throws Throwable {
		String className = thisJoinPoint.getTarget().getClass().getName();
		String methodName = thisJoinPoint.getSignature().getName();
		Ad ad= null;
		try {
			ad = (Ad)thisJoinPoint.proceed();
		} catch (Throwable e) {
			logger.error("{}.{}:{}:{}",className,methodName,e.getClass());
			throw e;
		}
		
		logger.debug("{}.{}(client email ={}, adId={}): Ad is successfully retrieved",
				className,methodName,email,adId);

		return ad;
	}
	
	@Around("ua.translate.logging.SystemArchitecture.inServiceLayer() &&"
			 + " execution(public void deleteById(..)) && args(adId)")
	public void deleteById(ProceedingJoinPoint thisJoinPoint,long adId) throws Throwable {
		String className = thisJoinPoint.getTarget().getClass().getName();
		String methodName = thisJoinPoint.getSignature().getName();
		try {
			thisJoinPoint.proceed();
		} catch (Throwable e) {
			logger.error("{}.{}:{}:{}",className,methodName,e.getClass(),e.getMessage());
			throw e;
		}
		
		logger.info("{}.{}(adId={}): Ad is successfully deleted",
				className,methodName,adId);

	}
	

}

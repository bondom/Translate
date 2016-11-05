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

import ua.translate.model.Order;
import ua.translate.model.ad.Ad;
import ua.translate.model.ad.Ad.TranslateType;
import ua.translate.model.ad.Document;
import ua.translate.model.ad.RespondedAd;
import ua.translate.model.ad.ResultDocument;
import ua.translate.model.ad.WrittenAd;
import ua.translate.model.status.AdStatus;
import ua.translate.model.viewbean.SearchFilterForAds;

@Aspect
@Component
public class AdServiceAspect {
	
	Logger logger = LoggerFactory.getLogger(AdServiceAspect.class);
	
	@Around("ua.translate.logging.SystemArchitecture.inServiceLayer() &&"
			 + " execution(public long saveAd(..)) && args(ad,email,maxNumberOfAds)")
	public long saveAd(ProceedingJoinPoint thisJoinPoint,
						Ad ad,String email,long maxNumberOfAds) throws Throwable {
		String className = thisJoinPoint.getTarget().getClass().getName();
		String methodName = thisJoinPoint.getSignature().getName();
		long adId= 0L;
		try {
			adId = (long)thisJoinPoint.proceed();
		} catch (Throwable e) {
			logger.debug("{}.{}(email={},maxNumberOfAds={}):{}:{}",
					className,methodName,email,maxNumberOfAds,e.getClass(),e.getMessage());
			throw e;
		}
		
		logger.debug("{}.{}(email={},maxNumberOfAds={}): "
				+ "Ad is successfully saved:name='{}',status='{}',clientId='{}'",
				className,methodName,email,maxNumberOfAds,ad.getName(),ad.getStatus(),ad.getClient().getId());

		return adId;
	}
	
	
	@Around("ua.translate.logging.SystemArchitecture.inServiceLayer() &&"
			 + " execution(public void refreshPubDate(..)) && "
			 + "args(email,adId,hoursBetweenRefreshing)")
	public void refreshPubDate(ProceedingJoinPoint thisJoinPoint,
			String email,long adId,long hoursBetweenRefreshing) throws Throwable {
		String className = thisJoinPoint.getTarget().getClass().getName();
		String methodName = thisJoinPoint.getSignature().getName();
		try {
			thisJoinPoint.proceed();
		} catch (Throwable e) {
			logger.debug("{}.{}(email={},adId={},required hours={}):{}:{}",
					className,methodName,email,adId,hoursBetweenRefreshing,
					e.getClass(),e.getMessage());
			throw e;
		}
		
		logger.debug("{}.{}(email={},adId={},required hours={}): "
				+ "Publication DateTime of Ad is successfully refreshed ",
				className,methodName,email,adId,hoursBetweenRefreshing);

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
			logger.debug("{}.{}(adId={}):{}:{}",className,methodName,adId,e.getClass());
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
			logger.debug("{}.{}:{}:{}",className,methodName,e.getClass());
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
			logger.debug("{}.{}:{}:{}",className,methodName,e.getClass(),e.getMessage());
			throw e;
		}
		
		logger.debug("{}.{}(adId={}): Ad is successfully deleted",
				className,methodName,adId);

	}
	
	@Around("ua.translate.logging.SystemArchitecture.inServiceLayer() &&"
			 + " execution(public void deleteFromAssociationsWithClientAndArchieveAd(..)) && "
			 + "args(adId,email)")
	public void deleteFromAssociationsWithClientAndArchieveAd(ProceedingJoinPoint thisJoinPoint,
															  long adId,
															  String email) throws Throwable {
		String className = thisJoinPoint.getTarget().getClass().getName();
		String methodName = thisJoinPoint.getSignature().getName();
		try {
			thisJoinPoint.proceed();
		} catch (Throwable e) {
			logger.debug("{}.{}(adId={},client email={}):{}:{}",className,methodName,
					adId,email,e.getClass(),e.getMessage());
			throw e;
		}
		
		logger.debug("{}.{}(adId={},client email={}): Ad is successfully deleted from associations"
				+ " with client",
				className,methodName,adId,email);

	}
	
	@Around("ua.translate.logging.SystemArchitecture.inServiceLayer() &&"
			 + " execution(public void deleteFromAssociationsWithTranslatorAndArchieveAd(..)) && "
			 + "args(adId,email)")
	public void deleteFromAssociationsWithTranslatorAndArchieveAd(ProceedingJoinPoint thisJoinPoint,
															  long adId,
															  String email) throws Throwable {
		String className = thisJoinPoint.getTarget().getClass().getName();
		String methodName = thisJoinPoint.getSignature().getName();
		try {
			thisJoinPoint.proceed();
		} catch (Throwable e) {
			logger.debug("{}.{}(adId={},translator email={}):{}:{}",className,methodName,
					adId,email,e.getClass(),e.getMessage());
			throw e;
		}
		
		logger.debug("{}.{}(adId={},email={}): Ad is successfully deleted from associations"
				+ " with translator",
				className,methodName,adId,email);

	}
	
	
	

}

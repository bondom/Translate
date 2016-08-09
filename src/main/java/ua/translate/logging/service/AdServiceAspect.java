package ua.translate.logging.service;

import java.util.List;
import java.util.Set;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import ua.translate.model.ad.Ad;
import ua.translate.model.ad.ResponsedAd;

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
			logger.warn("{}.{}:{}:{}",className,methodName,e.getClass(),e.getMessage());
			throw e;
		}
		
		logger.info("{}.{}(email={}): Ad is successfully saved:name='{}',status='{}',clientId='{}'",
				className,methodName,email,ad.getName(),ad.getStatus(),ad.getClient().getId());

		return adId;
	}
	
	@Around("ua.translate.logging.SystemArchitecture.inServiceLayer() &&"
			 + " execution(public ua.translate.model.ad.Ad updateAd(..)) && args(adId,..)")
	public Ad updateAd(ProceedingJoinPoint thisJoinPoint,long adId) throws Throwable {
		String className = thisJoinPoint.getTarget().getClass().getName();
		String methodName = thisJoinPoint.getSignature().getName();
		Ad savedAd= null;
		try {
			savedAd = (Ad)thisJoinPoint.proceed();
		} catch (Throwable e) {
			logger.error("{}.{}:{}:{}",className,methodName,e.getClass(),e.getMessage());
			throw e;
		}
		
		logger.info("{}.{}(adId={}): Ad is successfully updated",
				className,methodName,adId);

		return savedAd;
	}
	
	@AfterReturning(pointcut = "ua.translate.logging.SystemArchitecture.inServiceLayer() &&"
			 + " execution(public * getAdsForShowing(..))",returning = "ads")
	public void getAdsForShowing(JoinPoint thisJoinPoint,Set<Ad> ads) throws Throwable {
		String className = thisJoinPoint.getTarget().getClass().getName();
		String methodName = thisJoinPoint.getSignature().getName();
		if(ads.size()>0){
			ads.stream().forEach(ad ->{
				logger.info("{}.{}:ad id = {}, ad name='{}', ad status= '{}', ad end date = '{}'",className,methodName,
						ad.getId(),ad.getName(),ad.getStatus(),ad.getEndDate());
			});
		}else logger.info("{}.{}: 0 ads exists",className,methodName);
	}
	
	@Around("ua.translate.logging.SystemArchitecture.inServiceLayer() &&"
			 + " (execution(public ua.translate.model.ad.Ad getForShowing(..)) ||"
			 + "execution(public ua.translate.model.ad.Ad getForUpdating(..)))&& args(adId)")
	public Ad getForShowingOrUpdating(ProceedingJoinPoint thisJoinPoint,long adId) throws Throwable {
		String className = thisJoinPoint.getTarget().getClass().getName();
		String methodName = thisJoinPoint.getSignature().getName();
		Ad ad= null;
		try {
			ad = (Ad)thisJoinPoint.proceed();
		} catch (Throwable e) {
			logger.error("{}.{}:{}:{}",className,methodName,e.getClass());
			throw e;
		}
		
		logger.info("{}.{}(adId={}): Ad is successfully retrieved",
				className,methodName,adId);

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
	
	/*@Around(value = "ua.translate.logging.SystemArchitecture.inServiceLayer() &&"
			 + " execution(public ua.translate.model.ad.Ad get(..)) && args(id)")
	public Ad getting(ProceedingJoinPoint thisJoinPoint, Long id) {
		String className = thisJoinPoint.getTarget().getClass().getName();
		String methodName = thisJoinPoint.getSignature().getName();
		Ad ad = null;
		try {
			ad = (Ad)thisJoinPoint.proceed();
		} catch (Throwable e) {
			logger.info("{}.{}:{}",className,methodName,e.getStackTrace());
			return null;
		}
		
		if(ad!=null){
			logger.info("{}.{}(id={}):advertisement is retrieved from db, name='{}',status={}",
					className,methodName,id,ad.getName(),ad.getStatus());
			List<ResponsedAd> responsedAds = ad.getResponsedAds();
			responsedAds.forEach(rad->{
				logger.info("{}.{}(id={}): ad contains - ResponsedAd with id = {},"
						+ "it translator id={}",
					className,methodName,id,rad.getId(),rad.getTranslator().getId());
			});
		}else{
			logger.info("{}.{}(id={}):ad with such id doesn't exist",
					className,methodName,id);
		}
		return ad;
	}*/

}
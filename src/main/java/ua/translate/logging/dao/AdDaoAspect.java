package ua.translate.logging.dao;

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

@Aspect
@Component
public class AdDaoAspect {

	Logger logger = LoggerFactory.getLogger(AdDaoAspect.class);
	
	@Around("ua.translate.logging.SystemArchitecture.inDaoLayer() && "
			+ "execution(Long save(ua.translate.model.ad.Ad)) && args(ad)")
	public Long saving(ProceedingJoinPoint thisJoinPoint, Ad ad) {
		String className = thisJoinPoint.getTarget().getClass().getName();
		String methodName = thisJoinPoint.getSignature().getName();
		Long id = 0L;
		try {
			id = (Long)thisJoinPoint.proceed();
		} catch (Throwable e) {
			logger.error("{}.{}:{}",className,methodName,e.getStackTrace());
			return id;
		}
		logger.debug("{}.{}: Ad  with name='{}' is successfully saved, owner(client)={}",className,methodName,ad.getName(),ad.getClient().getEmail());
		return id;
	}
	
	@Around(value = "ua.translate.logging.SystemArchitecture.inDaoLayer() &&"
			 + " execution(public ua.translate.model.ad.Ad get(*)) && args(id)")
	public Ad getting(ProceedingJoinPoint thisJoinPoint, Long id) {
		String className = thisJoinPoint.getTarget().getClass().getName();
		String methodName = thisJoinPoint.getSignature().getName();
		Ad ad = null;
		try {
			ad = (Ad)thisJoinPoint.proceed();
		} catch (Throwable e) {
			logger.error("{}.{}:{}",className,methodName,e.getStackTrace());
			return null;
		}
		
		if(ad!=null){
			logger.debug("{}.{}(id={}):advertisement is retrieved from db, name='{}',status={}",
					className,methodName,id,ad.getName(),ad.getStatus());
		}else{
			logger.error("{}.{}(id={}):ad with such id doesn't exist",
					className,methodName,id);
		}
		return ad;
	}
	
	@AfterReturning(value = "ua.translate.logging.SystemArchitecture.inDaoLayer() &&"
			 + " execution(* getAllAds())",returning = "ads")
	public void getAllAds(JoinPoint thisJoinPoint, Set<Ad> ads) {
		String className = thisJoinPoint.getTarget().getClass().getName();
		String methodName = thisJoinPoint.getSignature().getName();
		
		ads.stream().forEach(ad->{
			logger.debug("{}.{}: {} ad is retrieved from db: id = {}name={}, status={}, publicationDateTime={}, endDate={}",
					className,methodName,ad.getId(),ad.getName(),ad.getStatus(),ad.getPublicationDateTime(),ad.getEndDate());
		});
	}
	
	@AfterReturning(value = "ua.translate.logging.SystemArchitecture.inDaoLayer() &&"
			 + " execution(* getAdsForShowing())",returning = "ads")
	public void getAdsForShowing(JoinPoint thisJoinPoint, Set<Ad> ads) {
		String className = thisJoinPoint.getTarget().getClass().getName();
		String methodName = thisJoinPoint.getSignature().getName();
		
		ads.stream().forEach(ad->{
			logger.debug("{}.{}: id={} SHOWED ad is retrieved from db: id = {}name={}, publicationDateTime={}, endDate={}",
					className,methodName,ad.getId(),ad.getName(),ad.getPublicationDateTime(),ad.getEndDate());
		});
	}
}

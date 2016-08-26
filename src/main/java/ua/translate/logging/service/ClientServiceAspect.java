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
import ua.translate.model.ad.RespondedAd;

@Aspect
@Component
public class ClientServiceAspect {
	
	Logger logger = LoggerFactory.getLogger(ClientServiceAspect.class);
	
	@Around("ua.translate.logging.SystemArchitecture.inServiceLayer() &&"
			 + " execution(public * getAds(..)) && args(email)")
	public Set<Ad> getAds(ProceedingJoinPoint thisJoinPoint,String email) throws Throwable {
		String className = thisJoinPoint.getTarget().getClass().getName();
		String methodName = thisJoinPoint.getSignature().getName();
		Set<Ad> ads = null;
		try {
			ads = (Set<Ad>)thisJoinPoint.proceed();
		} catch (Throwable e) {
			logger.error("{}.{}:{}:{}",className,methodName,e.getClass(),e.getMessage());
			throw e;
		}
		if(ads.size()>0){
			ads.stream().forEach(ad->{
				logger.debug("{}.{}(email={}): ad id = {}, ad name='{}', ad status= '{}'",className,methodName,email
					,ad.getId(),ad.getName(),ad.getStatus());
			});
		}else logger.debug("{}.{}(email={}): 0 ads",className,methodName);
		return ads;
	}
	
}

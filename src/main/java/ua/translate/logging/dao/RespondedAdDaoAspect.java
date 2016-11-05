package ua.translate.logging.dao;


import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import ua.translate.model.ad.RespondedAd;

@Aspect
@Component
public class RespondedAdDaoAspect {

	Logger logger = LoggerFactory.getLogger(RespondedAdDaoAspect.class);
	
	@Around("ua.translate.logging.SystemArchitecture.inDaoLayer() && "
			+ "execution(Long save(ua.translate.model.ad.RespondedAd)) && args(respondedAd)")
	public Long saving(ProceedingJoinPoint thisJoinPoint, RespondedAd respondedAd) {
		String className = thisJoinPoint.getTarget().getClass().getName();
		String methodName = thisJoinPoint.getSignature().getName();
		Long id = 0L;
		try {
			id = (Long)thisJoinPoint.proceed();
		} catch (Throwable e) {
			logger.error("{}.{}:{}",className,methodName,e.getStackTrace());
			return id;
		}
		logger.debug("{}.{}: RespondedAd is successfully saved with id={}, "
				+ "owner(client)={},translator={},ad={}",
				className,methodName,id,respondedAd.getClient().getEmail(),
				respondedAd.getTranslator().getEmail(),respondedAd.getAd().getName());
		return id;
	}
	
	
}

package ua.translate.logging.dao;

import java.util.List;

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
public class ResponsedAdDaoAspect {

	Logger logger = LoggerFactory.getLogger(ResponsedAdDaoAspect.class);
	
	@Around("ua.translate.logging.SystemArchitecture.inDaoLayer() && "
			+ "execution(Long save(ua.translate.model.ad.ResponsedAd)) && args(responsedAd)")
	public Long saving(ProceedingJoinPoint thisJoinPoint, ResponsedAd responsedAd) {
		String className = thisJoinPoint.getTarget().getClass().getName();
		String methodName = thisJoinPoint.getSignature().getName();
		Long id = 0L;
		try {
			id = (Long)thisJoinPoint.proceed();
		} catch (Throwable e) {
			logger.error("{}.{}:{}",className,methodName,e.getStackTrace());
			return id;
		}
		logger.debug("{}.{}: ResponsedAd  is successfully saved with id={}, owner(client)={},translator={},ad={}",
				className,methodName,id,responsedAd.getClient().getEmail(),responsedAd.getTranslator().getEmail(),responsedAd.getAd().getName());
		return id;
	}
	
	
}

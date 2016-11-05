package ua.translate.logging.service;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Aspect
@Component
public class RespondedAdServiceAspect {
	
	Logger logger = LoggerFactory.getLogger(RespondedAdServiceAspect.class);
	
	@Around("ua.translate.logging.SystemArchitecture.inServiceLayer() &&"
			 + " execution(public void accept(..)) && args(email,radId)")
	public void accept(ProceedingJoinPoint thisJoinPoint,String email,long radId) throws Throwable {
		String className = thisJoinPoint.getTarget().getClass().getName();
		String methodName = thisJoinPoint.getSignature().getName();
		try {
			thisJoinPoint.proceed();
		} catch (Throwable e) {
			logger.error("{}.{}(client email={},radId={}):{}:{}",className,methodName,
					email,radId,e.getClass(),e.getMessage());
			throw e;
		}
		
		logger.info("{}.{}(client email={},radId={}): RespondedAd is successfully accepted",
				className,methodName,email,radId);
	}
	
	@Around("ua.translate.logging.SystemArchitecture.inServiceLayer() &&"
			 + " execution(public void reject(..)) && args(email,radId)")
	public void reject(ProceedingJoinPoint thisJoinPoint,String email,long radId) throws Throwable {
		String className = thisJoinPoint.getTarget().getClass().getName();
		String methodName = thisJoinPoint.getSignature().getName();
		try {
			thisJoinPoint.proceed();
		} catch (Throwable e) {
			logger.error("{}.{}(email={},radId={}):{}:{}",className,methodName,
					email,radId,e.getClass(),e.getMessage());
			throw e;
		}
		
		logger.info("{}.{}(email={},radId={}): RespondedAd is successfully rejected",
				className,methodName,email,radId);
	}
}

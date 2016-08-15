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

import ua.translate.model.Translator;

@Aspect
@Component
public class TranslatorServiceAspect {
	
	Logger logger = LoggerFactory.getLogger(TranslatorServiceAspect.class);

	@AfterReturning(pointcut = "ua.translate.logging.SystemArchitecture.inServiceLayer() &&"
			 + " execution(public * getTranslators(..))",
			 returning = "translators")
	public void getTranslators(JoinPoint thisJoinPoint,Set<Translator> translators) {
		String className = thisJoinPoint.getTarget().getClass().getName();
		String methodName = thisJoinPoint.getSignature().getName();
		if(translators.size()>0){
			translators.forEach(trs->{
				logger.debug("{}.{}:{} {},{}",className,methodName,
						trs.getFirstName(),trs.getLastName(),trs.getPublishingTime());
			});
		}else logger.debug("{}.{}: 0 translators",className,methodName);
	}
	
	@Around("ua.translate.logging.SystemArchitecture.inServiceLayer() &&"
			 + " execution(public long getNumberOfPagesForTranslators(..)) "
			 + "&& args(numberOfTranslatorsOnPage)")
	public Long getNumberOfPagesForTranslators(ProceedingJoinPoint thisJoinPoint,
											   long numberOfTranslatorsOnPage) 
											   throws Throwable {
		String className = thisJoinPoint.getTarget().getClass().getName();
		String methodName = thisJoinPoint.getSignature().getName();
		Long numberOfPages= 0L;
		try {
			numberOfPages = (Long)thisJoinPoint.proceed();
		} catch (Throwable e) {
			logger.error("{}.{}:{}:{}",className,methodName,e.getClass());
			throw e;
		}
		
		logger.info("{}.{}(numberOfTranslatorsOnPage={}): number of pages={}",
				className,methodName,numberOfTranslatorsOnPage,numberOfPages);

		return numberOfPages;
	}
	
	@Around(value = "ua.translate.logging.SystemArchitecture.inServiceLayer() &&"
			 + " execution(public void saveResponsedAd(..)) && "
			 + "args(email,adId)")
	public void saveResponsedAd(ProceedingJoinPoint thisJoinPoint, String email,
								long adId) throws Throwable {
		String className = thisJoinPoint.getTarget().getClass().getName();
		String methodName = thisJoinPoint.getSignature().getName();
		try {
			thisJoinPoint.proceed();
		} catch (Throwable e) {
			logger.error("{}.{}:{}:{}",className,methodName,e.getClass(),e.getMessage());
			throw e;
		}
		
		logger.info("{}.{}:Responsed Ad is successfully saved "
				+ "for translator with email={} and is related to Ad with id={}",
				className,methodName,email,adId);
	}
	

	@SuppressWarnings("null")
	@Around(value = "ua.translate.logging.SystemArchitecture.inServiceLayer() &&"
			 + " execution(public ua.translate.model.Translator getTranslatorById(..)) && "
			 + "args(id)")
	public Translator getTranslatorById(ProceedingJoinPoint thisJoinPoint, long id) throws Throwable {
		String className = thisJoinPoint.getTarget().getClass().getName();
		String methodName = thisJoinPoint.getSignature().getName();
		Translator translator = null;
		try {
			translator = (Translator)thisJoinPoint.proceed();
		} catch (Throwable e) {
			logger.error("{}.{}:{}:{}",className,methodName,e.getClass(),e.getMessage());
			throw e;
		}
		
		logger.debug("{}.{}:translator with id = {} is retrieved from db, "
				+ "email='{}',languages={},numberOfExecutedAds={},rating={}",
				className,methodName,id,translator.getEmail(),translator.getLanguages(),
				translator.getNumberOfExecutedAds(),translator.getRating());
		return translator;
	}
}

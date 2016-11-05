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

import ua.translate.model.Client;
import ua.translate.model.Translator;
import ua.translate.model.ad.RespondedAd;

@Aspect
@Component
public class TranslatorServiceAspect {
	
	Logger logger = LoggerFactory.getLogger(TranslatorServiceAspect.class);

	
	@Around("ua.translate.logging.SystemArchitecture.inServiceLayer() &&"
			 + " execution(public ua.translate.model.Translator getTranslatorById(..)) "
			 + "&& args(id)")
	public Translator getTranslatorById(ProceedingJoinPoint thisJoinPoint,
											   long id) 
											   throws Throwable {
		String className = thisJoinPoint.getTarget().getClass().getName();
		String methodName = thisJoinPoint.getSignature().getName();
		Translator translator= null;
		try {
			translator = (Translator)thisJoinPoint.proceed();
		} catch (Throwable e) {
			logger.error("{}.{}(id={}):{}:{}",className,methodName,id,e.getClass(),e.getMessage());
			throw e;
		}
		
		logger.debug("{}.{}(id={}):Translator data: email={},firstName={},lastName={},"
				+"languages={},rating={},number of executed Ads={}",
				className,methodName,id,translator.getEmail(),translator.getFirstName(),
				translator.getLastName(),translator.getLanguages(),translator.getRating(),
				translator.getNumberOfExecutedAds());

		return translator;
	}
	
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
			 + " execution(public long saveRespondedAd(..)) "
			 + "&& args(email,adId,maxNumberOfSendedRad)")
	public Long saveRespondedAd(ProceedingJoinPoint thisJoinPoint,
								String email, long adId,int maxNumberOfSendedRad) 
																throws Throwable {
		String className = thisJoinPoint.getTarget().getClass().getName();
		String methodName = thisJoinPoint.getSignature().getName();
		Long generatedId= 0L;
		try {
			generatedId = (Long)thisJoinPoint.proceed();
		} catch (Throwable e) {
			logger.error("{}.{}(translator email={},adId={},"
					+ "maxNumberOfSendedRespondedAd={}):{}",
					className,methodName,email,adId,maxNumberOfSendedRad,e.getClass());
			throw e;
		}
		
		logger.info("{}.{}(translator email={},adId={},"
					+ "maxNumberOfSendedRespondedAd={}):generated id={}",
					className,methodName,email,adId,maxNumberOfSendedRad,generatedId);

		return generatedId;
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
	
	@Around("ua.translate.logging.SystemArchitecture.inServiceLayer() &&"
			 + " execution(public ua.translate.model.ad.RespondedAd getCurrentOrder(..)) "
			 + "&& args(email)")
	public RespondedAd getCurrentOrder(ProceedingJoinPoint thisJoinPoint,
											   String email) 
											   throws Throwable {
		String className = thisJoinPoint.getTarget().getClass().getName();
		String methodName = thisJoinPoint.getSignature().getName();
		RespondedAd currentOrder= null;
		try {
			currentOrder= (RespondedAd)thisJoinPoint.proceed();
		} catch (Throwable e) {
			logger.error("{}.{}(translator email={}):{}:{}",className,methodName,
					email,e.getClass());
			throw e;
		}
		if(currentOrder==null){
			logger.debug("{}.{}(translator email={}): RespondedAd with ACCEEPTED status doesn't exist",
					className,methodName,email);
		}else{
			Client client = currentOrder.getClient();
			String clientEmail;
			if(client==null){
				clientEmail="null";
			}else{
				clientEmail=client.getEmail();
			}
			logger.debug("{}.{}(translator email={}): RespondedAd[id={},ad name={},client email={}]",
				className,methodName,email,currentOrder.getId(),
				currentOrder.getAd().getName(),clientEmail);
		}
		return currentOrder;
	}
	
	@Around("ua.translate.logging.SystemArchitecture.inServiceLayer() &&"
			 + " execution(public boolean markAsNotChecked(..)) "
			 + "&& args(email,adId)")
	public boolean markAsNotChecked(ProceedingJoinPoint thisJoinPoint,
											   String email, long adId) 
											   throws Throwable {
		String className = thisJoinPoint.getTarget().getClass().getName();
		String methodName = thisJoinPoint.getSignature().getName();
		boolean marked= false;
		try {
			marked= (boolean)thisJoinPoint.proceed();
		} catch (Throwable e) {
			logger.error("{}.{}:{}:{}",className,methodName,e.getClass());
			throw e;
		}
		
		logger.debug("{}.{}(email={},adId={}): marked={}",
				className,methodName,email,adId,marked);

		return marked;
	}
	
	

}

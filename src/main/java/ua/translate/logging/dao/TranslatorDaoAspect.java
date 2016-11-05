package ua.translate.logging.dao;

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
public class TranslatorDaoAspect {
	
	Logger logger = LoggerFactory.getLogger(TranslatorDaoAspect.class);
	
	@Around(value = "ua.translate.logging.SystemArchitecture.inDaoLayer() &&"
			 + " execution(ua.translate.model.Translator get(*)) && args(id)")
	public Translator getting(ProceedingJoinPoint thisJoinPoint, Long id) {
		String className = thisJoinPoint.getTarget().getClass().getName();
		String methodName = thisJoinPoint.getSignature().getName();
		Translator translator = null;
		try {
			translator = (Translator)thisJoinPoint.proceed();
		} catch (Throwable e) {
			logger.error("{}.{}:{}",className,methodName,e.getStackTrace());
			return null;
		}
		
		if(translator!=null){
			logger.debug("{}.{}(id={}):translator is retrieved from db, email={}",
					className,methodName,id,translator.getEmail());
		}else{
			logger.error("{}.{}(id={}):translator with such id doesn't exist",
					className,methodName,id);
		}
		return translator;
	}
	
	@AfterReturning(value = "ua.translate.logging.SystemArchitecture.inDaoLayer() &&"
			 + " execution(* getTranslators(..))",returning = "translators")
	public void getTranslators(JoinPoint thisJoinPoint, Set<Translator> translators) {
		String className = thisJoinPoint.getTarget().getClass().getName();
		String methodName = thisJoinPoint.getSignature().getName();
		
		translators.forEach(translator ->{
		logger.debug("{}.{}:translator is retrieved from db: email={},id={},"
				+ "languages={}",
				className,methodName,translator.getEmail(),translator.getId(),
				translator.getLanguages().toString());
		});
	}
	
	@Around(value = "ua.translate.logging.SystemArchitecture.inDaoLayer() &&"
			 + " execution(ua.translate.model.Translator getTranslatorByEmail(*)) && args(email)")
	public Translator getTranslatorByEmail(ProceedingJoinPoint thisJoinPoint, String email) {
		String className = thisJoinPoint.getTarget().getClass().getName();
		String methodName = thisJoinPoint.getSignature().getName();
		Translator translator = null;
		try {
			translator = (Translator)thisJoinPoint.proceed();
		} catch (Throwable e) {
			logger.error("{}.{}:{}",className,methodName,e.getStackTrace());
			return null;
		}
		
		if(translator!=null){
			logger.debug("{}.{}(email={}):translator is retrieved from db, emailStatus={},confirmation url = {}",
					className,methodName,email,translator.getEmailStatus(),translator.getConfirmationUrl());
		}else{
			logger.error("{}.{}(email={}):translator with such email doesn't exist",
					className,methodName,email);
		}
		return translator;
	}
}

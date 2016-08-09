package ua.translate.logging.dao;

import java.io.Serializable;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import ua.translate.model.Client;
import ua.translate.model.Translator;
import ua.translate.model.User;


@Aspect
@Component
public class AbstractUserDaoAspect {
	
	Logger logger = LoggerFactory.getLogger(AbstractUserDaoAspect.class);
	
	@Around("ua.translate.logging.SystemArchitecture.inDaoLayer() && "
			+ "(execution(Long save(ua.translate.model.Client)) || "
			+ "execution(Long save(ua.translate.model.Translator))) && args(user)")
	public Long saving(ProceedingJoinPoint thisJoinPoint, User user) {
		String className = thisJoinPoint.getTarget().getClass().getName();
		String methodName = thisJoinPoint.getSignature().getName();
		Long id = 0L;
		try {
			id = (Long)thisJoinPoint.proceed();
		} catch (Throwable e) {
			logger.error("{}.{}:{}",className,methodName,e.getStackTrace());
			return id;
		}
		logger.debug("{}.{}: user of {}  with email={} is successfully saved with id ={}",className,methodName,user.getClass(),user.getEmail(),id);
		return id;
	}
	
	

}

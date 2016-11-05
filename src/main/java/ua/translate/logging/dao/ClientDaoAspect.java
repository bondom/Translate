package ua.translate.logging.dao;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import ua.translate.model.Client;

@Aspect
@Component
public class ClientDaoAspect {

	Logger logger = LoggerFactory.getLogger(ClientDaoAspect.class);
	
	@Around(value = "ua.translate.logging.SystemArchitecture.inDaoLayer() &&"
			 + " execution(ua.translate.model.Client getClientByEmail(*)) && args(email)")
	public Client getClientByEmail(ProceedingJoinPoint thisJoinPoint, String email) {
		String className = thisJoinPoint.getTarget().getClass().getName();
		String methodName = thisJoinPoint.getSignature().getName();
		Client client = null;
		try {
			client = (Client)thisJoinPoint.proceed();
		} catch (Throwable e) {
			logger.error("{}.{}:{}",className,methodName,e.getClass());
			e.printStackTrace();
			return null;
		}
		
		if(client!=null){
			logger.debug("{}.{}(email={}):client is retrieved from db, emailStatus={},confirmation url = {}",
					className,methodName,email,client.getEmailStatus(),client.getConfirmationUrl());
		}else{
			logger.error("{}.{}(email={}):client with such email doesn't exist",
					className,methodName,email);
		}
		return client;
	}
}

package ua.translate.logging.service;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import ua.translate.model.Client;
import ua.translate.model.Translator;
import ua.translate.model.User;
import ua.translate.service.exception.DuplicateEmailException;
import ua.translate.service.exception.EmailIsConfirmedException;
import ua.translate.service.exception.InvalidPasswordException;


@Aspect
@Component
public class UserServiceAspect {
	
	Logger logger = LoggerFactory.getLogger(UserServiceAspect.class);
	
	
	@Around(value = "ua.translate.logging.SystemArchitecture.inServiceLayer() &&"
			 + " execution(public void registerUser(..)) && "
			 + "args(user)")
	public void registerUser(ProceedingJoinPoint thisJoinPoint, User user) throws Throwable {
		String className = thisJoinPoint.getTarget().getClass().getName();
		String methodName = thisJoinPoint.getSignature().getName();
		try {
			thisJoinPoint.proceed();
		} catch (Throwable e) {
			logger.error("{}.{}:{}:{}",className,methodName,e.getClass(),e.getMessage());
			throw e;
		}
		
		logger.info("{}.{}:New user with email='{}' is successfully registered!",
				className,methodName,user.getEmail());
	}
	
	@Around(value = "ua.translate.logging.SystemArchitecture.inServiceLayer() &&"
			 + " execution(public void updateUserEmail(..)) && "
			 + "args(email,newEmail,password)")
	public void updateUserEmail(ProceedingJoinPoint thisJoinPoint, String email,
								String newEmail,String password) throws Throwable {
		String className = thisJoinPoint.getTarget().getClass().getName();
		String methodName = thisJoinPoint.getSignature().getName();
		try {
			thisJoinPoint.proceed();
		} catch (Throwable e) {
			logger.error("{}.{}:{}:{}",className,methodName,e.getClass(),e.getMessage());
			throw e;
		}
		
		logger.info("{}.{}:user's email='{}' is successfully replaced with new email='{}',password='{}'",
				className,methodName,email,newEmail,password);
	}
	
	@Around(value = "ua.translate.logging.SystemArchitecture.inServiceLayer() &&"
			 + " execution(public void updateUserPassword(..)) && "
			 + "args(email,password,newPassword)")
	public void updateUserPassword(ProceedingJoinPoint thisJoinPoint, String email,
									String password,String newPassword) throws Throwable {
		String className = thisJoinPoint.getTarget().getClass().getName();
		String methodName = thisJoinPoint.getSignature().getName();
		try {
			thisJoinPoint.proceed();
		} catch (Throwable e) {
			logger.error("{}.{}:{}:{}",className,methodName,e.getClass(),e.getMessage());
			throw e;
		}
		
		logger.info("{}.{}:user's password='{}' is successfully replaced with new password='{}',email='{}'",
				className,methodName,password,newPassword,email);
	}
	
	
	@Around(value = "ua.translate.logging.SystemArchitecture.inServiceLayer() &&"
			 + " execution(public String saveConfirmationUrl(..)) && "
			 + "args(email)")
	public String saveConfirmationUrl(ProceedingJoinPoint thisJoinPoint, String email) throws Throwable {
		String className = thisJoinPoint.getTarget().getClass().getName();
		String methodName = thisJoinPoint.getSignature().getName();
		String url = "";
		try {
			url = (String)thisJoinPoint.proceed();
		} catch (Throwable e) {
			logger.error("{}.{}:{}:{}",className,methodName,e.getClass(),e.getMessage());
			throw e;
		}
		
		logger.info("{}.{}:confirmation url='{}' is successfully saved for user with email='{}'",
				className,methodName,url,email);
		return url;
	}
	
	
	@AfterReturning(pointcut = "ua.translate.logging.SystemArchitecture.inServiceLayer() &&"
			 + " execution(public void updateUserProfile(..))")
	public void updateUserProfileSuccess(JoinPoint thisJoinPoint) {
		String className = thisJoinPoint.getTarget().getClass().getName();
		String methodName = thisJoinPoint.getSignature().getName();
		logger.info("{}.{}:user's profile is successfully updated.",className,methodName);
	}
	
	@Around(value = "ua.translate.logging.SystemArchitecture.inServiceLayer() &&"
			 + " execution(public void updateAvatar(..)) && args(email,avatar)")
	public void updateAvatar(ProceedingJoinPoint thisJoinPoint,
									String email,byte[] avatar) throws Throwable {
		String className = thisJoinPoint.getTarget().getClass().getName();
		String methodName = thisJoinPoint.getSignature().getName();
		try {
			thisJoinPoint.proceed();
		} catch (Throwable e) {
			logger.error("{}.{}:{}:{}",className,methodName,e.getClass(),e.getMessage());
			throw e;
		}
		logger.info("{}.{}:avatar is successfully updated for user with email={},size={}",
				className,methodName,email,avatar.length);
	}

}

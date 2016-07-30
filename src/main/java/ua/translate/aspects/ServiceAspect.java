package ua.translate.aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import ua.translate.model.User;


@Aspect
@Component
public class ServiceAspect {
	
	Logger logger = LoggerFactory.getLogger(ServiceAspect.class);
	
	@Around("execution(* ua.translate.service.UserService.isEmailUnique(..))")
	public boolean checkEmailUniqueness(ProceedingJoinPoint thisJoinPoint) throws Throwable{
		String className = thisJoinPoint.getTarget().getClass().getName();
		String methodName = thisJoinPoint.getSignature().getName();
		String email = (String)(thisJoinPoint.getArgs()[0]);
		boolean result = (boolean) thisJoinPoint.proceed();
		logger.debug("{}.{}({}):{}",className,methodName,email,result);
		return result;
		
	}
	
	@Around("execution(* ua.translate.service.UserService.isEmailChanged(..))")
	public boolean checkEmailChange(ProceedingJoinPoint thisJoinPoint) throws Throwable{
		String className = thisJoinPoint.getTarget().getClass().getName();
		String methodName = thisJoinPoint.getSignature().getName();
		String oldEmail = (String)(thisJoinPoint.getArgs()[0]);
		String newEmail = (String)(thisJoinPoint.getArgs()[1]);
		boolean result = (boolean) thisJoinPoint.proceed();
		logger.debug("{}.{}({},{}):{}",className,methodName,oldEmail,newEmail,result);
		return result;
		
	}
	
	@AfterReturning(pointcut = "execution(* ua.translate.service.UserService.registerUser(..))",
					returning = "id")
	public void registerUser(JoinPoint thisJoinPoint,long id) {
		String className = thisJoinPoint.getTarget().getClass().getName();
		String methodName = thisJoinPoint.getSignature().getName();
		logger.info("{}.{}:New User with id={} is registered!",className,methodName,id);
	}
	
	@Around("execution(* ua.translate.service.UserService.getUserByEmail(..))")
	public User getUserByEmail(ProceedingJoinPoint thisJoinPoint) throws Throwable{
		String className = thisJoinPoint.getTarget().getClass().getName();
		String methodName = thisJoinPoint.getSignature().getName();
		String email = (String)(thisJoinPoint.getArgs()[0]);
		User user = (User) thisJoinPoint.proceed();
		if(user!=null){
			long avatarSize = 0;
			if(user.getAvatar()!=null){
				avatarSize = user.getAvatar().length;
			}
			logger.debug("{}.{}({}):Retrieved user: id={},role={},status={},avatarSize={}",className,methodName,email,
						user.getId(),user.getRole(),user.getStatus(),avatarSize);
		}
		return user;
		
	}
	
	@Around("execution(* ua.translate.service.UserService.getUserById(..))")
	public User getUserById(ProceedingJoinPoint thisJoinPoint) throws Throwable{
		String className = thisJoinPoint.getTarget().getClass().getName();
		String methodName = thisJoinPoint.getSignature().getName();
		Long id = (Long)(thisJoinPoint.getArgs()[0]);
		User user = (User) thisJoinPoint.proceed();
		if(user!=null){
		logger.debug("{}.{}({}):Retrieved user: email={},role={},status={}",className,methodName,id,
					user.getEmail(),user.getRole(),user.getStatus());
		}
		return user;
		
	}
	
	@AfterReturning("execution(* ua.translate.service.UserService.confirmEmail(..))")
	public void confirmEmail(JoinPoint thisJoinPoint) {
		String className = thisJoinPoint.getTarget().getClass().getName();
		String methodName = thisJoinPoint.getSignature().getName();
		String email = (String)(thisJoinPoint.getArgs()[0]);
		logger.info("{}.{}({}): User's email confirmed!",className,methodName,email);
	}
	
	@AfterReturning(pointcut="execution(* ua.translate.service.UserService.generateConfirmUrl(..))",
					returning="url")
	public void generateConfirmUrl(JoinPoint thisJoinPoint,String url) {
		String className = thisJoinPoint.getTarget().getClass().getName();
		String methodName = thisJoinPoint.getSignature().getName();
		Long id = (Long)(thisJoinPoint.getArgs()[0]);
		logger.debug("{}.{}({}): Generated url={} ",className,methodName,id,url);
	}
	
	@AfterReturning(pointcut="execution(* ua.translate.service.UserService.sendConfirmLetter(..)) &&"
			+ "args(email)")
	public void sendConfirmLetter(JoinPoint thisJoinPoint, String email) {
		String className = thisJoinPoint.getTarget().getClass().getName();
		String methodName = thisJoinPoint.getSignature().getName();
		logger.debug("{}.{}: Letter is sended to user on email {} ",className,methodName,email);
	}
	
	@AfterReturning(pointcut = "execution(* ua.translate.service.UserService.isPasswordRight(..))",
					returning = "isPasswordRight")
	public void isPasswordRight(JoinPoint thisJoinPoint,boolean isPasswordRight) {
		String className = thisJoinPoint.getTarget().getClass().getName();
		String methodName = thisJoinPoint.getSignature().getName();
		logger.debug("{}.{}:{}",className,methodName,isPasswordRight);
	}
	
	@Before("execution(* ua.translate.service.UserService.editUserProfile(..)) && "
			+ "args(email,newUser)")
	public void editUserProfile(JoinPoint thisJoinPoint,String email,User newUser){
		String className = thisJoinPoint.getTarget().getClass().getName();
		String methodName = thisJoinPoint.getSignature().getName();
		logger.info("{}.{}:Profile of user with email={} updated!",
					className,methodName,email);
	}
	
	@Before("execution(* ua.translate.service.UserService.editUserEmail(..)) && "
			+ "args(oldEmail,newEmail,..)")
	public void editEmail(JoinPoint thisJoinPoint,String oldEmail,String newEmail){
		String className = thisJoinPoint.getTarget().getClass().getName();
		String methodName = thisJoinPoint.getSignature().getName();
		logger.info("{}.{}:oldEmail={},newEmail={}",
					className,methodName,oldEmail,newEmail);
	}
	
	@Before("execution(* ua.translate.service.UserService.editUserPassword(..)) && "
			+ "args(email,newPassword,..)")
	public void editPassword(JoinPoint thisJoinPoint,String email,String newPassword){
		String className = thisJoinPoint.getTarget().getClass().getName();
		String methodName = thisJoinPoint.getSignature().getName();
		logger.info("{}.{}:email={},new password={}",
					className,methodName,email,newPassword);
	}
}

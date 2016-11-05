package ua.translate.logging.web;


import java.security.Principal;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;

import ua.translate.model.UserEntity;
import ua.translate.model.viewbean.ChangeEmailBean;
import ua.translate.model.viewbean.ChangePasswordBean;

//@Aspect
public class ControllerAspect {
	
	/*Logger logger = LoggerFactory.getLogger(ControllerAspect.class);
	
	@Around("execution(* ua.translate.controller.ClientController.registration(..))")
	public ModelAndView registration(ProceedingJoinPoint joinPoint) throws Throwable{
		String className = joinPoint.getTarget().getClass().getName();
		String methodName = joinPoint.getSignature().getName();
		User user = (User)(joinPoint.getArgs()[0]);
		BindingResult result = (BindingResult)(joinPoint.getArgs()[1]);
		
		logger.debug("{}.{}:Properties of inputted user data: {},{},{},{},{},{},{}",className,methodName,
				user.getFirstName(),user.getLastName(),user.getCountry(),user.getCity(),
				user.getEmail(),user.getPhoneNumber(),user.getBirthday());
		if(result.hasErrors()){
			result.getAllErrors().forEach(error->{
				logger.debug("{}.{}:Validator:{}",error.getDefaultMessage());
			});
		}

		ModelAndView model = (ModelAndView)joinPoint.proceed();
		logger.debug("{}.{}: view name:{}",className,methodName,model.getViewName());
		return model;
	}
	
	
	@Around("execution(* ua.translate.controller.ClientController.saveEmail(..)) &&"
			+ "args(changeEmailBean,changeEmailResult,..)")									   
	public ModelAndView saveEmail(ProceedingJoinPoint joinPoint,
								 ChangeEmailBean changeEmailBean, 
								 BindingResult changeEmailResult) throws Throwable{
		String className = joinPoint.getTarget().getClass().getName();
		String methodName = joinPoint.getSignature().getName();
		
		logger.debug("{}.{}:newEmail={},newEmailAgain={},password={}",className,methodName,
					changeEmailBean.getNewEmail(),changeEmailBean.getNewEmailAgain(),
					changeEmailBean.getCurrentPassword());
		if(changeEmailResult.hasErrors()){
			changeEmailResult.getAllErrors().forEach(error->{
				logger.debug("{}.{}:Validator:{}-{}",className,methodName,error.getObjectName(),error.getDefaultMessage());
			});
		}
		
		
		ModelAndView model = (ModelAndView)joinPoint.proceed();
		logger.debug("{}.{}: view name={}",className,methodName,model.getViewName());
		return model;
		
	}
	
	@Around("execution(* ua.translate.controller.ClientController.savePassword(..)) &&"
			+ "args(changePasswordBean,changePasswordResult,..)")									   
	public ModelAndView savePassword(ProceedingJoinPoint joinPoint,
								 ChangePasswordBean changePasswordBean, 
								 BindingResult changePasswordResult) throws Throwable{
		String className = joinPoint.getTarget().getClass().getName();
		String methodName = joinPoint.getSignature().getName();
		
		logger.debug("{}.{}:oldPassword={},newPassword={},newPasswordAgain={}",className,methodName,
				changePasswordBean.getOldPassword(),changePasswordBean.getNewPassword(),
				changePasswordBean.getNewPasswordAgain());
		if(changePasswordResult.hasErrors()){
			changePasswordResult.getAllErrors().forEach(error->{
				logger.debug("{}.{}:Validator:{}-{}",className,methodName,error.getObjectName(),error.getDefaultMessage());
			});
		}
		
		ModelAndView model = (ModelAndView)joinPoint.proceed();
		logger.debug("{}.{}: view name={}",className,methodName,model.getViewName());
		return model;
	}
	
	@Around("execution(* ua.translate.controller.ClientController.emailConfirmation(..)) &&"
			+ "args(confirmUrl,..)")									   
	public ModelAndView emailConfirmation(ProceedingJoinPoint joinPoint,String confirmUrl) throws Throwable{
		String className = joinPoint.getTarget().getClass().getName();
		String methodName = joinPoint.getSignature().getName();
		ModelAndView model = (ModelAndView)joinPoint.proceed();
		logger.debug("{}.{}: retrieved url = {},returned view name={}",
				className,methodName,confirmUrl,model.getViewName());
		return model;
	}

	@Around("execution(* ua.translate.controller.ClientController.saveProfileEdits(..)) &&"
			+ "args(editedUser,userResult,..)")									   
	public ModelAndView saveProfileEdits(ProceedingJoinPoint joinPoint,
								 User editedUser,BindingResult userResult) throws Throwable{
		String className = joinPoint.getTarget().getClass().getName();
		String methodName = joinPoint.getSignature().getName();
		
		logger.debug("{}.{}:Properties of inputted user data: {},{},{},{},{},{}",className,methodName,
				editedUser.getFirstName(),editedUser.getLastName(),editedUser.getCountry(),editedUser.getCity(),
				editedUser.getPhoneNumber(),editedUser.getBirthday());
		if(userResult.hasErrors()){
			userResult.getAllErrors().forEach(error->{
				logger.debug("{}.{}:Validator:{}-{}",className,methodName,error.getObjectName(),error.getDefaultMessage());
			});
		}
		
		ModelAndView model = (ModelAndView)joinPoint.proceed();
		logger.debug("{}.{}: view name={}",className,methodName,model.getViewName());
		return model;
		
	}
	
	
	@AfterReturning(pointcut = "execution(* ua.translate.controller.ClientController.loginForm(..))",
			returning = "model")									   
	public void loginForm(JoinPoint joinPoint,ModelAndView model) throws Throwable{
		String className = joinPoint.getTarget().getClass().getName();
		String methodName = joinPoint.getSignature().getName();
		logger.debug("{}.{}: view name={}",className,methodName,model.getViewName());
	}
	
	
	@AfterReturning(pointcut = "execution(* ua.translate.controller.ClientController.editProfile(..))",
			returning = "model")									   
	public void editProfile(JoinPoint joinPoint,ModelAndView model) throws Throwable{
		String className = joinPoint.getTarget().getClass().getName();
		String methodName = joinPoint.getSignature().getName();
		logger.debug("{}.{}: view name={}",className,methodName,model.getViewName());
	}*/
	
}

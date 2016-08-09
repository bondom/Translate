package ua.translate.logging;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class SystemArchitecture {
	
	@Pointcut("within(ua.translate.service..*)")
	public void inServiceLayer(){}
	
	@Pointcut("within(ua.translate.dao..*)")
	public void inDaoLayer(){}
	
	@Pointcut("within(ua.translate.controller..*)")
	public void inWebLayer(){}
}

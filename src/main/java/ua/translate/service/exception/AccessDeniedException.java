package ua.translate.service.exception;

/**
 * Thrown when user want to execute some action, but he haven't rights for that action
 * @author Yuriy Phediv
 *
 */
public class AccessDeniedException extends Exception {
	
	public AccessDeniedException(){
		super();
	}
	
	public AccessDeniedException(String msg){
		super(msg);
	}
}

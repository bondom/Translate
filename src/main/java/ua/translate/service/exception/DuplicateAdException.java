package ua.translate.service.exception;

/**
 * Thrown when {@link Client} attempts to create {@link Ad}, which is similar
 * to other {@link Ad} of that client
 * @author Yuriy Phediv
 *
 */
public class DuplicateAdException extends Exception {
	
	
	public DuplicateAdException (){
		super();
	}
	
	public DuplicateAdException (String msg){
		super(msg);
	}
}

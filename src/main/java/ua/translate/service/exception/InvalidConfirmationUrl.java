package ua.translate.service.exception;

/**
 * Thrown when user attempts to confirm his e-mail, but
 * url for confirmation doesn't exist in data storage
 * 
 * @author Yuriy Phediv
 *
 */
public class InvalidConfirmationUrl extends Exception{

	public InvalidConfirmationUrl(String msg){
		super(msg);
	}
	
	public InvalidConfirmationUrl(){
		super();
	}
}

package ua.translate.service.exception;

import ua.translate.model.ad.RespondedAd;

/**
 * Thrown when client attempts to get, edit or delete {@link RespondedAd} 
 * with non-existent id
 * 
 * @author Yuriy Phediv
 *
 */
public class NonExistedRespondedAdException extends Exception{

	public NonExistedRespondedAdException(){
		super();
	}
	
	public NonExistedRespondedAdException(String msg){
		super(msg);
	}
}

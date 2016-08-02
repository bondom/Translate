package ua.translate.service.exception;

import ua.translate.model.ResponsedAd;

/**
 * Thrown when client attempts to get, edit or delete {@link ResponsedAd} 
 * with non-existent id
 * 
 * @author Yuriy Phediv
 *
 */
public class NonExistedResponsedAdException extends Exception{

	public NonExistedResponsedAdException(){
		super();
	}
	
	public NonExistedResponsedAdException(String msg){
		super(msg);
	}
}

package ua.translate.service.exception;

import ua.translate.model.ad.Ad;

/**
 * Thrown when client attempts to get, edit or delete {@link Ad} 
 * with non-existent id
 * 
 * @author Yuriy Phediv
 *
 */
public class NonExistedAdException extends Exception{

	public NonExistedAdException(){
		super();
	}
}

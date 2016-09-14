package ua.translate.service.exception;

import ua.translate.model.ad.Ad;

/**
 * Thrown if status of {@link Ad} {@code ad} is ACCEPTED 
 * and {@link User} want to get {@code ad} to view, or
 * to accept it or
 * {@link Client} want to delete,update this {@code Ad}
 * 
 * @author Yuriy Phediv
 *
 */
public class IllegalActionForAcceptedAd extends Exception{

	public IllegalActionForAcceptedAd(){
		super();
	}
}

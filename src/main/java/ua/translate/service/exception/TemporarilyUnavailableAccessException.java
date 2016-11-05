package ua.translate.service.exception;

import ua.translate.model.Translator;
import ua.translate.model.ad.RespondedAd;
import ua.translate.model.status.RespondedAdStatus;

/**
 * Thrown when user attempts to execute some action, but
 * in that moment he can't execute this action.
 * For example: {@link Translator} want to withdraw money,
 * but its {@link Translator#getRespondedAds() Translator.respondedAds} contains
 * {@link RespondedAd} with {@link RespondedAdStatus#PAYED PAYED} status
 * @author Yuriy Phediv
 *
 */
public class TemporarilyUnavailableAccessException extends Exception {

	public TemporarilyUnavailableAccessException(){
		super();
	}
	
	public TemporarilyUnavailableAccessException(String msg){
		super(msg);
	}
}

package ua.translate.service;

import ua.translate.model.ad.RespondedAd;
import ua.translate.model.status.RespondedAdStatus;
import ua.translate.service.exception.IllegalActionForAcceptedAd;
import ua.translate.service.exception.IllegalActionForRejectedAd;
import ua.translate.service.exception.NonExistedRespondedAdException;
import ua.translate.service.exception.TranslatorDistraction;

/**
 * This interface contains methods for interaction with {@link RespondedAd}s 
 * @author Yuriy Phediv
 *
 */
public interface RespondedAdService {
	
	/**
	 * Gets {@link RespondedAd} {@code respondedAd} from data storage by id
	 * @param id
	 * @throws NonExistedRespondedAdException if {@code ResponseAd} with such {@code id}
	 * doesn't exist
	 * @return {@code respondedAd}, never {@code null}
	 */
	public RespondedAd get(long id) throws NonExistedRespondedAdException;
	
	/**
	 * Gets {@link RespondedAd } {@code respondedAd} from data storage by id
	 * and changes status from SENDED to ACCEPTED. Changes status of other 
	 * {@code RespondedAd}s, related to the same {@code Ad} that {@code respondedAd} 
	 * to REJECTED. And changes status of {@link Ad}, related to {@code respondedAd}, to ACCEPTED
	 * <p>Checks if translator, responded to Ad, doesn't have ACCEPTED RespondedAd,
	 * if he has one, exception is thrown.
	 * <p>This method is owned by only one thread for avoiding situation,
	 * when two clients simultaneously accept RespondedAds which have common translator,
	 * because translator will have two Accepted RespondedAd - it is unacceptable
	 * <p><b>NOTE:</b>Around Logging via Spring AOP is present
	 * @param email - email of authenticated client, <b>must</b> be retrieved from Principal object
	 * @param id - id of {@code respondedAd}
	 * @throws NonExistedRespondedAdException if {@code ResponseAd} with such {@code id}
	 * doesn't exist, or it doesn't belong to client with {@code email}
	 * @throws IllegalActionForAcceptedAd if {@code ResponseAd} has ACCEPTED status
	 * @throws IllegalActionForRejectedAd if {@code ResponseAd} has REJECTED status
	 * @throws TranslatorDistraction if translator already has ACCEPTED ad
	 * @see {@link #get(long)}
	 */
	public void accept(String email,long id) throws NonExistedRespondedAdException,
									   IllegalActionForAcceptedAd,
									   IllegalActionForRejectedAd,
									   TranslatorDistraction;
	
	
	/**
	 * Gets {@link RespondedAd} {@code respondedAd} from data storage by id and
	 * changes it status to {@link RespondedAdStatus#REJECTED}
	 * <p><b>NOTE:</b>Around Logging via Spring AOP is present
	 * @param email - email of authenticated client, <b>must</b> be retrieved from Principal object
	 * @param id - id of {@code respondedAd}
	 * @throws NonExistedRespondedAdException if {@code ResponseAd} with such {@code id}
	 * doesn't exist, or it doesn't belong to client with {@code email}
	 * @throws IllegalActionForAcceptedAd if {@code ResponseAd} has ACCEPTED status
	 * @see {@link #get(long)}
	 */
	public void reject(String email,long id) throws NonExistedRespondedAdException,
									   IllegalActionForAcceptedAd;
}

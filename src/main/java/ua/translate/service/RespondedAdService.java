package ua.translate.service;

import ua.translate.model.ad.Ad;
import ua.translate.model.ad.Ad.TranslateType;
import ua.translate.model.ad.OralAd;
import ua.translate.model.ad.RespondedAd;
import ua.translate.model.status.RespondedAdStatus;
import ua.translate.service.exception.IllegalActionForAd;
import ua.translate.service.exception.IllegalActionForRespondedAd;
import ua.translate.service.exception.InsufficientFunds;
import ua.translate.service.exception.InvalidIdentifier;
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
	 * @throws InvalidIdentifier if {@code ResponseAd} with id = {@code id}
	 * doesn't exist
	 * @return {@code respondedAd}, never {@code null}
	 */
	public RespondedAd get(long id) throws InvalidIdentifier;
	
	/**
	 * Gets {@link RespondedAd } {@code respondedAd} from data storage by id
	 * and changes status from SENDED to ACCEPTED. Changes status of other 
	 * {@code RespondedAd}s, related to the same {@code Ad} that {@code respondedAd} 
	 * to REJECTED. And changes status of {@link Ad} {@code mainAd}, related to {@code respondedAd}, to ACCEPTED
	 * <p> Gets {@link Translator} {@code translator} from data storage by email,
	 * and creates relationships between {@code translator} and {{@code mainAd}.
	 * <p>Transfers {@code pledge}% of Ad cost from client's account to translator's account.
	 * If client hasn't sufficient money, exception is thrown.
	 * If pledge is less than or equal to 0 - default value is used
	 * <p>Checks if translator, responded to Ad, doesn't have ACCEPTED RespondedAd,
	 * if he has one, exception is thrown.
	 * <p>Implementation provides, that it is owned by only one thread for avoiding situation,
	 * when two clients simultaneously accept RespondedAds which have common translator,
	 * because translator will have two ACCEPTED RespondedAd and {@code mainAd} will be overriden
	 * by another one - it is unacceptable.
	 * <p><b>NOTE:</b>Around Logging via Spring AOP is present
	 * @param email - email of authenticated client, <b>must</b> be retrieved from Principal object
	 * @param id - id of {@code respondedAd}
	 * @param pledge - percentage of Ad cost, which will be transfered from client's account to translator's one
	 * @return updated {@code respondedAd}
	 * @throws InvalidIdentifier if {@code ResponseAd} with such {@code id}
	 * doesn't exist, or it doesn't belong to client with {@code email}
	 * @throws IllegalActionForRespondedAd if {@code RespondedAd} hasn't SENDED status
	 * @throws TranslatorDistraction if translator already has ACCEPTED ad
	 * @throws InsufficientFunds if client hasn't sufficient money for paying
	 * @see {@link #get(long)}
	 */
	public RespondedAd acceptRespondedAdAndTransferPledge(String email,long id, int pledge) 
									throws InvalidIdentifier,
										   IllegalActionForRespondedAd,
										   TranslatorDistraction,
										   InsufficientFunds;
	
	
	/**
	 * Gets {@link RespondedAd} {@code respondedAd} from data storage by id and
	 * changes it status to {@link RespondedAdStatus#REJECTED}
	 * <p><b>NOTE:</b>Around Logging via Spring AOP is present
	 * @param email - email of authenticated client, <b>must</b> be retrieved from Principal object
	 * @param id - id of {@code respondedAd}
	 * @throws InvalidIdentifier if {@code ResponseAd} with such {@code id}
	 * doesn't exist, or it doesn't belong to client with {@code email}
	 * @throws IllegalActionForRespondedAd if {@code ResponseAd} has ACCEPTED status
	 * @see {@link #get(long)}
	 */
	public void reject(String email,long id) throws InvalidIdentifier,
													IllegalActionForRespondedAd;
}

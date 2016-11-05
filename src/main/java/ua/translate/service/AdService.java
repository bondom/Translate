package ua.translate.service;

import java.time.LocalDateTime;

import ua.translate.model.Client;
import ua.translate.model.ad.Ad;
import ua.translate.model.ad.ArchievedAd;
import ua.translate.model.ad.RespondedAd;
import ua.translate.model.status.AdStatus;
import ua.translate.service.exception.DuplicateAdException;
import ua.translate.service.exception.IllegalActionForAd;
import ua.translate.service.exception.InvalidIdentifier;
import ua.translate.service.exception.TooManyAds;
import ua.translate.service.exception.TooManyRefreshings;

public interface AdService {
	
	/**
	 * Gets {@link Client} client from data storage by email.
	 * If {@link Ad} {@code ad} is not similar to another Ads of this client,
	 * then does associations between {@link Ad} {@code ad} and {@code client},saves in data storage.
	 * <br>Sets status of {@code ad} to SHOWED and invokes {@code ad.setPublicationDateTime(LocalDateTime.now())}
	 * <p>If {@code maxNumberOfAds} is less than 1, default value is used, this variable
	 * defines maximum number of {@code Ad}s. If number of {@code Ad}s of client equalas to
	 * {@code maxNumberOfAds}, exception is thrown
	 * <p><b>NOTE:</b>Around Logging via Spring AOP is present
	 * @param ad {@link Ad} object
	 * @param email - client's email, must be retrieved from {@code Principal} object
	 * @param maxNumberOfAds - maximum number of ads, that can belong to client
	 * @throws TooManyAds if client already has {@code maxNumberOfAds} ads
	 * @throws DuplicateAdException  if {@link Client#getAds() Client.ads} contains
	 * {@code Ad} which is similar to {@code ad}
	 * @return generated id of this advertisement
	 */
	public abstract long saveAd(Ad ad, String email,long maxNumberOfAds) 
												throws TooManyAds,DuplicateAdException;
	
	
	
	
	
	/**
	 * Attempts to update {@link Ad#getPublicationDateTime() Ad.publicationDateTime} of {@code Ad} 
	 * <p>Method gets {@code Ad} by id and if updating of 
	 * publication dateTime is allowed, sets {@link LocalDateTime#now()},
	 * otherwise appropriate exception is thrown
	 * <p>If {@code hoursBetweenRefreshing} is less than 1, default value is used
	 * <p><b>NOTE:</b>Around Logging via Spring AOP is present
	 * @param email - email of authenticated client, <b>must</b> be retrieved from Principal object
	 * @param adId -id of {@code Ad}  
	 * @param hoursBetweenRefreshing - minimum number of hours, which must pass for next
	 * refreshing
	 * @throws InvalidIdentifier if {@code Ad} with such id doesn't exist,
	 * or it doesn't belong to {@link Client} with email={@code email}
	 * @throws IllegalActionForAd if {@code Ad} with such id exists, but status is not SHOWED
	 * @throws TooManyRefreshings if required time for refreshing has not yet passed
	 */
	public abstract void refreshPubDate(String email, long adId, int hoursBetweenRefreshing) throws InvalidIdentifier, 
													   IllegalActionForAd,
													   TooManyRefreshings;
	
	/**
	 * Gets advertisement from data storage by Id
	 * @param id
	 * @return {@code Ad} object, never {@code null}
	 * @throws InvalidIdentifier if {@code Ad} with such id doesn't exist
	 * @see AdService#getForShowing(long)
	 * @see AdService#getForUpdating(long)
	 */
	public abstract Ad get(long id) throws InvalidIdentifier ;
	
	/**
	 * Gets advertisement from data storage by id with SHOWED status
	 * @param id
	 * @return {@code Ad} object, never {@code null}  
	 * @throws InvalidIdentifier if {@code Ad} with such id doesn't exist
	 * @throws IllegalActionForAd if {@code Ad} with such id exists, but
	 * status is not SHOWED
	 * @see #getForUpdating(long)
	 */
	public abstract Ad getForShowing(long id) throws InvalidIdentifier, IllegalActionForAd;
	
	/**
	 * Gets {@link Ad } {@code ad} from data storage by id with 
	 * {@link AdStatus#SHOWED SHOWED} status,
	 * checks if {@code ad} belongs to {@link Client} {@code client}, who has {@code email}
	 * <p>Invoke this method if you want to return page for editing advertisiment
	 * @param id - id of {@code Ad}, which should be retrieved for updating
	 * @param email - email of authenticated client, <b>must</b> be retrieved from Principal object
	 * @return {@code Ad} object, never {@code null}  
	 * @throws InvalidIdentifier if {@code ad} with such id doesn't exist, 
	 * or it doesn't belong to {@code client}
	 * @throws IllegalActionForAd if {@code ad} with such id exists, but its status 
	 * isn't {@code SHOWED}
	 *  or it  has responses with {@code ResponsedAdStatus#SENDED} status
	 */
	public abstract Ad getForUpdating(String email,long id) throws InvalidIdentifier, 
														  IllegalActionForAd;
	
	/**
	 * Gets {@link Ad} {@code ad} by id and delete it, if status is SHOWED or PAYED
	 * <p><b>NOTE:</b>Around Logging via Spring AOP is present
	 * @throws InvalidIdentifier if {@code Ad} with such id doesn't exist
	 * @throws IllegalActionForAd if {@code Ad} with such id exists,
	 * but its status isn't SHOWED or PAYED
	 */
	public abstract void deleteById(long id) throws InvalidIdentifier, IllegalActionForAd;
	
	/**
	 * Gets {@link Ad} {@code ad} by id and {@link Client} {@code client}
	 * by email from data storage.
	 * If {@code ad} belongs to {@code client} and ad has {@link AdStatus#PAYED PAYED} status,
	 * deletes relationships between {@code ad} and {@code client}. Deletes relationships
	 * beetween all {@link RespondedAd}s, related to {@code ad} and {@code client} as well.
	 * <p>If {@link ArchievedAd} {@code archievedAd}, related to ad, exists in data storage,
	 * retieves it and sets {@code client} to it, if no one {@code ArchievedAd} object, related
	 * to that {@code ad}, exists, creates new one ,sets {@code client} and
	 * {@code ad} to it. Saves it in data storage. 
	 *	<p>Implementation prodives, that all body of method is locked by {@code ReentrantLock} object,
	 * the same lock is used in {@link #deleteFromAssociationsWithTranslatorAndArchieveAd(long, String)} for avoiding situation,
	 * when client and translator simultaneously get {@code Ad} and update it, and check on existing {@code ArchievedAd}.
	 * @param id - id of {@code Ad}
	 * @param email - email of authenticated {@code Client}
	 * @throws InvalidIdentifier if {@code ad} or {@code client} is {@code null}, 
	 * or {@code ad} doesn't belong to {@code client}
	 */
	public abstract void deleteFromAssociationsWithClientAndArchieveAd(long id,String email) 
					throws InvalidIdentifier;
	
	/**
	 * Gets {@link Ad} {@code ad} by id and {@link Translator} {@code translator}
	 * by email from data storage.
	 * If {@code ad} and {@code translator} have relationships and {@code ad} has 
	 * {@link AdStatus#PAYED PAYED} status,
	 * deletes relationships between {@code ad} and {@code translator}. Deletes all
	 * {@code RespondedAd}s, which belongs to {@code ad} and {@code translator}, as well.
	 * <p>If {@link ArchievedAd} {@code archievedAd}, related to ad, exists in data storage,
	 * retrieves it and sets {@code translator} to it, if no one {@code ArchievedAd} object, related
	 * to that {@code ad}, exists, creates new one ,sets {@code translator} and
	 * {@code ad} to it. Saves it in data storage. 
	 * <p>Implementation prodives, that all body of method is locked by {@code ReentrantLock} object,
	 * the same lock is used in {@link #deleteFromAssociationsWithClientAndArchieveAd(long, String)} for avoiding situation,
	 * when client and translator simultaneously get {@code Ad} and update it, and check on existing {@code ArchievedAd}.
	 * 
	 * @param id - id of {@code Ad}
	 * @param email - email of authenticated {@code Translator}
	 * @throws InvalidIdentifier if {@code ad} or {@code translator} is {@code null}, 
	 * or {@code ad} isn't linked to {@code translator}
	 */
	public abstract void deleteFromAssociationsWithTranslatorAndArchieveAd(long id,String email) 
					throws InvalidIdentifier;
	
}

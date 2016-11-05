package ua.translate.service;

import java.util.Set;

import ua.translate.model.Admin;
import ua.translate.model.Order;
import ua.translate.model.Translator;
import ua.translate.model.UserEntity.UserRole;
import ua.translate.model.ad.Ad;
import ua.translate.model.ad.Document;
import ua.translate.model.ad.RespondedAd;
import ua.translate.model.ad.ResultDocument;
import ua.translate.model.ad.WrittenAd;
import ua.translate.model.searchbean.SearchWrittenAdBean;
import ua.translate.model.status.AdStatus;
import ua.translate.model.viewbean.SearchFilterForWrittenAds;
import ua.translate.service.exception.AccessDeniedException;
import ua.translate.service.exception.DownloadFileAccessDenied;
import ua.translate.service.exception.DuplicateAdException;
import ua.translate.service.exception.IllegalActionForAd;
import ua.translate.service.exception.InsufficientFunds;
import ua.translate.service.exception.InvalidIdentifier;
import ua.translate.service.exception.WrongPageNumber;

public abstract class WrittenAdService extends AbstractAdService{
	
	/**
	 * Attempts to update {@link WrittenAd} 
	 * <p>First method gets {@code WrittenAd} {@code adFromDb} by id(is retrieved from {@code updatedAd}) 
	 * from data storage, if updating is allowed(see below in exceptions),
	 * he copies all properties, that can't be setted by client while 
	 * updating WrittenAd(see below), from {@code adFromDb} to {@code updatedAd}. 
	 * 
	 * <p>If {@link WrittenAd#getDocument() WrittenAd.document} of {@code updatedAd} equals to 
	 * {@code null}, copies document from {@code adFromDb} to {@code updatedAd} too.
	 * Then method saves {@code updatedAd} in data storage, to be precise replaces 
	 * {@code adFromDb} with {@code updatedAd}(merging).
	 * 
	 * <p> Properties, that can't be setted by client while updating {@code Ad} are:
     * 		<ul>
     * 			<li>{@link Ad#getClient() Ad.client}</li>
     * 			<li>{@link Ad#getPublicationDateTime() Ad.publicationDateTime}</li>
     * 			<li>{@link Ad#getStatus() Ad.status}</li>
     * 			<li>{@link Ad#getRespondedAds() Ad.respondedAds}</li>
     * 		</ul>
	 * 
	 * <p><b>NOTE:</b>Around Logging via Spring AOP is present
	 * 
	 * @param email - email of authenticated client, <b>must</b> be retrieved from Principal object
	 * @param updatedAd - {@code WrittenAd} with setted all properties except properties, 
	 * that can't be setted by client while updating {@code Ad}(see above) and setted id
	 * 
	 * @return  {@code WrittenAd} ad object, never {@code null}
	 * 
	 * @throws InvalidIdentifier if {@code WrittenAd} with such id doesn't exist,
	 * or it doesn't belong to {@code client}
	 * @throws IllegalActionForAd if {@code WrittenAd} with such id exists, 
	 * but its status is not SHOWED or it has {@link RespondedAd} with SENDED status
	 * @throws DuplicateAdException if {@code WrittenAd} is very similar to another
	 * {@code WrittenAd}s of that client
	 */
	public abstract WrittenAd updateWrittenAdByClient(String email, WrittenAd updatedAd) throws InvalidIdentifier, 
													   IllegalActionForAd,DuplicateAdException;
	
	
	/**
	 * 
	 * @param adminEmail
	 * @param writtenAd
	 * @return
	 */
	public abstract void updateWrittenAd(WrittenAd writtenAd);
	
	/**
	 * Gets {@code Set} of {@link WrittenAd}s from data storage, for every {@code ad}
	 * from this set: ad.status={@code adStatus} and ordered by 
	 * {@link Ad#getPublicationDateTime() Ad.publicationDateTime}, order is assigned by {@link Order} 
	 * {@code order}
	 * <p>If {@code numberAdsOnPage} is less then 1, default
	 * number is used. If {@code page} is less then 1, exception is thrown
	 * <p>Size of result {@code Set} is not more than {@code numberAdsOnPage}
	 * <p><b>NOTE:</b>AfterReturning Logging via Spring AOP is present
	 * @param page -  page number, can't be less than 1
	 * @param numberAdsOnPage - number {@link WrittenAd}s, which can be displayed on 1 page
	 * @return set of {@code WrittenAd}s, never {@code null}
	 * @throws WrongPageNumber if {@code page} is less than 1
	 */
	public abstract Set<WrittenAd> getWrittenAdsByStatusAndOrder(int page,int numberAdsOnPage,
							AdStatus adStatus, Order order) throws WrongPageNumber;
	
	
	/**
	 * Gets {@code Set} of {@link WrittenAd}s from data storage,  
	 * which have SHOWED status, ordered by {@link WrittenAd#getPublicationDateTime() WrittenAd.publicationDateTime} 
	 * from latest to earliest and filtered by user
	 * <br><p>{@link SearchFilterForWrittenAds} {@code searchFilter} contains String 
	 * representation of all properties, available to user for choosing 
	 * <br><p>Method  creates {@link SearchWrittenAdBean} {@code searchAdBean}, and sets
	 * its properties based on {@code searchFilter}. If some properties 
	 * of {@code searchFilter} equal to {@code valueWithoutFilter}, appropriate 
	 * properties of {@code searchAdBean} are assigned to {@code null}
	 * (that is no filter for this properties won't be applied) 
	 * <p>If {@code numberAdsOnPage} is less then 1, default
	 * number is used. If {@code page} is less then 1, exception is thrown
	 * <br><p>Size of result {@code Set} is not more than {@code numberAdsOnPage}
	 * <p><b>NOTE:</b>AfterReturning and Before Logging via Spring AOP is present
	 * @param page -  page number, can't be less than 1
	 * @param numberAdsOnPage - number {@code WrittenAd}s, which can be displayed on 1 page
	 * @param searchFilter - object, which represents filters, chosen by user
	 * @param valueWithoutFilter - value, which is setted for user's 
	 * {@link SearchFilterForWrittenAds} String properties by default(without choosing),
	 * and imposes no restrictions for appropriate String properties
	 * @return set of {@code WrittenAd}s, never {@code null}
	 * @throws WrongPageNumber if {@code page} is less than 1
	 */
	public abstract Set<WrittenAd> getWrittenAdsForShowingByFilter(int page,int numberAdsOnPage,
							SearchFilterForWrittenAds searchFilter,String valueWithoutFilter) throws WrongPageNumber;
	
	
	/**
	 * Returns number of pages for all existed {@link WrittenAd}s with status={@code adStatus},
	 * if on one page can be displayed only {@code numberOfAds} {@code WrittenAd}s
	 * <p>If {@code numberOfAds} is less then 1, default
	 * number is used.
	 * <p><b>NOTE:</b>Around Logging via Spring AOP is present
	 * @param numberOfAds - number of {@code Ad}s, which can be displayed on one page
	 */
	public abstract long getNumberOfPagesForWrittenAdsByStatus(AdStatus adStatus,int numberOfAds);
	

	
	
	
	/**
	 * Returns number of pages for all existed {@link WrittenAd}s with status={@code adStatus}
	 * and filtered by user.
	 * 
	 * <p>{@link SearchFilterForWrittenAds} {@code searchFilter} contains String 
	 * representation of all properties, available to user for choosing 
	 * 
	 * <p>Method  creates {@link SearchWrittenAdBean} {@code searchAdBean}, and sets
	 * its properties based on {@code searchFilter}. If some properties 
	 * of {@code searchFilter} equal to {@code valueWithoutFilter}, appropriate 
	 * properties of {@code searchAdBean} are assigned to {@code null}
	 * (that is no filter for this properties won't be applied) 
	 * 
	 * <p>If {@code numberOfAds} is less then 1, default
	 * number is used.
	 * 
	 * <p><b>NOTE:</b>Around Logging via Spring AOP is present
	 * 
	 * @param numberOfAds - number of {@code Ad}s, which can be displayed on one page
	 * @param searchFilter - {@code SearchFilterForWrittenAds} object, which represents filters, chosen by user
	 * @param valueWithoutFilter - value, which is setted for user's 
	 * {@link SearchFilterForWrittenAds} String properties by default(without choosing),
	 * and imposes no restrictions for appropriate String properties
	 */
	public abstract long getNumberOfPagesForWrittenAdsByStatusAndFilter(AdStatus adStatus,int numberOfAds,
												SearchFilterForWrittenAds searchFilter,
												String valueWithoutFilter);
	
	/**
	 * Gets Set of {@link WrittenAd}s from data storage, every element 
	 * of which have status, which is equal to one of {@code statuses}.
	 * @param statuses
	 */
	public abstract Set<WrittenAd> getAllWrittenAdsByStatuses(Set<AdStatus> statuses);
	/**
	 * Gets {@link WrittenAd} {@code ad} by id and
	 * {@link Translator} {@code translator} by email from data storage. 
	 *
	 * <br>If {@code ad} have {@link AdStatus#ACCEPTED ACCEPTED} or {@link AdStatus#REWORKING REWORKING} 
	 * status and {@code translator} have relationships with it,
	 * does associations between {@link ResultDocument} {@code resultDocument} and {@code ad}.
	 * <br>Changes status of {@code ad} to {@link AdStatus#NOTCHECKED NOTCHECKED}.
	 * 
	 * <p>If {@code resultDocument} is {@code null}, method returns false with no actions.
	 * 
	 * <p>RespondedAd which is related to these Ad and Translator, still has ACCEPTED status, for
	 * prohibiting to take by translator more than 1 {@code Ad}.
	 * 
	 * <p><b>NOTE:</b>Around Logging via Spring AOP is present
	 * 
	 * @param adId - id of ACCEPTED {@code Ad}
	 * @param email - email of authenticated {@link Translator}, 
	 * <b>must</b> be retrieved from {@code Principal} object
	 * @param resultDocument - {@link ResultDocument} object, which contains translated text, can't be {@code null}
	 * 
	 * @return true, if status is changed to NOTCHECKED, else false
	 * 
	 * @throws InvalidIdentifier if {@code WrittenAd} with id={@code adId} doesn't exist in data storage,
	 * or {@code translator} hasn't relationships with {@code  ad}
	 * @throws IllegalActionForAd if {@code ad} exists, but its status is not 
	 * {@link AdStatus#ACCEPTED ACCEPTED} and not {@link AdStatus#REWORKING REWORKING}
	 */
	public abstract boolean saveResultDocAndMarkAsNotChecked(String email,long adId,ResultDocument resultDocument)
					throws InvalidIdentifier, IllegalActionForAd;
	/**
	 * Gets {@link WrittenAd} {@code writtenAd} from data storage by id,
	 * if {@code writtenAd} has {@link AdStatus#NOTCHECKED NOTCHECKED} status,
	 * changes status to {@link AdStatus#CHECKED CHECKED}.
	 * @param adminEmail - email of authenticated {@link Admin} 
	 * @param adId - id of {@code WrittenAd}
	 * @throws InvalidIdentifier if {@code WrittenAd} with such id doesn't exist in
	 * data storage
	 * @throws IllegalActionForAd if {@code WrittenAd} has status different from 
	 * {@code NOTCHECKED}
	 * @throws AccessDeniedException if {@code Admin} with {@code adminEmail} email
	 * doesn't exist in data storage
	 */
	public abstract void markAsChecked(String adminEmail, long adId) 
			throws InvalidIdentifier,IllegalActionForAd,AccessDeniedException;
	
	/**
	 * Gets {@link WrittenAd} {@code writtenAd} from data storage by id,
	 * if {@code writtenAd} has {@link AdStatus#NOTCHECKED NOTCHECKED} status,
	 * changes status to {@link AdStatus#REWORKING REWORKING} and
	 * sets {@code messageForDownloader} to 
	 * {@link ResultDocument#getMessageForDownloader() ResultDocument.messageForDownloader}.
	 * <br>If {@code messageForDownloader} is {@code null} or is empty, sets default message, retrieved from 
	 * data storage.
	 * @param adminEmail - email of authenticated {@link Admin} 
	 * @param adId - id of {@code WrittenAd}
	 * @throws InvalidIdentifier if {@code WrittenAd} with such id doesn't exist in
	 * data storage
	 * @throws IllegalActionForAd if {@code WrittenAd} has status different from 
	 * {@code NOTCHECKED}
	 * @throws AccessDeniedException if {@code Admin} with {@code adminEmail} email
	 * doesn't exist in data storage
	 */
	public abstract void markForRework(String adminEmail, long adId, String messageForDownloader)
			throws InvalidIdentifier,IllegalActionForAd,AccessDeniedException;
	
	/**
	 * Gets {@link Client} {@code client} from data storage by email,
	 * and gets {@link WrittenAd} {@code writtenAd} from data storage by id={@code adId}.
	 * If {@code writtenAd} belongs to {@code client} and {@code writtenAd} has 
	 * {@link AdStatus#CHECKED CHECKED} status, changes status to {@link AdStatus#PAYED PAYED}.
	 * <br>Transfers restPersent% of {@link Ad#getCost() Ad.cost} from client's account to translator's one, who
	 * downloaded {@link WrittenAd#getResultDocument() resultDocument} for that {@code writtenAd}.
	 * <p>Too increments {@link Translator#getNumberOfExecutedAds() numberOfExecutedAds} for {@code translator} by 1.
	 * @param email - email of authenticated {@link Client}
	 * @param adId - id of {@code WrittenAd} with {@code CHECKED} status
	 * @param restPercent - percent of {@link Ad#getCost() Ad.cost}, which must be transfered,
	 * if it is less than 0, default value is used
	 * @throws InvalidIdentifier if {@code WrittenAd} with id={@code adId} doesn't exist
	 * in data storage or it exists, but doesn't belong to {@code Client} with email={@code email}
	 * @throws IllegalActionForAd if {@code WrittenAd} has status different from {@code CHECKED}
	 * @throws InsufficientFunds if {@code Client} hasn't sufficient money for transfering
	 */
	public abstract void transferRestPriceAndChangeStatusAndIncrementExecutedAds(String email,long adId,int restPercent)
			throws InvalidIdentifier,IllegalActionForAd,InsufficientFunds;
	
}

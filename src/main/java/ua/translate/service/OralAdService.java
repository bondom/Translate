package ua.translate.service;

import java.util.Set;

import ua.translate.model.Order;
import ua.translate.model.Translator;
import ua.translate.model.ad.Ad;
import ua.translate.model.ad.OralAd;
import ua.translate.model.ad.RespondedAd;
import ua.translate.model.ad.WrittenAd;
import ua.translate.model.searchbean.SearchOralAdBean;
import ua.translate.model.status.AdStatus;
import ua.translate.model.viewbean.SearchFilterForOralAds;
import ua.translate.service.exception.DuplicateAdException;
import ua.translate.service.exception.IllegalActionForAd;
import ua.translate.service.exception.InsufficientFunds;
import ua.translate.service.exception.InvalidIdentifier;
import ua.translate.service.exception.TooEarlyPaying;
import ua.translate.service.exception.WrongPageNumber;

public abstract class OralAdService extends AbstractAdService{
	
	/**
	 * Attempts to update {@link OralAd} 
	 * <p>First method gets {@code OralAd} {@code adFromDb} by id(is retrieved from {@code updatedAd}) 
	 * from data storage, if updating is allowed(see below in throwed exceptions),
	 * he copies all properties, that can't be setted by client while 
	 * updating OralAd(see below), from {@code adFromDb} to {@code updatedAd}. 
	 * 
	 * Then method saves {@code updatedAd} in data storage, to be precise replaces 
	 * {@code adFromDb} with {@code updatedAd}(merging).
	 * <p> Properties, that can't be setted by client while updating {@code Ad} are:
     * 		<ul>
     * 			<li>{@link Ad#getClient() Ad.client}</li>
     * 			<li>{@link Ad#getPublicationDateTime() Ad.publicationDateTime}</li>
     * 			<li>{@link Ad#getStatus() Ad.status}</li>
     * 			<li>{@link Ad#getRespondedAds() Ad.respondedAds}</li>
     * 		</ul>
	 * <p><b>NOTE:</b>Around Logging via Spring AOP is present
	 * @param email - email of authenticated client, <b>must</b> be retrieved from Principal object
	 * @param updatedAd - {@code OralAd} with setted all properties except properties, 
	 * that can't be setted by client while updating {@code Ad}(see above) and setted id
	 * @return  {@code OralAd} ad object, never {@code null}
	 * @throws InvalidIdentifier if {@code WrittenAd} with such id doesn't exist,
	 * or it doesn't belong to {@code client}
	 * @throws IllegalActionForAd if {@code WrittenAd} with such id exists, 
	 * but its status is not SHOWED or it has {@link RespondedAd} with SENDED status
	 * @throws DuplicateAdException if {@code OralAd} is very similar to another
	 * {@code OralAd}s of that client
	 */
	public abstract OralAd updateOralAd(String email, OralAd updatedAd) 
												throws InvalidIdentifier, 
													   IllegalActionForAd,
													   DuplicateAdException;
	
	/**
	 * Gets {@code Set} of {@link OralAd}s from data storage, for every {@code ad}
	 * from this set: ad.status={@code adStatus} and ordered by 
	 * {@link Ad#getPublicationDateTime() Ad.publicationDateTime}, order is assigned by {@link Order} 
	 * {@code order}
	 * <p>If {@code numberAdsOnPage} is less then 1, default
	 * number is used. If {@code page} is less then 1, exception is thrown
	 * <p>Size of result {@code Set} is not more than {@code numberAdsOnPage}
	 * <p><b>NOTE:</b>AfterReturning Logging via Spring AOP is present
	 * @param page -  page number, can't be less than 1
	 * @param numberAdsOnPage - number {@link OralAd}s, which can be displayed on 1 page
	 * @return set of {@code OralAd}s, never {@code null}
	 * @throws WrongPageNumber if {@code page} is less than 1
	 */
	public abstract Set<OralAd> getOralAdsByStatusAndOrder(int page,int numberAdsOnPage,
							AdStatus adStatus, Order order) throws WrongPageNumber;
	
	/**
	 * Gets {@code Set} of {@link OralAd}s from data storage,  
	 * which have SHOWED status, ordered by {@link OralAd#getPublicationDateTime()
	 * OralAd.publicationDateTime} 
	 * from latest to earliest and filtered by user
	 * <br><p>{@link SearchFilterForOralAds} {@code searchFilter} contains String 
	 * representation of all properties, available to user for choosing 
	 * <br><p>Method  creates {@link SearchOralAdBean} {@code searchAdBean}, and sets
	 * its properties based on {@code searchFilter}. If some properties 
	 * of {@code searchFilter} equal to {@code valueWithoutFilter}, appropriate 
	 * properties of {@code searchAdBean} are assigned to {@code null}
	 * (that is no filter for this properties won't be applied) 
	 * <p>If {@code numberAdsOnPage} is less then 1, default
	 * number is used. If {@code page} is less then 1, exception is thrown
	 * <br><p>Size of result {@code Set} is not more than {@code numberAdsOnPage}
	 * <p><b>NOTE:</b>AfterReturning and Before Logging via Spring AOP is present
	 * @param page -  page number, can't be less than 1
	 * @param numberAdsOnPage - number {@link OralAd}s, which can be displayed on 1 page
	 * @param searchFilter - object, which represents filters, chosen by user
	 * @param valueWithoutFilter - value, which is setted for user's 
	 * {@link SearchFilterForOralAds} String properties by default(without choosing),
	 * and imposes no restrictions for appropriate String properties
	 * @return set of {@code OralAd}s, never {@code null}
	 * @throws WrongPageNumber if {@code page} is less than 1
	 */
	public abstract Set<OralAd> getOralAdsForShowingByFilter(int page,int numberAdsOnPage,
			SearchFilterForOralAds searchFilter,String valueWithoutFilter) throws WrongPageNumber;
	
	/**
	 * Returns number of pages for all existed {@link OralAd}s with status={@code adStatus},
	 * if on one page can be displayed only {@code numberOfAds} {@code OralAd}s
	 * <p>If {@code numberOfAds} is less then 1, default
	 * number is used.
	 * <p><b>NOTE:</b>Around Logging via Spring AOP is present
	 * @param numberOfAds - number of {@code Ad}s, which can be displayed on one page
	 */
	public abstract long getNumberOfPagesForOralAdsByStatus(AdStatus adStatus,int numberOfAds);
	
	/**
	 * Returns number of pages for all existed {@link OralAd}s with status={@code adStatus}
	 * and filtered by user.
	 * <br><p>{@link SearchFilterForOralAds} {@code searchFilter} contains String 
	 * representation of all properties, available to user for choosing 
	 * <br><p>Method  creates {@link SearchOralAdBean} {@code searchAdBean}, and sets
	 * its properties based on {@code searchFilter}. If some properties 
	 * of {@code searchFilter} equal to {@code valueWithoutFilter}, appropriate 
	 * properties of {@code searchAdBean} are assigned to {@code null}
	 * (that is no filter for this properties won't be applied) 
	 * <p>If {@code numberOfAds} is less then 1, default
	 * number is used.
	 * <p><b>NOTE:</b>Around Logging via Spring AOP is present
	 * @param adStatus - {@link AdStatus}
	 * @param numberOfAds - number of {@code Ad}s, which can be displayed on one page
	 * @param searchFilter - {@code SearchFilterForOralAds} object, which represents filters, chosen by user
	 * @param valueWithoutFilter - value, which is setted for user's 
	 * {@link SearchFilterForOralAds} String properties by default(without choosing),
	 * and imposes no restrictions for appropriate String properties
	 */
	public abstract long getNumberOfPagesForOralAdsByStatusAndFilter(AdStatus adStatus,int numberOfAds,
			SearchFilterForOralAds searchFilter,String valueWithoutFilter);
	
	/**
	 * Gets {@link Client} {@code client} from data storage by email,
	 * and gets {@link OralAd} {@code oralAd} from data storage by id={@code adId}.
	 * If {@code oralAd} meets following demands:
	 * <ul>
	 * 		<li>it belongs to {@code client};</li>
	 * 		<li>it has {@link AdStatus#ACCEPTED ACCEPTED} status</li>
	 * 		<li>{@code LocalDateTime.now()} is more than {@link OralAd#getFinishDateTime() oralAd.FinishDateTime()}</li>
	 * </ul>
	 * changes status to {@link AdStatus#PAYED PAYED}.
	 * <br>Transfers restPersent% of {@link Ad#getCost() Ad.cost} from client's account to translator's one,who
	 * is executing {@code oralAd}.
	 * <p>Too increments {@link Translator#getNumberOfExecutedAds() numberOfExecutedAds} for {@code translator} by 1.
	 * @param email - email of authenticated {@link Client}
	 * @param adId - id of {@code OralAd}
	 * @param restPercent - percent of {@link Ad#getCost() Ad.cost}, which must be transfered,
	 * if it is less than 0, default value is used
	 * @throws InvalidIdentifier if {@code OralAd} with id={@code adId} doesn't exist
	 * in data storage or it exists, but doesn't belong to {@code Client} with email={@code email}
	 * @throws IllegalActionForAd if {@code OralAd} has status different from {@code ACCEPTED}
	 * @throws InsufficientFunds if {@code Client} hasn't sufficient money for transfering
	 * @throws TooEarlyPaying if If {@code LocalDateTime.now()} is less than {@link OralAd#getFinishDateTime() oralAd.FinishDateTime()}
	 */
	public abstract void transferRestPriceAndChangeStatusAndIncrementExecutedAds(String email,long adId,int restPercent)
			throws InvalidIdentifier,IllegalActionForAd,InsufficientFunds,TooEarlyPaying;
}

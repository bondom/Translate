package ua.translate.service;

import java.security.Principal;
import java.util.List;
import java.util.Set;

import javax.persistence.ManyToOne;

import ua.translate.model.Client;
import ua.translate.model.Translator;
import ua.translate.model.User;
import ua.translate.model.ad.Ad;
import ua.translate.model.ad.RespondedAd;
import ua.translate.model.status.AdStatus;
import ua.translate.model.viewbean.AdView;
import ua.translate.service.exception.IllegalActionForAd;
import ua.translate.service.exception.NonExistedAdException;
import ua.translate.service.exception.IllegalActionForAcceptedAd;
import ua.translate.service.exception.WrongPageNumber;

/**
 * This interface contains methods for interaction with {@link Ad}s 
 * @author Yuriy Phediv
 *
 */
public interface AdService {
	
	/**
	 * Gets {@code Client} client from data storage by email,
	 * then does associations between {@code ad} and {@code client} and saves in data storage
	 * <p><b>NOTE:</b>Around Logging via Spring AOP is present
	 * @param ad {@link Ad} object
	 * @param email - client's email, must be retrieved from {@code Principal} object
	 * @return generated id of this advertisement
	 */
	public long saveAd(Ad ad, String email);
	
	/**
	 * Attempts to retrieve advertisement by id from data storage, and to update it
	 * <p><b>NOTE:</b>Around Logging via Spring AOP is present
	 * @param email - email of authenticated client, <b>must</b> be retrieved from Principal object
	 * @return  {@code Ad} ad object, never {@code null}
	 * @throws NonExistedAdException if {@code Ad} with such id doesn't exist,
	 * or it doesn't belong to {@code client}
	 * @throws IllegalActionForAcceptedAd if {@code Ad} with such id exists, but status is ACCEPTED
	 * @throws IllegalActionForAd if {@code Ad} has responses with {@code ResponsedAdStatus#SENDED} status
	 */
	public Ad updateAd(String email,long adId, Ad updatedAd) throws NonExistedAdException, 
													   IllegalActionForAcceptedAd,
													   IllegalActionForAd;
	
	/**
	 * Gets advertisement from data storage by Id
	 * @param id
	 * @return {@code Ad} object, never {@code null}
	 * @throws NonExistedAdException if {@code Ad} with such id doesn't exist
	 * @see AdService#getForShowing(long)
	 * @see AdService#getForUpdating(long)
	 */
	public Ad get(long id) throws NonExistedAdException ;
	
	/**
	 * Gets advertisement from data storage by Id with {@code status==SHOWED}
	 * @param id
	 * @return {@code Ad} object, never {@code null}  
	 * @throws NonExistedAdException if {@code Ad} with such id doesn't exist
	 * @throws IllegalActionForAcceptedAd if {@code Ad} with such id exists, but
	 * {@code status!=SHOWED}
	 * @see #getForShowing(long)
	 * @see #getForUpdating(long)
	 */
	public Ad getForShowing(long id) throws NonExistedAdException, IllegalActionForAcceptedAd;
	
	/**
	 * Gets {@link Ad } {@code ad} from data storage by id with SHOWED status,
	 * checks if {@code ad} belongs to {@link Client} {@code client}, who has {@code email}
	 * <p>Invoke this method if you want to return page for editing advertisiment
	 * @param id - id of {@code Ad}, which should be retrieved for updating
	 * @param email - email of authenticated client, <b>must</b> be retrieved from Principal object
	 * @return {@code Ad} object, never {@code null}  
	 * @throws NonExistedAdException if {@code ad} with such id doesn't exist, 
	 * or it doesn't belong to {@code client}
	 * @throws IllegalActionForAcceptedAd if {@code ad} with such id exists, 
	 * but status is ACCEPTED
	 * @throws IllegalActionForAd if {@code ad} has responses with {@code ResponsedAdStatus#SENDED} status
	 */
	public Ad getForUpdating(String email,long id) throws NonExistedAdException, 
														  IllegalActionForAcceptedAd,
														  IllegalActionForAd;
	
	
	/**
	 * Gets {@code Set} of advertisements from data storage, 
	 * which have {@code status==SHOWED}, ordered by {@link Ad#getPublicationDateTime()} 
	 * from latest to earliest.
	 * size of result {@code Set} is not more than {@code numberAdsOnPage}
	 * <p><b>NOTE:</b>AfterReturning Logging via Spring AOP is present
	 * @param page -  page number, can't be less than 1
	 * @param numberAdsOnPage - number {@link Ad}s, which can be displayed on 1 page
	 * @return set of advertisements, never {@code null}
	 * @throws WrongPageNumber if {@code page} is less than 1
	 */
	public Set<Ad> getAdsForShowing(int page,int numberAdsOnPage) throws WrongPageNumber;
	
	/**
	 * <b>For ADMIN</b>
	 * <p>Gets {@code Set} of advertisements from data storage, 
	 * which have {@code status==NOTCHECKED}, ordered by {@link Ad#getPublicationDateTime()} 
	 * from earliest to latest.
	 * size of result {@code Set} is not more than {@code numberAdsOnPage}
	 * <p><b>NOTE:</b>AfterReturning Logging via Spring AOP is present
	 * @param page -  page number, can't be less than 1
	 * @param numberAdsOnPage - number {@link Ad}s, which can be displayed on 1 page
	 * @return set of advertisements, never {@code null}
	 * @throws WrongPageNumber if {@code page} is less than 1
	 */
	public Set<Ad> getAdsForChecking(int page,int numberAdsOnPage) throws WrongPageNumber;
	
	/**
	 * Returns number of pages for all existed {@link Ad}s with status={@code adStatus},
	 * if on one page can be displayed only {@code numberOfAds} {@code Ad}s
	 * <p><b>NOTE:</b>Around Logging via Spring AOP is present
	 * @param numberOfAds - number of {@code Ad}s, which can be displayed on one page
	 */
	public long getNumberOfPagesForAdsByStatus(AdStatus adStatus,int numberOfAds);
	
	
	/**
	 * Gets {@link Ad} {@code ad} by id and delete it, if status is not ACCEPTED
	 * <p><b>NOTE:</b>Around Logging via Spring AOP is present
	 * @throws NonExistedAdException if {@code Ad} with such id doesn't exist
	 * @throws IllegalActionForAcceptedAd if {@code Ad} with such id exists,
	 * but {@code ad.status==ACCEPTED}
	 */
	public void deleteById(long id) throws NonExistedAdException, IllegalActionForAcceptedAd;

	
	
}

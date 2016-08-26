package ua.translate.service;

import java.security.Principal;
import java.util.List;
import java.util.Set;

import javax.persistence.ManyToOne;

import ua.translate.model.Client;
import ua.translate.model.Translator;
import ua.translate.model.User;
import ua.translate.model.ad.Ad;
import ua.translate.model.ad.Document;
import ua.translate.model.ad.RespondedAd;
import ua.translate.model.searchbean.SearchAdBean;
import ua.translate.model.status.AdStatus;
import ua.translate.model.viewbean.AdView;
import ua.translate.model.viewbean.SearchFilterForAds;
import ua.translate.service.exception.IllegalActionForAd;
import ua.translate.service.exception.NonExistedAdException;
import ua.translate.service.exception.DownloadFileAccessDenied;
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
	 * Gets advertisement from data storage by Id with SHOWED status
	 * @param id
	 * @return {@code Ad} object, never {@code null}  
	 * @throws NonExistedAdException if {@code Ad} with such id doesn't exist
	 * @throws IllegalActionForAcceptedAd if {@code Ad} with such id exists, but
	 * status is not SHOWED
	 * @see #getForUpdating(long)
	 */
	public Ad getForShowing(long id) throws NonExistedAdException, IllegalActionForAcceptedAd;
	
	/**
	 * Gets {@link Document} from data storage by its ad id
	 * <p>Invoke this method if user attempts to download file, 
	 * related to ad with id={@code adId}<br>
	 * <p>File can be downloaded only by translators or owner of advertisement, 
	 * to which the file is related. If another user attempts to get file, exception is thrown
	 * @param adId - ad id
	 * @param userEmail - email of user, who attempts to download file, related to ad
	 * @return {@code Document} object, never {@code null}  
	 * @throws NonExistedAdException if {@code Ad} with such id doesn't exist
	 * @throws DownloadFileAccessDenied if user with email={@code userEmail} is not translator and
	 * is not owner of this ad
	 * @see #getForShowing(long)
	 * @see #getForUpdating(long)
	 */
	public Document getDocumentForDownloading(long adId,String userEmail) throws NonExistedAdException,DownloadFileAccessDenied;
	
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
	 * which have SHOWED status, ordered by {@link Ad#getPublicationDateTime()} 
	 * from latest to earliest.
	 * <p>If {@code numberAdsOnPage} is less then 1, default
	 * number is used. If {@code page} is less then 1, exception is thrown
	 * <p>Size of result {@code Set} is not more than {@code numberAdsOnPage}
	 * <p><b>NOTE:</b>AfterReturning Logging via Spring AOP is present
	 * @param page -  page number, can't be less than 1
	 * @param numberAdsOnPage - number {@link Ad}s, which can be displayed on 1 page
	 * @return set of advertisements, never {@code null}
	 * @throws WrongPageNumber if {@code page} is less than 1
	 */
	public Set<Ad> getAdsForShowing(int page,int numberAdsOnPage) throws WrongPageNumber;
	
	/**
	 * Gets {@code Set} of {@code Ad}s from data storage,  
	 * which have SHOWED status, ordered by {@link Ad#getPublicationDateTime()} 
	 * from latest to earliest and filtered by user
	 * <br><p>{@link SearchFilterForAds} {@code searchFilter} contains String 
	 * representation of all properties, available to user for choosing 
	 * <br><p>Method  creates {@link SearchAdBean} {@code searchAdBean}, and sets
	 * its properties based on {@code searchFilter}. If some properties 
	 * of {@code searchFilter} equal to {@code valueWithoutFilter}, appropriate 
	 * properties of {@code searchAdBean} are assigned to {@code null}
	 * (that is no filter for this properties won't be applied) 
	 * <p>If {@code numberAdsOnPage} is less then 1, default
	 * number is used. If {@code page} is less then 1, exception is thrown
	 * <br><p>Size of result {@code Set} is not more than {@code numberAdsOnPage}
	 * <p><b>NOTE:</b>AfterReturning and Before Logging via Spring AOP is present
	 * @param page -  page number, can't be less than 1
	 * @param numberAdsOnPage - number {@link Ad}s, which can be displayed on 1 page
	 * @param searchFilter - object, which represents filters, chosen by user
	 * @param valueWithoutFilter - value, which is setted for user's 
	 * {@link SearchFilterForAds} String properties by default(without choosing),
	 * and imposes no restrictions for appropriate String properties
	 * @return set of advertisements, never {@code null}
	 * @throws WrongPageNumber if {@code page} is less than 1
	 */
	public Set<Ad> getAdsForShowingByFilter(int page,int numberAdsOnPage,
							SearchFilterForAds searchFilter,String valueWithoutFilter) throws WrongPageNumber;
	
	/**
	 * <b>For ADMIN</b>
	 * <p>Gets {@code Set} of advertisements from data storage, 
	 * which have {@code status==NOTCHECKED}, ordered by {@link Ad#getPublicationDateTime()} 
	 * from earliest to latest.
	 * size of result {@code Set} is not more than {@code numberAdsOnPage}
	 * <p>If {@code numberAdsOnPage} is less then 1, default
	 * number is used. If {@code page} is less then 1, exception is thrown
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
	 * <p>If {@code numberOfAds} is less then 1, default
	 * number is used.
	 * <p><b>NOTE:</b>Around Logging via Spring AOP is present
	 * @param numberOfAds - number of {@code Ad}s, which can be displayed on one page
	 */
	public long getNumberOfPagesForAdsByStatus(AdStatus adStatus,int numberOfAds);
	
	/**
	 * Returns number of pages for all existed {@link Ad}s with status={@code adStatus}
	 * and filtered by user.
	 * <br><p>{@link SearchFilterForAds} {@code searchFilter} contains String 
	 * representation of all properties, available to user for choosing 
	 * <br><p>Method  creates {@link SearchAdBean} {@code searchAdBean}, and sets
	 * its properties based on {@code searchFilter}. If some properties 
	 * of {@code searchFilter} equal to {@code valueWithoutFilter}, appropriate 
	 * properties of {@code searchAdBean} are assigned to {@code null}
	 * (that is no filter for this properties won't be applied) 
	 * <p>If {@code numberOfAds} is less then 1, default
	 * number is used.
	 * <p><b>NOTE:</b>Around Logging via Spring AOP is present
	 * @param numberOfAds - number of {@code Ad}s, which can be displayed on one page
	 * @param searchFilter - object, which represents filters, chosen by user
	 * @param valueWithoutFilter - value, which is setted for user's 
	 * {@link SearchFilterForAds} String properties by default(without choosing),
	 * and imposes no restrictions for appropriate String properties
	 */
	public long getNumberOfPagesForAdsByStatusAndFilter(AdStatus adStatus,int numberOfAds,
												SearchFilterForAds searchFilter,
												String valueWithoutFilter);
	
	
	/**
	 * Gets {@link Ad} {@code ad} by id and delete it, if status is not ACCEPTED
	 * <p><b>NOTE:</b>Around Logging via Spring AOP is present
	 * @throws NonExistedAdException if {@code Ad} with such id doesn't exist
	 * @throws IllegalActionForAcceptedAd if {@code Ad} with such id exists,
	 * but {@code ad.status==ACCEPTED}
	 */
	public void deleteById(long id) throws NonExistedAdException, IllegalActionForAcceptedAd;

	
	
}

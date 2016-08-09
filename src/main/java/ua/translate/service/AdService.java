package ua.translate.service;

import java.security.Principal;
import java.util.List;
import java.util.Set;

import javax.persistence.ManyToOne;

import ua.translate.model.Client;
import ua.translate.model.Translator;
import ua.translate.model.User;
import ua.translate.model.ad.Ad;
import ua.translate.model.ad.ResponsedAd;
import ua.translate.model.viewbean.AdView;
import ua.translate.service.exception.NonExistedAdException;
import ua.translate.service.exception.UnacceptableActionForAcceptedAd;

public interface AdService {
	
	/**
	 * Gets {@code Client} client from data storage by email,
	 * then does associations between {@code ad} and {@code client} and saves in data storage
	 * @param ad {@link Ad} object
	 * @param email - client's email, must be retrieved from {@code Principal} object
	 * @return generated id of this advertisement
	 */
	public long saveAd(Ad ad, String email);
	
	/**
	 * Attempts to retrieve advertisement by id from data storage, and to update it
	 * @return  {@code Ad} ad object, never {@code null}
	 * @throws NonExistedAdException if {@code Ad} with such id doesn't exist
	 * @throws UnacceptableActionForAcceptedAd if {@code Ad} with such id exists, but {@code ad.status==ACCEPTED}
	 */
	public Ad updateAd(long adId, Ad updatedAd) throws NonExistedAdException, UnacceptableActionForAcceptedAd;
	
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
	 * @throws UnacceptableActionForAcceptedAd if {@code Ad} with such id exists, but
	 * {@code status!=SHOWED}
	 * @see #getForShowing(long)
	 * @see #getForUpdating(long)
	 */
	public Ad getForShowing(long id) throws NonExistedAdException, UnacceptableActionForAcceptedAd;
	
	/**
	 * Gets advertisement from data storage by Id with {@code status==SHOWED}
	 * <p>Invoke this method if you want to return to {@code Client} page for editing advertisiment
	 * @param id
	 * @return {@code Ad} object, never {@code null}  
	 * @throws NonExistedAdException if {@code Ad} with such id doesn't exist
	 * @throws UnacceptableActionForAcceptedAd if {@code Ad} with such id exists, but
	 * {@code status!=SHOWED}
	 */
	public Ad getForUpdating(long id) throws NonExistedAdException, UnacceptableActionForAcceptedAd;
	
	
	/**
	 * Gets advertisements from data storage, 
	 * which have {@code status==SHOWED}
	 * @return list of advertisements, never {@code null}
	 */
	public Set<Ad> getAdsForShowing();
	
	
	/**
	 * Gets {@link Ad} {@code ad} by id and delete it, if {@code ad.status!=ACCEPTED}
	 * @throws NonExistedAdException if {@code Ad} with such id doesn't exist
	 * @throws UnacceptableActionForAcceptedAd if {@code Ad} with such id exists,
	 * but {@code ad.status==ACCEPTED}
	 */
	public void deleteById(long id) throws NonExistedAdException, UnacceptableActionForAcceptedAd;

	
	
}

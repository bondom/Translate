package ua.translate.service;

import java.security.Principal;
import java.util.List;

import ua.translate.model.ad.Ad;
import ua.translate.service.exception.NonExistedAdException;

public interface AdService {
	
	/**
	 * Gets client from data storage by email,
	 * then does associations between {@code ad} client and saves in data storage
	 * @param ad {@link Ad} object
	 * @param email - client's email, must be retrieved from {@code Principal} object
	 * @return generated id of this advertisement
	 */
	public long saveAd(Ad ad, String email);
	
	/**
	 * Attempts to retrieve advertisement by id from data storage, and to update it
	 * @return  {@code Ad} object, never null
	 * @throws NonExistedAdException if {@code Ad} with such id doesn't exist
	 */
	public Ad updateAd(long adId, Ad updatedAd) throws NonExistedAdException;
	
	/**
	 * Gets advertisement from data storage by Id
	 * @param id
	 * @return {@code Ad} object or {@code null} if advertisement with such id doesn't exist 
	 * @throws NonExistedAdException if {@code Ad} with such id doesn't exist
	 */
	public Ad get(long id) throws NonExistedAdException ;
	
	/**
	 * Gets all advertisements from data storage
	 * @return list of advertisements, never {@code null}
	 */
	public List<Ad> getAllAds();
	
	/**
	 * Gets {@code Ad} by id and delete it
	 * @throws NonExistedAdException if {@code Ad} with such id doesn't exist
	 */
	public void deleteById(long id) throws NonExistedAdException;
}

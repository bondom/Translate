package ua.translate.dao;

import java.util.Set;

import ua.translate.dao.impl.GetAdDaoImpl;
import ua.translate.model.Order;
import ua.translate.model.ad.Ad;
import ua.translate.model.ad.OralAd;
import ua.translate.model.searchbean.SearchOralAdBean;
import ua.translate.model.status.AdStatus;

public abstract class OralAdDao extends GetAdDaoImpl{
	
	/**
	 * Gets {@link OralAd} from data storage by id
	 * @param id
	 * @return {@code OralAd} object, or {@code null} if {@code OralAd} with
	 * such id doesn't exist in data storage.
	 */
	public abstract OralAd getOralAdById(long id);
	
	
	/**
	 * Gets {@code Set} of {@link OralAd}s from data storage, 
	 * which have SHOWED status, ordered by 
	 * {@link OralAd#getPublicationDateTime() OralAd.ublicationDateTime} and
	 * filtered by properties of {@link SearchOralAdBean} {@code searchAdBean}
	 * <br>Size of result {@code Set} is not more than {@code numberAdsOnPage}
	 * @param page -  page number, can't be less than 1
	 * @param numberAdsOnPage - number {@link OralAd}s, which can be displayed on 1 page
	 * @return set {@code OralAd}s, never {@code null}
	 */
	public abstract Set<OralAd> getFilteredOralAdsForShowing(int page,int numberAdsOnPage, 
												SearchOralAdBean searchAdBean);
	
	
	/**
	 * Returns number of {@link OralAd}s with status={@code adStatus} and filtered by properties 
	 * of {@link SearchOralAdBean} {@code searchAdBean}, which exist in data storage
	 */
	public abstract long getNumberOfOralAdsByStatusAndFilter(AdStatus adStatus,SearchOralAdBean searchAdBean);
}

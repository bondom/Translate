package ua.translate.dao;

import java.util.List;
import java.util.Set;

import ua.translate.model.ad.Ad;
import ua.translate.model.status.AdStatus;
import ua.translate.service.exception.WrongPageNumber;

public interface AdDao extends AbstractDao<Long, Ad>{
	/**
	 * Gets {@code Set} of {@link Ad}s from data storage, 
	 * which have {@code status==SHOWED}, ordered by {@link Ad#getPublicationDateTime()} 
	 * size of result {@code Set} is not more than {@code numberAdsOnPage}
	 * @param page -  page number, can't be less than 1
	 * @param numberAdsOnPage - number {@link Ad}s, which can be displayed on 1 page
	 * @return set {@code Ad}s, never {@code null}
	 */
	public Set<Ad> getAdsForShowing(int page,int numberAdsOnPage);
	
	/**
	 * <b>For ADMIN</b>
	 * <p>Gets {@code Set} of {@link Ad}s from data storage, 
	 * which have {@code status==NOTCHECKED}, ordered by {@link Ad#getPublicationDateTime()} 
	 * size of result {@code Set} is not more than {@code numberAdsOnPage}
	 * @param page -  page number, can't be less than 1
	 * @param numberAdsOnPage - number {@link Ad}s, which can be displayed on 1 page
	 * @return set {@code Ad}s, never {@code null}
	 */
	public Set<Ad> getAdsForChecking(int page,int numberAdsOnPage);
	
	/**
	 * Returns number of {@link Ad}s with status={@code adStatus}, which exist in data storage
	 */
	public long getNumberOfAdsByStatus(AdStatus adStatus);
	
	public Ad merge(Ad ad);
	
}

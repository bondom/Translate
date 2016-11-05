package ua.translate.dao;

import java.util.Set;

import ua.translate.dao.impl.GetAdDaoImpl;
import ua.translate.model.ad.WrittenAd;
import ua.translate.model.searchbean.SearchWrittenAdBean;
import ua.translate.model.status.AdStatus;

public abstract class WrittenAdDao extends GetAdDaoImpl {

	/**
	 * Gets {@link WrittenAd} from data storage by id
	 * @param id
	 * @return {@code WrittenAd} object, or {@code null} if {@code WrittenAd} with
	 * such id doesn't exist in data storage.
	 */
	public abstract WrittenAd getWrittenAdById(long id);
	
	
	/**
	 * Gets {@code Set} of {@link WrittenAd}s from data storage, 
	 * which have SHOWED status, ordered by 
	 * {@link WrittenAd#getPublicationDateTime() WrittenAd.publicationDateTime} from latest
	 *  to earliest and
	 * filtered by properties of {@link SearchWrittenAdBean} {@code searchAdBean}
	 * <br>Size of result {@code Set} is not more than {@code numberAdsOnPage}
	 * @param page -  page number, can't be less than 1
	 * @param numberAdsOnPage - number {@link WrittenAd}s, which can be displayed on 1 page
	 * @return set {@code WrittenAd}s, never {@code null}
	 */
	public abstract Set<WrittenAd> getFilteredWrittenAdsForShowing(int page,int numberAdsOnPage, 
												SearchWrittenAdBean searchAdBean);
	
	/**
	 * Returns number of {@link WrittenAd}s with status={@code adStatus} and filtered by properties 
	 * of {@link SearchWrittenAdBean} {@code searchAdBean}, which exist in data storage
	 */
	public abstract long getNumberOfWrittenAdsByStatusAndFilter(AdStatus adStatus,SearchWrittenAdBean searchAdBean);

	/**
	 * Gets Set of {@link WrittenAd}s from data storage, every element 
	 * of which have status, which is equal to one of {@code statuses}.
	 * @param statuses
	 */
	public abstract Set<WrittenAd>  getAllWrittenAdsByStatuses(Set<AdStatus> statuses);

}

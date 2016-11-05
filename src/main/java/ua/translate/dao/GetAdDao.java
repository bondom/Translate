package ua.translate.dao;

import java.util.Set;

import ua.translate.model.Order;
import ua.translate.model.ad.Ad;
import ua.translate.model.ad.Ad.TranslateType;
import ua.translate.model.status.AdStatus;

public abstract class GetAdDao extends AdDao {
	
	/**
	 *	Gets {@code Set} of {@link Ad}s from data storage,every {@code ad} of which
	 * meets the following demands: ad.status={@code adStatus}, ad.translateType={@code translateType}.
	 * <br>{@code Ad}s in set are ordered by 
	 * {@link Ad#getPublicationDateTime() Ad.publicationDateTime}, type of order is assigned by {@link Order} 
	 * {@code order}
	 * @param page -  page number, can't be less than 1
	 * @param numberAdsOnPage - number {@code Ad}s, which can be displayed on 1 page
	 * @return set {@code Ad}s, never {@code null}
	 */
	public abstract Set<Ad> getAdsByTranslateTypeAndStatusAndOrder(int page,int numberAdsOnPage,
			TranslateType translateType,AdStatus adStatus,Order order);
	

	/**
	 * Returns number of {@link Ad}s from data storage, which meet following demands:
	 * ad.status={@code adStatus} and ad.translateType = {@code translateType}
	 */
	public abstract long getNumberOfAdsByStatusAndTranslateType(AdStatus adStatus,
																TranslateType translateType);
	
	public abstract Set<Ad> getAllAdsByStatus(AdStatus adStatus);
	
	
}

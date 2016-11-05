package ua.translate.dao;

import java.util.Set;

import ua.translate.model.ad.Ad;
import ua.translate.model.ad.ArchievedAd;

public interface ArchievedAdDao extends AbstractDao<Long, ArchievedAd>{
		
	/**
	 * Gets {@link ArchievedAd} {@code archievedAd} from data storage 
	 * by id of its {@link ArchievedAd#getAd() ArchievedAd.ad}
	 * @param adId
	 * @return {@code ArchievedAd}, or {@code null}
	 */
	public ArchievedAd getArchievedAdByAdId(long adId);
	
	/**
	 * Gets all {@link ArchievedAd}s from data storage, 
	 * ordered by {@link ArchievedAd#getCreatingDateTime() ArchievedAd.creatingDateTime} in
	 * desc order
	 * @return Set of {@code ArchievedAd}, never {@code null}
	 */
	public Set<ArchievedAd> getAllArchievedAdsInDescOrder();
}

package ua.translate.service;

import java.util.Set;

import ua.translate.model.ad.ArchievedAd;
import ua.translate.service.exception.InvalidIdentifier;

public interface ArchievedAdService {
	
	/**
	 * Gets all {@link ArchievedAd}s from data storage, 
	 * ordered by {@link ArchievedAd#getCreatingDateTime() ArchievedAd.creatingDateTime} in
	 * desc order
	 * @return Set of {@code ArchievedAd}, never {@code null}
	 */
	public Set<ArchievedAd> getAllArchievedAdsInDescOrder();
	
	/**
	 * Gets {@link ArchievedAd} by id from data storage.
	 * <br>If retrieved object is {@code null}, exception is thrown.
	 * @param id
	 * @return
	 * @throws InvalidIdentifier if {@link ArchievedAd} with such id doesn't exist
	 * in data storage.
	 */
	public ArchievedAd getArchievedAdByAdId(long id) throws InvalidIdentifier;
}

package ua.translate.model.status;

import ua.translate.model.ad.Ad;

public enum AdStatus {
	EXPIRED,
	/**
	 * It is status of created advertisement
	 */
	SHOWED,
	/**
	 * It is status, when client accepted response of translator, 
	 * and translator begin to execute it 
	 */
	ACCEPTED,
	/**
	 * It is status, when translator have finished to execute {@link Ad}
	 */
	NOTCHECKED,
	EXECUTED
}

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
	 * and translator begin to execute his 
	 */
	ACCEPTED,
	/**
	 * It is status, when translator finish to execute {@code Ad}
	 */
	NOTCHECKED,
	EXECUTED
}

package ua.translate.model.status;

import ua.translate.model.ad.Ad;
import ua.translate.model.ad.WrittenAd;

public enum AdStatus {
	/**
	 * It is status of created advertisement
	 */
	SHOWED,
	/**
	 * It is status, when client accepted response of translator, 
	 * and translator began to execute it 
	 */
	ACCEPTED,
	
	/**
	 * It is status, when translator have finished to execute {@link WrittenAd}
	 */
	NOTCHECKED,
	
	/**
	 * It is status, when admin have checked {@link ResultDocument}, 
	 * related to {@link WrittenAd}
	 */
	CHECKED,
	
	/**
	 * It is status, when admin have checked {@link ResultDocument},
	 * but it needs to be edited
	 */
	REWORKING,
	
	/**
	 * It is status, when client didn't execute {@link WrittenAd} for
	 * {@link WrittenAd#getEndDate() End Date}
	 */
	FAILED,
	
	/**
	 * It is status, when client payed full cost for {@link Ad}, in case of
	 * {@link WrittenAd} he can download {@link ResultDocument}
	 */
	PAYED
	
}

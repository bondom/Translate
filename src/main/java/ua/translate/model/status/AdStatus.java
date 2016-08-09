package ua.translate.model.status;

public enum AdStatus {
	EXPIRED,
	/**
	 * It is status of created advertisement
	 */
	SHOWED,
	/**
	 * It is status, when client accept response of translator, 
	 * and translator begin to execute his 
	 */
	ACCEPTED,
	EXECUTED
}

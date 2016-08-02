package ua.translate.service;

import java.security.Principal;
import java.util.List;


import ua.translate.model.Client;
import ua.translate.model.ResponsedAd;
import ua.translate.model.ad.Ad;

public abstract class ClientService extends UserService<Client>{
	
	/**
	 * Returns all {@link ResponsedAd}s, related to this client
	 * @param email - email of authenticated client, 
	 * usually is retrieved from {@link Principal} object
	 */
	public abstract List<ResponsedAd> getResponsedAds(String email);
	
	/**
	 * Returns all {@link Ad}s, related to this client
	 * @param email - email of authenticated client, 
	 * usually is retrieved from {@link Principal} object
	 */
	public abstract List<Ad> getAds(String email);
	
	/**
	 * Gets {@link Client} object by id from data storage, never null
	 * @param email - email of authenticated client, <b>must</b> be retrieved from
	 * {@code Principal} object
	 */
	public abstract Client getClientByEmail(String email);
}

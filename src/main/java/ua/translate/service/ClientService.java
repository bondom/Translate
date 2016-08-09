package ua.translate.service;

import java.security.Principal;
import java.util.List;
import java.util.Set;

import ua.translate.model.Client;
import ua.translate.model.ad.Ad;
import ua.translate.model.ad.ResponsedAd;

public abstract class ClientService extends UserService<Client>{
	
	/**
	 * Returns all {@link ResponsedAd}s, related to this client
	 * @param email - email of authenticated client, 
	 * usually is retrieved from {@link Principal} object
	 */
	public abstract Set<ResponsedAd> getResponsedAds(String email);
	
	/**
	 * Returns all {@link Ad}s, related to this client
	 * @param email - email of authenticated client, 
	 * usually is retrieved from {@link Principal} object
	 */
	public abstract Set<Ad> getAds(String email);
	
	/**
	 * Gets {@link Client} object by email from data storage, never {@code null}
	 * @param email - email of authenticated client, <b>must</b> be retrieved from
	 * {@code Principal} object
	 */
	public abstract Client getClientByEmail(String email);
}

package org.dream.university.service;

import org.dream.university.model.Client;
import org.dream.university.model.User;
import org.springframework.web.servlet.ModelAndView;

public interface UserService<T extends User> {
	/**
	 * Registers user in system, adds his to db
	 * 
	 * @param user
	 * @return true if registration is successful or 
	 * false if registration fails because some data are not unique
	 */
	public boolean registerUser(T user);
	
	/**
	 * Edits user's profile
	 * 
	 * @param email - email of authenticated user, get from {Principal.class} object
	 * @param newUser - user with some new values of fields,
	 * which must update old one
	 * @return updated user, or {code null} if new email is registered in system already
	 */
	public T editUserProfile(String email,T newUser);
	
	/**
	 * Gets user from db by email
	 * 
	 * @param email - email of user
	 * @return registered user, or {code null} if user with that email
	 * is not registered
	 */
	public T getUserByEmail(String email);
	
	/**
	 * Updates avatar of user
	 * 
	 * @param email - email of existed user,
	 * usually is retrieving from {Principal.class} object 
	 * @param avatar
	 * @return updated user, never {@code null}
	 */
	public T updateAvatar(String email,byte[] avatar);

}

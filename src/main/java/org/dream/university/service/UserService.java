package org.dream.university.service;

import org.dream.university.model.User;
import org.springframework.web.servlet.ModelAndView;

public interface UserService {
	/**
	 * Registers user in system, adds his to db
	 * 
	 * @param user
	 * @return true if registration is successful or 
	 * false if registration fails because some data are not unique
	 */
	public boolean registerUser(User user);
	
	/**
	 * Edits user's profile
	 * 
	 * @param user - user, who exist in db
	 * @param newUser - user with some new values of fields,
	 * which must update old
	 * @return
	 */
	public User editUserProfile(User user, User newUser);
	
	/**
	 * Gets user from db by login
	 * 
	 * @param login - login of user
	 * @return registered user, or {code null} if user with that login
	 * is not registered
	 */
	public User getUserByLogin(String login);
	
	/**
	 * Gets user from db by email
	 * 
	 * @param email - email of user
	 * @return registered user, or {code null} if user with that email
	 * is not registered
	 */
	public User getUserByEmail(String email);
	
	/**
	 * Updates avatar of user
	 * 
	 * @param login - login of existed user,
	 * usually is retrieving from {Principal.class} object 
	 * @param avatar
	 * @return updated user, never {@code null}
	 */
	public User updateAvatar(String login,byte[] avatar);
}

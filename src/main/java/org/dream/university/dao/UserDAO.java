package org.dream.university.dao;

import org.dream.university.model.User;

public interface UserDao<T extends User>{
	
	/**
	 * Gets user from db by email
	 * @return existed user, or {code null}, if user with that email is not registered
	 */
	public T getUserByEmail(String email);
}

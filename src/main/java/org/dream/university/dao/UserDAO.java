package org.dream.university.dao;

import org.dream.university.model.User;

public interface UserDAO extends GenericDAO<User>{
	public User getUserByLogin(String login);
	public User getUserByEmail(String email);
}

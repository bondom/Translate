package org.dream.university.service;

import org.dream.university.model.User;
import org.springframework.web.servlet.ModelAndView;

public interface UserService {
	public boolean registerUser(User user);
}

package org.dream.university.service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import org.dream.university.dao.UserDAO;
import org.dream.university.model.User;
import org.dream.university.model.UserStatus;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


@Service("userService")
@Transactional(propagation = Propagation.REQUIRES_NEW )
public class UserServiceImpl implements UserService, UserDetailsService{

	@Autowired
	private UserDAO userDao;
	
	@Override
	public boolean registerUser(User user) {
		User userWithTheSameLogin = userDao.getUserByLogin(user.getLogin());
		if(!Objects.isNull(userWithTheSameLogin)){
			//if user with the same login registered already
			return false;
		}else{
			User userWithTheSamePassword = userDao.getUserByEmail(user.getEmail());
			if(!Objects.isNull(userWithTheSamePassword)){
				//if user with the same email registered already
				return false;
			}
			else{
				//if user's credentials are unique
				userDao.create(user);
				return true;
			}
		}
		
	}
	@Override
	public User getUserByLogin(String login) {
		User userFromDB = userDao.getUserByLogin(login);
		return userFromDB;
	}
	
	@Override
	public User getUserByEmail(String email){
		User userFromDB = userDao.getUserByEmail(email);
		return userFromDB;
	}
	@Override
	public User updateAvatar(String login, byte[] image){
		User userFromDB = userDao.getUserByLogin(login);
		userFromDB.setImage(image);
		User savedUser = userDao.update(userFromDB);
		return savedUser;
		
	}

	@Override
	@Transactional(readOnly = true)
	public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
		// TODO Auto-generated method stub
		User user = userDao.getUserByLogin(userName); 
		if(user!=null){
			boolean enabled = user.getStatus().equals(UserStatus.ACTIVE);
			boolean accountNonExpired = user.getStatus().equals(UserStatus.ACTIVE);
			boolean credentialsNonExpired = user.getStatus().equals(UserStatus.ACTIVE);
			boolean accountNonLocked = user.getStatus().equals(UserStatus.ACTIVE);
			
			Collection<GrantedAuthority> authorities = new ArrayList<>();
			authorities.add(new SimpleGrantedAuthority(user.getRole()));
	
			org.springframework.security.core.userdetails.User securityUser = 
					new org.springframework.security.core.userdetails.User(
							user.getLogin(), user.getPassword(), enabled, 
							accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
			return securityUser;
		}else{
			throw new UsernameNotFoundException("Invalid user login");
		}
	}
	@Override
	public User editUserProfile(User user, User newUser) {
		user.setName(newUser.getName());
		user.setPhoneNumber(newUser.getPhoneNumber());
		user.setSurname(newUser.getSurname());
		user.setEmail(newUser.getEmail());
		User updatedUser = userDao.update(user);
		return updatedUser;
	}

}

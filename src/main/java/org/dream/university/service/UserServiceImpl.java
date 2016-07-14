package org.dream.university.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import org.dream.university.dao.AbstractDao;
import org.dream.university.dao.UserDao;
import org.dream.university.model.User;
import org.dream.university.model.UserRole;
import org.dream.university.model.UserStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


@Service("userService")
@Transactional(propagation = Propagation.REQUIRES_NEW )
public class UserServiceImpl /*implements UserService, UserDetailsService*/{
/*
	@Autowired
	private UserDao userDao;
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean registerUser(User user) {
		User userWithTheSameEmail = userDao.getUserByEmail(user.getEmail());
		if(!Objects.isNull(userWithTheSameEmail)){
			//if user with the same email registered already
			return false;
		}else{
			//if user's credentials are unique
			String encodedPassword = encodePassword(user.getPassword());
			user.setPassword(encodedPassword);
			user.setRole(UserRole.CLIENT_ROLE);
			user.setStatus(UserStatus.ACTIVE);
			((AbstractDao<Integer, User>)userDao).persist(user);
			return true;
		}
	}
	
	private String encodePassword(String password){
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String encodedString = passwordEncoder.encode(password);
		return encodedString;
	}
	
	@Override
	public User getUserByEmail(String email){
		User userFromDB = userDao.getUserByEmail(email);
		return userFromDB;
	}
	@SuppressWarnings("unchecked")
	@Override
	public User updateAvatar(String email, byte[] image){
		User userFromDB = userDao.getUserByEmail(email);
		userFromDB.setAvatar(image);
		User savedUser = ((AbstractDao<Integer,User>)userDao).update(userFromDB);
		return savedUser;
		
	}

	@Override
	@Transactional(readOnly = true)
	public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
		// TODO Auto-generated method stub
		User user = userDao.getUserByEmail(userName); 
		if(user!=null){
			boolean enabled = user.getStatus().equals(UserStatus.ACTIVE);
			boolean accountNonExpired = user.getStatus().equals(UserStatus.ACTIVE);
			boolean credentialsNonExpired = user.getStatus().equals(UserStatus.ACTIVE);
			boolean accountNonLocked = user.getStatus().equals(UserStatus.ACTIVE);
			
			Collection<GrantedAuthority> authorities = new ArrayList<>();
			authorities.add(new SimpleGrantedAuthority(user.getRole().toString()));
	
			org.springframework.security.core.userdetails.User securityUser = 
					new org.springframework.security.core.userdetails.User(
							user.getEmail(), user.getPassword(), enabled, 
							accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
			return securityUser;
		}else{
			throw new UsernameNotFoundException("Invalid user login");
		}
	}
	@SuppressWarnings("unchecked")
	@Override
	public User editUserProfile(User user, User newUser) {
		user.setFirstName(newUser.getFirstName());
		user.setPhoneNumber(newUser.getPhoneNumber());
		user.setLastName(newUser.getLastName());
		user.setEmail(newUser.getEmail());
		User updatedUser = ((AbstractDao<Integer, User>)userDao).update(user);
		return updatedUser;
	}*/

}

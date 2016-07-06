package org.dream.university.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import org.dream.university.dao.UserDAO;
import org.dream.university.model.User;
import org.dream.university.model.UserStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


@Service("userService")
@Transactional(propagation = Propagation.REQUIRES_NEW )
public class UserServiceImpl implements UserService, UserDetailsService{

	@Autowired
	private UserDAO userDao;
	
	@Override
	public boolean registerUser(User user) {
		User userWithTheSameLogin = userDao.getUserByLogin(user.getUserLogin());
		if(!Objects.isNull(userWithTheSameLogin)){
			//if user with the same login registered already
			return false;
		}else{
			User userWithTheSamePassword = userDao.getUserByEmail(user.getUserEmail());
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
	@Transactional(readOnly = true)
	public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
		// TODO Auto-generated method stub
		User user = userDao.getUserByLogin(userName); 
		if(user!=null){
			boolean enabled = user.getUserStatus().equals(UserStatus.ACTIVE);
			boolean accountNonExpired = user.getUserStatus().equals(UserStatus.ACTIVE);
			boolean credentialsNonExpired = user.getUserStatus().equals(UserStatus.ACTIVE);
			boolean accountNonLocked = user.getUserStatus().equals(UserStatus.ACTIVE);
			
			Collection<GrantedAuthority> authorities = new ArrayList<>();
			authorities.add(new SimpleGrantedAuthority(user.getRole()));
	
			org.springframework.security.core.userdetails.User securityUser = 
					new org.springframework.security.core.userdetails.User(
							user.getUserLogin(), user.getUserPassword(), enabled, 
							accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
			return securityUser;
		}else{
			throw new UsernameNotFoundException("Invalid user login");
		}
	}
}

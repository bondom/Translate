package ua.translate.service.impl;

import java.util.ArrayList;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ua.translate.controller.translator.GetTranslatorController;
import ua.translate.dao.UserDao;
import ua.translate.model.UserEntity;
import ua.translate.model.security.UserImpl;
import ua.translate.model.status.EmailStatus;
import ua.translate.model.status.UserStatus;

@Service("userDetailsServiceImpl")
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class UserDetailsServiceImpl implements UserDetailsService{

	private Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);
	
	@Autowired
	private UserDao userDao;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserEntity user = userDao.getUserEntityByEmail(username); 
		if(user!=null){
			logger.info("User exists, his role={}",user.getRole());
			boolean enabled = user.getStatus().equals(UserStatus.ACTIVE);
			boolean accountNonExpired = user.getStatus().equals(UserStatus.ACTIVE);
			boolean credentialsNonExpired = user.getStatus().equals(UserStatus.ACTIVE);
			boolean accountNonLocked = user.getStatus().equals(UserStatus.ACTIVE);
			
			Collection<GrantedAuthority> authorities = new ArrayList<>();
			authorities.add(new SimpleGrantedAuthority(user.getRole().toString()));
	
			UserImpl securityUser = 
					new UserImpl(
							user.getEmail(), user.getPassword(), enabled, 
							accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
			return securityUser;
		}else{
			logger.info("User=null");
			throw new UsernameNotFoundException("Invalid user email or password");
		}
	}

}

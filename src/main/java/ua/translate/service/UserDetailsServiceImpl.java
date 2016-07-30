package ua.translate.service;

import java.util.ArrayList;
import java.util.Collection;

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

import ua.translate.dao.UserDao;
import ua.translate.model.EmailStatus;
import ua.translate.model.User;
import ua.translate.model.UserStatus;
import ua.translate.model.security.UserImpl;
import ua.translate.service.exception.NotConfirmedEmailException;

@Service("detailsService")
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class UserDetailsServiceImpl implements UserDetailsService{

	@Autowired
	@Qualifier(value = "userDao")
	private UserDao userDao;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// TODO Auto-generated method stub
				User user = userDao.getUserByEmail(username); 
				if(user!=null){
					if(user.getEmailStatus().equals(EmailStatus.NOTCONFIRMED)){
						/*!!!! Show message that email is not confirmed!!!!*/
					}
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
					throw new UsernameNotFoundException("Invalid user email or password");
				}
	}

}

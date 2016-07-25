package ua.translate.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ua.translate.dao.AbstractDao;
import ua.translate.dao.UserDao;
import ua.translate.model.Client;
import ua.translate.model.User;
import ua.translate.model.UserRole;
import ua.translate.model.UserStatus;
import ua.translate.model.ad.Ad;
import ua.translate.model.ad.AdStatus;
import ua.translate.model.ad.ResponsedAd;

@Service("clientService")
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class ClientServiceImpl extends UserService<Client>{

	@Autowired
	@Qualifier("clientDao")
	private UserDao<Client> clientDao;
	
	@SuppressWarnings("unchecked")
	@Override
	public long registerUser(Client user) {
		String encodedPassword = encodePassword(user.getPassword());
		user.setPassword(encodedPassword);
		user.setRole(UserRole.ROLE_CLIENT);
		user.setStatus(UserStatus.NOTCONFIRMED);
		user.setRegistrationTime(LocalDateTime.now());
		((AbstractDao<Integer, Client>)clientDao).save(user);
		return user.getId();
	}

	@Override
	public Client editUserProfile(String email, Client newUser,boolean changeEmail) {
		Client client = (Client)getUserByEmail(email);
		client.setFirstName(newUser.getFirstName());
		client.setLastName(newUser.getLastName());
		client.setBirthday(newUser.getBirthday());
		client.setCity(newUser.getCity());
		client.setCountry(newUser.getCountry());
		client.setPhoneNumber(newUser.getPhoneNumber());
		if(changeEmail){
			client.setEmail(newUser.getEmail());
			/*UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(userName, password);
	
		    // Authenticate the user
		    Authentication authentication = authenticationManager.authenticate(authRequest);
		    SecurityContext securityContext = SecurityContextHolder.getContext();
		    securityContext.setAuthentication(authentication);

		    // Create a new session and add the security context.
		    HttpSession session = request.getSession(true);
		    session.setAttribute("SPRING_SECURITY_CONTEXT", securityContext);*/
		}
		return client;
	}
	
	public List<ResponsedAd> getResponsedAds(String email){
		Client client = (Client)getUserByEmail(email);
		List<ResponsedAd> ads = client.getResponsedAds();
		return ads;
	}

	@Override
	public void confirmRegistration(String email) {
		Client client = clientDao.getUserByEmail(email);
		client.setStatus(UserStatus.ACTIVE);
	}
	
}

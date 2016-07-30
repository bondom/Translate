package ua.translate.model.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

public class UserImpl extends User{

	private String modifiableUsername;
	
	public UserImpl(String username, String password, boolean enabled, boolean accountNonExpired,
			boolean credentialsNonExpired, boolean accountNonLocked,
			Collection<? extends GrantedAuthority> authorities) {
		super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
		this.modifiableUsername = username;
	}
	
	public UserImpl(String username, String password,
			Collection<? extends GrantedAuthority> authorities) {
		super(username, password, true, true, true, true, authorities);
		this.modifiableUsername = username;
	}
	
	public void setUsername(String username){
		this.modifiableUsername = username;
	}
	
	public String getUsername(){
		return this.modifiableUsername;
	}

}

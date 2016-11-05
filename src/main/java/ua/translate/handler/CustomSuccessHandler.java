package ua.translate.handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
	
	private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
	@Override
	protected void handle(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		String url = determineTargetUrl(authentication);
		if(response.isCommitted()){
			return;
		}
		
		redirectStrategy.sendRedirect(request, response, url);
		
	}
	
	protected String determineTargetUrl(Authentication authentication){
		
		Collection<? extends GrantedAuthority> authorities = 
								authentication.getAuthorities();
		List<String> roles = new ArrayList<>();
		for(GrantedAuthority authority: authorities){
			roles.add(authority.getAuthority());
		}
		
		if(roles.contains("ROLE_ADMIN")){
			return "/bulbular/adminPage";
		}else if(roles.contains("ROLE_CLIENT")){
			return "/client/profile";
		}else if(roles.contains("ROLE_TRANSLATOR")){
			return "/translator/profile";
		}
		return "/login";
	}
	
	
}

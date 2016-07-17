package org.dream.university.handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Component("customAccessDeniedHandler")
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
	
	private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
	
	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
			AccessDeniedException accessDeniedException) throws IOException, ServletException {
		String url = determineTargetUrl();
		redirectStrategy.sendRedirect(request, response, url);
		
	}
	protected String determineTargetUrl(){
		Authentication authentication = 
                SecurityContextHolder.getContext().getAuthentication();
		Collection<? extends GrantedAuthority> authorities = 
				authentication.getAuthorities();
		
		List<String> roles = new ArrayList<>();
		for(GrantedAuthority authority: authorities){
			System.out.println(authority.getAuthority());
			roles.add(authority.getAuthority());
		}
		if(roles.contains("ROLE_CLIENT")){
			return "/client/profile";
		}else if (roles.contains("ROLE_TRANSLATOR")){
			return "/translator/profile";
		}else if (roles.contains("ROLE_ADMIN")){
			/**
			 * Доделать!!
			 */
			return "";
		}else{
			/**
			 * Такой сценарий вообще не должен произойти
			 */
			return "/index";
		}
	}

}

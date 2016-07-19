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
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

public class CustomSavedAwareHandler extends SavedRequestAwareAuthenticationSuccessHandler{
	
	private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
	
	@Override
	protected void handle(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		String targetUrl = determineTargetUrl(authentication);
		logger.debug("targetUrl:"+targetUrl);
		if (response.isCommitted()) {
			logger.debug("Response has already been committed. Unable to redirect to "
					+ targetUrl);
			return;
		}

		redirectStrategy.sendRedirect(request, response, targetUrl);
	}
	
	protected String determineTargetUrl(Authentication authentication){
			
			Collection<? extends GrantedAuthority> authorities = 
									authentication.getAuthorities();
			List<String> roles = new ArrayList<>();
			for(GrantedAuthority authority: authorities){
				roles.add(authority.getAuthority());
			}
			
			if(roles.contains("ROLE_ADMIN")){
				return "/admin/adminPage";
			}else if(roles.contains("ROLE_USER")){
				return "/personal/profile";
			}else return "/login";
		}

}

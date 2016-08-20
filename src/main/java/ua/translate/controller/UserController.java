package ua.translate.controller;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import ua.translate.model.User;
import ua.translate.model.UserRole;
import ua.translate.model.security.UserImpl;

public class UserController {
	
	@Autowired
	private AuthenticationTrustResolver authenticationTrustResolver;
	
	/**
	 * Checks if user is authenticated via remember-me authentication.
	 * <p>Returns true if user is authenticated via remember-me authentication, otherwise false
	 */
	protected boolean isRememberMeAuthenticated() {
		Authentication authentication = 
                    SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null) {
			return false;
		}
		return RememberMeAuthenticationToken.class.isAssignableFrom(authentication.getClass());
	}
	
	/*!!!!Don't working redirecting to old url!!!!*/
	/*!!!!Write for translator too!!!!*/
	/**
	 * Sets {@code targetUrl} attribute to session depending of {@link UserRole}
	 */
	@SuppressWarnings("unused")
	protected void setRememberMeTargetUrlToSession(User user, HttpServletRequest request){
		HttpSession session = request.getSession(false);
		if(session!=null){
			UserRole role= user.getRole();
			if(role.equals(UserRole.ROLE_CLIENT)){
				session.setAttribute("targetUrl", "/client/edit");
			}	
		}
	}
	
	/**
	 * Returns {@code targetUrl} attribute from session, if such exists,
	 * otherwise returns empty string
	 */
	protected String getRememberMeTargetUrlFromSession(HttpServletRequest request){
		String targetUrl = "";
		HttpSession session = request.getSession(false);
		if(session!=null){
			targetUrl = session.getAttribute("targetUrl")==null?""
                             :session.getAttribute("targetUrl").toString();
		}
		return targetUrl;
	}
	
	/**
     * This method returns true if user is anonymous , else false.
     */
    protected boolean isCurrentAuthenticationAnonymous() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authenticationTrustResolver.isAnonymous(authentication);
    }
    
    /**
     * Replaces {@link org.springframework.security.core.userdetails.User} old username with new one 
     */
    protected void refreshUsername(String newUsername){
    	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserImpl userDetails = (UserImpl) authentication.getPrincipal();
        userDetails.setUsername(newUsername);
    }
}

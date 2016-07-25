package ua.translate.controller;

import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import ua.translate.model.User;
import ua.translate.model.UserRole;

public class UserController {
	
	@Autowired
	private AuthenticationTrustResolver authenticationTrustResolver;
	
	/**
	 * Converts user's avatar from byte[] representation to String representation
	 * @throws UnsupportedEncodingException
	 */
	protected String convertAvaForRendering(byte[] ava) throws UnsupportedEncodingException{
		byte[] encodeBase64 = Base64.encodeBase64(ava); 
		String base64Encoded = new String(encodeBase64,"UTF-8");
		return base64Encoded;
	}
	
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
	private void setRememberMeTargetUrlToSession(User user, HttpServletRequest request){
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
			System.out.println("targetUrl = " + targetUrl);
		}
		return targetUrl;
	}
	
	/**
     * This method returns true if user is already authenticated [logged-in], else false.
     */
    protected boolean isCurrentAuthenticationAnonymous() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authenticationTrustResolver.isAnonymous(authentication);
    }
}

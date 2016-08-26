package ua.translate.controller;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import ua.translate.model.Language;
import ua.translate.model.User;
import ua.translate.model.UserRole;
import ua.translate.model.ad.Currency;
import ua.translate.model.ad.TranslateType;
import ua.translate.model.security.UserImpl;

public class UserController {
	
	@Autowired
	private AuthenticationTrustResolver authenticationTrustResolver;
	
	/**
	 * This array contains countries, which can be used by user for registration,
	 * creating and searching Ad. 
	 * <p><b>NOTE:</b> In that moment there is several countries for testing purposes
	 */
	private static String[] ALLOWED_COUNTRIES = {"Ukraine","Russia"};
	
	/**
	 * This array contains cities, which can be used by user for registration,
	 * creating and searching Ad. 
	 * <p><b>NOTE:</b> In that moment there is several cities for testing purposes
	 */
	private static String[] ALLOWED_CITIES= {"Kiev","Chernivtsi","Moscow"};
	
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
    
    /**
     * Returns {@code Map}, where keys - {@link Language} values,
     * objects - user-friendly names of languages
     */
    protected Map<String,String> getLanguagesForSelect(){
  		Language[] languages = Language.values();
  		Map<String, String> languagesMap = new HashMap<String, String>();
  		for(Language language:languages){
  			//creating user-friendly name of language and adding to map
  			String renderedLanguage = language.name();
  			renderedLanguage = renderedLanguage.substring(0, 1) + 
  					renderedLanguage.substring(1, renderedLanguage.length()).toLowerCase();
  			languagesMap.put(language.name(), renderedLanguage);
  		}
  		return languagesMap;
    }
    
    
    /**
     * Returns {@code Map}, where keys - {@link TranslateType} values,
     * objects - user-friendly names of translate types
     */
    protected Map<String,String> getTranslateTypesForSelect(){
  		TranslateType[] types = TranslateType.values();
  		Map<String, String> typesMap = new HashMap<String, String>();
  		for(TranslateType type:types){
  			String renderedTranslateType = type.name();
  			renderedTranslateType = renderedTranslateType.substring(0, 1) + 
  					renderedTranslateType.substring(1, renderedTranslateType.length()).toLowerCase();
  			typesMap.put(type.name(), renderedTranslateType);
  		}
  		return typesMap;
  	}
    
    /**
     * Returns {@code Map}, where keys - {@link Currency} values,
     * objects - user-friendly names of currencies
     */
    protected Map<String,String> getCurrenciesForSelect(){
    	Currency[] currencies = Currency.values();
  		Map<String,String> currenciesMap = new HashMap<>();
  		for(Currency currency: currencies){
  			currenciesMap.put(currency.name(),"(" +currency.name()+")");
  		}
  		return currenciesMap;
    }
    
    /**
     * Returns {@code Map}, where keys and
     * objects - names of countries
     */
    protected Map<String,String> getCountriesForSelect(){
  		Map<String,String> countriesMap = new HashMap<>();
  		for(String country: ALLOWED_COUNTRIES){
  			countriesMap.put(country,country);
  		}
  		return countriesMap;
    }
    
    /**
     * Returns {@code Map}, where keys and
     * objects - names of cities
     */
    protected Map<String,String> getCitiesForSelect(){
  		Map<String,String> citiesMap = new HashMap<>();
  		for(String city: ALLOWED_CITIES){
  			citiesMap.put(city,city);
  		}
  		return citiesMap;
    }
    
}

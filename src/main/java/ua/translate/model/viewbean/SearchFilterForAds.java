package ua.translate.model.viewbean;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.format.annotation.NumberFormat;

import ua.translate.model.validator.FieldNotMatch;

/**
 * It is class, instance which is transmitted to user for getting restrictions on ads
 * This class is for interaction between web and service layers
 * @author Yuriy Phediv
 *
 */
public class SearchFilterForAds {
	
	@NotEmpty
	private String translateType;
	@NotEmpty
	private String country;
	@NotEmpty
	private String city;
	@NotEmpty
	private String initLanguage;
	@NotEmpty
	private String resultLanguage;
	@NotEmpty
	private String currency;

	private int minCost;
	
	private int maxCost;
	
	
	public SearchFilterForAds() {
		super();
	}
	public SearchFilterForAds(String translateType, String country, String city, String initLanguage,
			String resultLanguage, String currency, int minCost, int maxCost) {
		super();
		this.translateType = translateType;
		this.country = country;
		this.city = city;
		this.initLanguage = initLanguage;
		this.resultLanguage = resultLanguage;
		this.currency = currency;
		this.minCost = minCost;
		this.maxCost = maxCost;
	}
	public String getTranslateType() {
		return translateType;
	}
	public void setTranslateType(String translateType) {
		this.translateType = translateType;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getInitLanguage() {
		return initLanguage;
	}
	public void setInitLanguage(String initLanguage) {
		this.initLanguage = initLanguage;
	}
	
	public String getResultLanguage() {
		return resultLanguage;
	}
	public void setResultLanguage(String resultLanguage) {
		this.resultLanguage = resultLanguage;
	}
	
	public int getMinCost() {
		return minCost;
	}
	public void setMinCost(int minCost) {
		this.minCost = minCost;
	}
	public int getMaxCost() {
		return maxCost;
	}
	public void setMaxCost(int maxCost) {
		this.maxCost = maxCost;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	
	
	
}

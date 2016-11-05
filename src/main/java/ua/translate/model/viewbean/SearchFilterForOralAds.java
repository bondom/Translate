package ua.translate.model.viewbean;

import org.hibernate.validator.constraints.NotEmpty;

public class SearchFilterForOralAds extends SearchFilterForAds{
	
	@NotEmpty
	private String country;
	@NotEmpty
	private String city;
	
	
	public SearchFilterForOralAds() {
		super();
	}
	
	public SearchFilterForOralAds(String country, String city, 
			String initLanguage, String resultLanguage, String currency, 
			int minCost, int maxCost) {
		super(initLanguage,resultLanguage,currency,minCost,maxCost);
		this.country = country;
		this.city = city;
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
	
	
}

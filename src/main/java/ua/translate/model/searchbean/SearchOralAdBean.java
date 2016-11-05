package ua.translate.model.searchbean;

import ua.translate.model.Language;
import ua.translate.model.ad.Currency;
import ua.translate.model.ad.Ad.TranslateType;

public class SearchOralAdBean extends SearchAdBean{
	
	public SearchOralAdBean(Currency currency, Language resultLanguage, Language initLanguage, int minCost,
			int maxCost) {
		super(currency, resultLanguage, initLanguage, minCost, maxCost);
		this.translateType = TranslateType.ORAL;
	}
	
	public SearchOralAdBean(Currency currency, Language resultLanguage, Language initLanguage, 
			String country,String city,int minCost,int maxCost) {
		super(currency, resultLanguage, initLanguage, minCost, maxCost);
		this.translateType = TranslateType.ORAL;
		this.country = country;
		this.city = city;
	}
	
	private String country;
	private String city;
	
	
	
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

package ua.translate.model.searchbean;

import ua.translate.model.Language;
import ua.translate.model.ad.Currency;
import ua.translate.model.ad.TranslateType;

/**
 * This class represents {@link Ad} with parameters, specified by user for
 * searching {@code Ad}s with restrictions.
 * It is class for interaction between service and dao layer
 * @author Yuriy Phediv
 *
 */
public class SearchAdBean {
	
	private Currency currency;
	private Language resultLanguage;
	private Language initLanguage;
	private TranslateType translateType;
	private String country;
	private String city;
	private int minCost;
	private int maxCost;

	public SearchAdBean(Currency currency, Language resultLanguage, Language initLanguage, TranslateType translateType,
			String country, String city, int minCost, int maxCost) {
		super();
		this.currency = currency;
		this.resultLanguage = resultLanguage;
		this.initLanguage = initLanguage;
		this.translateType = translateType;
		this.country = country;
		this.city = city;
		this.minCost = minCost;
		this.maxCost = maxCost;
	}
	public Currency getCurrency() {
		return currency;
	}
	public void setCurrency(Currency currency) {
		this.currency = currency;
	}
	public Language getResultLanguage() {
		return resultLanguage;
	}
	public void setResultLanguage(Language resultLanguage) {
		this.resultLanguage = resultLanguage;
	}
	public Language getInitLanguage() {
		return initLanguage;
	}
	public void setInitLanguage(Language initLanguage) {
		this.initLanguage = initLanguage;
	}
	public TranslateType getTranslateType() {
		return translateType;
	}
	public void setTranslateType(TranslateType translateType) {
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
	
	
	
}

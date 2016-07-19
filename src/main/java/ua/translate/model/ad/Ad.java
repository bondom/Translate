package ua.translate.model.ad;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import ua.translate.model.Client;
import ua.translate.model.Comment;
import ua.translate.model.customvalidators.FieldMatch;

@Entity
@NamedQueries({
	@NamedQuery(name =  "allAds",
			query = "from Ad")
})
@FieldMatch(first = "initLanguage",second = "resultLanguage", message = "Languages must be different")
@Table(name = "AD_TEST")
public class Ad  implements Serializable{
	
	/**
	 * Version of this class in production 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "standart",initialValue = 1)
	@GeneratedValue(generator = "standart")
	@Column(name = "AD_ID")
	private long id;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "CLIENT",nullable = false)
	private Client client;
	
	@NotNull
	@Column(name = "AD_NAME", nullable = false)
	private String name;
	
	@NotNull
	@Column(name = "AD_DESC",nullable = false,length = 1000)
	
	@Lob
	@Size(min = 0, max = 1000)
	private String description;

	@Column(name = "AD_COUNTRY", nullable = false)
	private String country;
	
	@Column(name = "AD_CITY", nullable = false)
	private String city;
	

	@NotNull
	/**
	 * �������� �������� ���������� ����
	 */
	@DateTimeFormat(iso = ISO.DATE,pattern = "dd.MM.yyyy")
	@Column(name = "AD_END_DATE",nullable = false)
	private LocalDate endDate;
	
	@Column(name = "AD_INIT_LANGUAGE",nullable = false)
	@Enumerated(EnumType.STRING)
	private Language initLanguage;
	
	@Column(name = "AD_RESULT_LANGUAGE",nullable = false)
	@Enumerated(EnumType.STRING)
	private Language resultLanguage;
	
	@Column(name = "AD_TRANSLATE_TYPE",nullable = false)
	@Enumerated(EnumType.STRING)
	private TranslateType translateType;
	
	@Lob
	@Column(name = "AD_FILE")
	private byte[] file;
	
	@NotNull
	@Column(name = "AD_COST",nullable = false,precision = 2)
	private double cost;
	
	@Column(name = "AD_CURRENCY",nullable = false)
	@Enumerated(EnumType.STRING)
	private Currency currency;
	
	@Column(name = "AD_CREATING_DATE",nullable = false)
	private LocalDateTime creationDateTime;
	
	@Column(name = "AD_STATUS",nullable = false)
	@Enumerated(EnumType.STRING)
	private AdStatus status;
	
	public Ad(){}

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}

	
	public LocalDateTime getCreationDateTime() {
		return creationDateTime;
	}

	public void setCreationDateTime(LocalDateTime creationDateTime) {
		this.creationDateTime = creationDateTime;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}

	public Language getInitLanguage() {
		return initLanguage;
	}

	public void setInitLanguage(Language initLanguage) {
		this.initLanguage = initLanguage;
	}

	public Language getResultLanguage() {
		return resultLanguage;
	}

	public void setResultLanguage(Language resultLanguage) {
		this.resultLanguage = resultLanguage;
	}

	public TranslateType getTranslateType() {
		return translateType;
	}

	public void setTranslateType(TranslateType translateType) {
		this.translateType = translateType;
	}

	public byte[] getFile() {
		return file;
	}

	public void setFile(byte[] file) {
		this.file = file;
	}

	public double getCost() {
		return cost;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}

	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	
	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
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

	public AdStatus getStatus() {
		return status;
	}

	public void setStatus(AdStatus status) {
		this.status = status;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	@Override
	public boolean equals(Object obj){
		Ad ad = (Ad)obj;
		if(ad == null){
			return false;
		}
		return (this.getId() ==ad.getId());
	}
	
	@Override
	public int hashCode(){
		return Long.hashCode(id);
	}
	
}
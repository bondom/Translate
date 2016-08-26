package ua.translate.model.ad;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import ua.translate.model.Client;
import ua.translate.model.Comment;
import ua.translate.model.Language;
import ua.translate.model.Translator;
import ua.translate.model.status.AdStatus;
import ua.translate.model.validator.FieldNotMatch;

@Entity
@NamedQueries({
	@NamedQuery(name =  "getAllAds",
			query = "from Ad"),
	@NamedQuery(name =  "getAdsByStatusAndDescOrderByPubDate",
	query = "from Ad ad where ad.status = :status order by ad.publicationDateTime desc"),
	@NamedQuery(name =  "getAdsByStatusAndAscOrderByPubDate",
	query = "from Ad ad where ad.status = :status order by ad.publicationDateTime asc")
})
@FieldNotMatch(first = "initLanguage",second = "resultLanguage", message = "Languages must be different")
@Table(name = "AD_TEST")
@Inheritance(strategy=InheritanceType.JOINED)
public class Ad  implements Serializable{
	
	/**
	 * Version of this class in production 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "standart",initialValue = 1)
	@GeneratedValue(generator = "standart",strategy =GenerationType.SEQUENCE)
	@Column(name = "AD_ID")
	private long id;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "CLIENT",nullable = false)
	private Client client;
	
	@NotBlank
	@Column(name = "AD_NAME", nullable = false)
	private String name;
	
	@NotBlank
	@Column(name = "AD_DESC",nullable = false,length = 1000)
	@Lob
	@Size(min = 0, max = 1000)
	private String description;

	
	 /*!!!!Добавить проверку валидности даты!!!!*/
	 
	@Column(name = "AD_INIT_LANGUAGE",nullable = false)
	@Enumerated(EnumType.STRING)
	private Language initLanguage;
	
	@Column(name = "AD_RESULT_LANGUAGE",nullable = false)
	@Enumerated(EnumType.STRING)
	private Language resultLanguage;
	
	@Column(name = "AD_COST",nullable = false)
	private Integer cost;
	
	@Column(name = "AD_CURRENCY",nullable = false)
	@Enumerated(EnumType.STRING)
	private Currency currency;
	
	@Column(name = "AD_PUBLICATIO_DATE_TIME",nullable = false)
	private LocalDateTime publicationDateTime;
	
	@Column(name = "AD_STATUS",nullable = false)
	@Enumerated(EnumType.STRING)
	private AdStatus status;
	
	@OneToMany(fetch = FetchType.EAGER,orphanRemoval = true,mappedBy = "ad")
	@Cascade(CascadeType.ALL)
	@Fetch(FetchMode.SELECT)
	private Set<RespondedAd> respondedAds = new LinkedHashSet<>();
	
	@Column(name = "AD_TRANSLATE_TYPE",nullable = false)
	@Enumerated(EnumType.STRING)
	private TranslateType translateType;
	
	/**
	 * Properties for Ad with translateType={@link TranslateType#ORAL}
	 */
	@Column(name = "ORAL_AD_COUNTRY")
	private String country;
	
	@Column(name = "ORAL_AD_CITY")
	private String city;
	
	@DateTimeFormat(iso = ISO.DATE_TIME,pattern = "dd.MM.yyyy HH:mm:ss")
	@Column(name = "ORAL_AD_INIT_DATE")
	private LocalDateTime initialDateTime;
	
	@DateTimeFormat(iso = ISO.DATE_TIME,pattern = "dd.MM.yyyy HH:mm:ss")
	@Column(name = "ORAL_AD_FINISH_DATE")
	private LocalDateTime finishDateTime;
	
	/**
	 * Properties for Ad with translateType={@link TranslateType#WRITTEN}
	 */
	@DateTimeFormat(iso = ISO.DATE,pattern = "dd.MM.yyyy")
	@Column(name = "WRITTEN_AD_END_DATE")
	private LocalDate endDate;
	
	@OneToOne(mappedBy = "ad",fetch = FetchType.EAGER,orphanRemoval = true)
	@Cascade(CascadeType.ALL)
	private Document document;
	
	public Ad(){}

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	public LocalDateTime getPublicationDateTime() {
		return publicationDateTime;
	}
	
	public void setPublicationDateTime(LocalDateTime publicationDateTime) {
		this.publicationDateTime = publicationDateTime;
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
	
	public Integer getCost() {
		return cost;
	}

	public void setCost(Integer cost) {
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
	
	public AdStatus getStatus() {
		return status;
	}
	
	public void setStatus(AdStatus status) {
		this.status = status;
	}
	
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	

	public Set<RespondedAd> getRespondedAds() {
		return respondedAds;
	}

	public void setRespondedAds(Set<RespondedAd> respondedAds) {
		this.respondedAds = respondedAds;
	}

	public void addRespondedAd(RespondedAd respondedAd){
		respondedAds.add(respondedAd);
		respondedAd.setAd(this);
	}
	
	public void removeRespondedAd(RespondedAd respondedAd){
		respondedAds.remove(respondedAd);
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

	public LocalDateTime getInitialDateTime() {
		return initialDateTime;
	}

	public void setInitialDateTime(LocalDateTime initialDateTime) {
		this.initialDateTime = initialDateTime;
	}

	public LocalDateTime getFinishDateTime() {
		return finishDateTime;
	}

	public void setFinishDateTime(LocalDateTime finishDateTime) {
		this.finishDateTime = finishDateTime;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}
	
	public Document getDocument() {
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((client == null) ? 0 : client.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((initLanguage == null) ? 0 : initLanguage.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((resultLanguage == null) ? 0 : resultLanguage.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Ad other = (Ad) obj;
		if (client == null) {
			if (other.client != null)
				return false;
		} else if (!client.equals(other.client))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (initLanguage != other.initLanguage)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (resultLanguage != other.resultLanguage)
			return false;
		return true;
	}

}

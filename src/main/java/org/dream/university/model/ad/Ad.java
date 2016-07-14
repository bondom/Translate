package org.dream.university.model.ad;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.dream.university.model.Client;
import org.dream.university.model.Comment;

@Entity
@Table(name = "AD_TEST")
public class Ad  implements Serializable{
	
	/**
	 * Version of this class in production 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "AD_ID")
	private long id;
/*	
	@ManyToOne
	@JoinColumn(name = "CLIENT_ID",nullable = false)
	private Client client;*/
	
	@NotNull
	@Column(name = "AD_NAME", nullable = false)
	private String name;

	@NotNull
	@Column(name = "AD_DESC",nullable = false,length = 1000)
	@Lob
	@Size(min = 0, max = 1000)
	private String description;

	@Column(name = "AD_END_DATE",nullable = false)
	private LocalDate endDate;
	
	@Column(name = "AD_INIT_LANGUAGE",nullable = false)
	@Enumerated
	private Language initLanguage;
	
	@Column(name = "AD_RESULT_LANGUAGE",nullable = false)
	@Enumerated
	private Language resultLanguage;
	
	@Column(name = "AD_TRANSLATE_TYPE",nullable = false)
	@Enumerated
	private TranslateType translateType;
	
	@Lob
	@Column(name = "AD_FILE")
	private byte[] file;
	
	@Column(name = "AD_COST",nullable = false,precision = 2)
	private double cost;
	
	@Column(name = "AD_CURRENCY",nullable = false)
	@Enumerated
	private Currency currency;
	
	@Column(name = "AD_CREATING_DATE",nullable = false)
	private LocalDateTime creatingDate;
	
	@Column(name = "AD_STATUS",nullable = false)
	@Enumerated
	private AdStatus status;
	
	public Ad(){}

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}

	public LocalDateTime getCreatingDate() {
		return creatingDate;
	}

	public void setCreatingDate(LocalDateTime creatingDate) {
		this.creatingDate = creatingDate;
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

	
	/*public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}
*/
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

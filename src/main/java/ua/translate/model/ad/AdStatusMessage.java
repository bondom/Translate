package ua.translate.model.ad;

import java.util.LinkedHashSet;
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
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.validator.constraints.NotBlank;

import ua.translate.model.ad.Ad.TranslateType;
import ua.translate.model.status.AdStatus;

@Entity
@Table(name = "AD_STATUSES_WITH_MESSAGES")
public class AdStatusMessage {
	
	@Id
	@SequenceGenerator(name = "standart",initialValue = 1)
	@GeneratedValue(generator = "standart",strategy =GenerationType.SEQUENCE)
	@Column
	private long id;
	
	@Column(name = "AD_STATUS",nullable = false)
	@Enumerated(EnumType.STRING)
	@NotNull
	private AdStatus adStatus;
	
	@Column(name = "TRANSLATE_TYPE",nullable = false)
	@Enumerated(EnumType.STRING)
	@NotNull
	protected TranslateType translateType;
	
	
	@Column(nullable = false)
	@NotBlank
	private String messageForClient;
	
	@Column(nullable = false)
	@NotBlank
	private String messageForTranslator;
	
	public AdStatusMessage(){}
	public AdStatus getAdStatus() {
		return adStatus;
	}

	public void setAdStatus(AdStatus adStatus) {
		this.adStatus = adStatus;
	}

	public String getMessageForClient() {
		return messageForClient;
	}

	public void setMessageForClient(String messageForClient) {
		this.messageForClient = messageForClient;
	}

	public String getMessageForTranslator() {
		return messageForTranslator;
	}

	public void setMessageForTranslator(String messageForTranslator) {
		this.messageForTranslator = messageForTranslator;
	}
	
	
	public TranslateType getTranslateType() {
		return translateType;
	}
	public void setTranslateType(TranslateType translateType) {
		this.translateType = translateType;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((adStatus == null) ? 0 : adStatus.hashCode());
		result = prime * result + ((translateType == null) ? 0 : translateType.hashCode());
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
		AdStatusMessage other = (AdStatusMessage) obj;
		if (adStatus != other.adStatus)
			return false;
		if (translateType != other.translateType)
			return false;
		return true;
	}
	
	
	
	
}

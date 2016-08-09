package ua.translate.model.ad;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.springframework.stereotype.Component;

import ua.translate.model.Client;
import ua.translate.model.Translator;
import ua.translate.model.status.ResponsedAdStatus;

@Entity
@Table(name = "RESPONSED_AD_TEST")
@NamedQueries({
	@NamedQuery(name = "responsedAdsByAd",query="from ResponsedAd where ad = :ad")
})
@Component
public class ResponsedAd {
	
	@Id
	@SequenceGenerator(name = "standart",initialValue = 1)
	@GeneratedValue(generator = "standart",strategy =GenerationType.SEQUENCE)
	private long id;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@Cascade(CascadeType.REMOVE)
	@JoinColumn(name = "AD",nullable = false)
	private Ad ad;
	
	@Column(nullable = false)
	private LocalDateTime dateTimeOfResponse;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "CLIENT",nullable = false)
	private Client client;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "TRANSLATOR",nullable = false)
	private Translator translator;
	
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private ResponsedAdStatus status;
	
	
	public ResponsedAd(){}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setAd(Ad ad) {
		this.ad = ad;
	}

	public Ad getAd() {
		return ad;
	}

	public LocalDateTime getDateTimeOfResponse() {
		return dateTimeOfResponse;
	}

	public void setDateTimeOfResponse(LocalDateTime dateTimeOfResponse) {
		this.dateTimeOfResponse = dateTimeOfResponse;
	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public Translator getTranslator() {
		return translator;
	}

	public void setTranslator(Translator translator) {
		this.translator = translator;
	}
	
	public ResponsedAdStatus getStatus() {
		return status;
	}

	public void setStatus(ResponsedAdStatus status) {
		this.status = status;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ad == null) ? 0 : ad.hashCode());
		result = prime * result + ((client == null) ? 0 : client.hashCode());
		result = prime * result + ((dateTimeOfResponse == null) ? 0 : dateTimeOfResponse.hashCode());
		result = prime * result + ((translator == null) ? 0 : translator.hashCode());
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
		ResponsedAd other = (ResponsedAd) obj;
		if (ad == null) {
			if (other.ad != null)
				return false;
		} else if (!ad.equals(other.ad))
			return false;
		if (client == null) {
			if (other.client != null)
				return false;
		} else if (!client.equals(other.client))
			return false;
		if (dateTimeOfResponse == null) {
			if (other.dateTimeOfResponse != null)
				return false;
		} else if (!dateTimeOfResponse.equals(other.dateTimeOfResponse))
			return false;
		if (translator == null) {
			if (other.translator != null)
				return false;
		} else if (!translator.equals(other.translator))
			return false;
		return true;
	}

	

	
	

}

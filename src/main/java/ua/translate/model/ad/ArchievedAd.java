package ua.translate.model.ad;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import ua.translate.model.Client;
import ua.translate.model.Translator;

@Entity
@Table
public class ArchievedAd {
	
	@Id
	@SequenceGenerator(name = "standart",initialValue = 1)
	@GeneratedValue(generator = "standart",strategy =GenerationType.SEQUENCE)
	@Column
	private long id;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn
	private Client client;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn
	private Translator translator;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable = false)
	private Ad ad;
	
	@Column(nullable = false)
	private LocalDateTime creatingDateTime;
	
	
	
	public ArchievedAd(){
		this.creatingDateTime = LocalDateTime.now();
	}
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
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

	public Ad getAd() {
		return ad;
	}

	public void setAd(Ad ad) {
		this.ad = ad;
	}
	
	public LocalDateTime getCreatingDateTime() {
		return creatingDateTime;
	}
	public void setCreatingDateTime(LocalDateTime creatingDateTime) {
		this.creatingDateTime = creatingDateTime;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ad == null) ? 0 : ad.hashCode());
		result = prime * result + ((client == null) ? 0 : client.hashCode());
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
		ArchievedAd other = (ArchievedAd) obj;
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
		if (translator == null) {
			if (other.translator != null)
				return false;
		} else if (!translator.equals(other.translator))
			return false;
		return true;
	}
	
	
	
}

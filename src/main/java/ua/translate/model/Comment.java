package ua.translate.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.Size;


@Entity
@Table(name = "COMMENT_TEST")
public class Comment implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "standart",initialValue = 1)
	@GeneratedValue(generator = "standart",strategy =GenerationType.SEQUENCE)
	@Column(name = "COMMENT_ID")
	private long id;
	
	@Lob
	@Column(name = "COMMENT_TEXT",nullable = false)
	@Size(min = 7)
	private String text;
	
	@Column(name = "COMMENT_CLIENT_NAME",nullable = false)
	private String clientName;
	
	@ManyToOne
	@JoinColumn(name = "COMMENT_TRANSLATOR",nullable = false)
	private Translator translator;

	@Column(name = "COMMENT_CREATING_DATE",nullable = false)
	private LocalDateTime creatingDate;
	
	

	public Comment(){}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public Translator getTranslator() {
		return translator;
	}

	public void setTranslator(Translator translator) {
		this.translator = translator;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	
	public LocalDateTime getCreatingDate() {
		return creatingDate;
	}

	public void setCreatingDate(LocalDateTime creatingDate) {
		this.creatingDate = creatingDate;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((clientName == null) ? 0 : clientName.hashCode());
		result = prime * result + ((creatingDate == null) ? 0 : creatingDate.hashCode());
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
		Comment other = (Comment) obj;
		if (clientName == null) {
			if (other.clientName != null)
				return false;
		} else if (!clientName.equals(other.clientName))
			return false;
		if (creatingDate == null) {
			if (other.creatingDate != null)
				return false;
		} else if (!creatingDate.equals(other.creatingDate))
			return false;
		if (translator == null) {
			if (other.translator != null)
				return false;
		} else if (!translator.equals(other.translator))
			return false;
		return true;
	}

	
	
	
}

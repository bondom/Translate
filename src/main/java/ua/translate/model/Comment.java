package ua.translate.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "COMMENT_TEST")
public class Comment implements Serializable{
	
	/**
	 * Version of this class in production 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "COMMENT_ID")
	private long id;
	
	@Lob
	@Column(name = "COMMENT_TEXT",nullable = false)
	private String text;
	
	@Column(name = "COMMENT_CLIENT_NAME",nullable = false)
	private String clientName;
	
/*	@ManyToOne
	@JoinColumn(name = "TRANSLATOR_ID",nullable = false)
	private Translator translator;
*/
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

/*	public Translator getTranslator() {
		return translator;
	}

	public void setTranslator(Translator translator) {
		this.translator = translator;
	}
*/
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
	public boolean equals(Object obj){
		Comment comment = (Comment)obj;
		if(comment == null){
			return false;
		}
		return (this.getId() ==comment.getId());
	}
	
	@Override
	public int hashCode(){
		return Long.hashCode(id);
	}
	
	
}

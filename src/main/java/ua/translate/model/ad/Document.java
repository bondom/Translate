package ua.translate.model.ad;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import ua.translate.model.validator.FieldNotMatch;

@Entity
@Table(name = "DOCUMENT_TEST")
public class Document {

	
	@Id
	@SequenceGenerator(name = "standart",initialValue = 1)
	@GeneratedValue(generator = "standart",strategy =GenerationType.SEQUENCE)
	@Column(name = "DOCUMENT_ID")
	private long id;
	
	@Lob
	@Column(name = "DOCUMENT_FILE",nullable = false)
	private byte[] file;
	
	@Column(name = "DOCUMENT_FILE_NAME",nullable = false)
	private String fileName;
	
	@Column(name = "DOCUMENT_CONTENT_TYPE",nullable = false)
	private String contentType;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "DOCUMENT_AD_ID")
	private Ad ad;
	
	
	public Document(Ad ad,byte[] file, String fileName, String contentType) {
		super();
		this.ad = ad;
		this.file = file;
		this.fileName = fileName;
		this.contentType = contentType;
	}
	
	public Document(byte[] file, String fileName, String contentType) {
		super();
		this.file = file;
		this.fileName = fileName;
		this.contentType = contentType;
	}
	
	public Document(){}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public byte[] getFile() {
		return file;
	}

	public void setFile(byte[] file) {
		this.file = file;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	
	
	
	
}

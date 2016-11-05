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
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

@Entity
@Table(name = "INIT_DOCUMENT_TEST")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
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
	
	public Document(){}
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

	public Ad getAd() {
		return ad;
	}

	public void setAd(Ad ad) {
		this.ad = ad;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ad == null) ? 0 : ad.hashCode());
		result = prime * result + ((fileName == null) ? 0 : fileName.hashCode());
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
		Document other = (Document) obj;
		if (ad == null) {
			if (other.ad != null)
				return false;
		} else if (!ad.equals(other.ad))
			return false;
		if (fileName == null) {
			if (other.fileName != null)
				return false;
		} else if (!fileName.equals(other.fileName))
			return false;
		return true;
	}
	
	
	
	
	
	
	
	
	
}

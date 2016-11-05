package ua.translate.model.ad;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;



@Entity
@Table(name="WRITTEN_AD")
@PrimaryKeyJoinColumn(name= "written_ad_id")
public class WrittenAd extends Ad{
	
	private static final long serialVersionUID = 1L;

	@DateTimeFormat(iso = ISO.DATE,pattern = "dd.MM.yyyy")
	@Column(name = "AD_END_DATE",nullable = false)
	@NotNull
	private LocalDate endDate;
	
	@OneToOne(mappedBy = "ad",fetch = FetchType.EAGER,orphanRemoval = true)
	@Cascade(CascadeType.ALL)
	private Document document;
	
	@OneToOne(mappedBy = "ad",fetch = FetchType.EAGER,orphanRemoval = true)
	@Cascade(CascadeType.ALL)
	private ResultDocument resultDocument;

	public WrittenAd(){
		super();
		this.translateType = TranslateType.WRITTEN;
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

	public ResultDocument getResultDocument() {
		return resultDocument;
	}

	public void setResultDocument(ResultDocument resultDocument) {
		this.resultDocument = resultDocument;
	}
	
	
	
}

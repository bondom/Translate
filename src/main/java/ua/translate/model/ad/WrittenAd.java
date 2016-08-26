package ua.translate.model.ad;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

/*@Entity
@Table(name="WRITTEN_AD")
@PrimaryKeyJoinColumn(name= "written_ad_id")*/
public class WrittenAd extends Ad{
	
	@DateTimeFormat(iso = ISO.DATE,pattern = "dd.MM.yyyy")
	@Column(name = "AD_END_DATE",nullable = false)
	private LocalDate endDate;
	
	@Lob
	@Column(name = "AD_FILE")
	private byte[] file;

	public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}

	public byte[] getFile() {
		return file;
	}

	public void setFile(byte[] file) {
		this.file = file;
	}
	
}

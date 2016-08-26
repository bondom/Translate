package ua.translate.model.ad;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

/*@Entity
@Table(name="ORAL_AD")
@PrimaryKeyJoinColumn(name= "oral_ad_id")*/
public class OralAd extends Ad{

	@Column(name = "ORAL_AD_COUNTRY", nullable = false)
	private String country;
	
	@Column(name = "ORAL_AD_CITY", nullable = false)
	private String city;
	
	@DateTimeFormat(iso = ISO.DATE_TIME,pattern = "dd.MM.yyyy HH:mm:ss")
	@Column(name = "ORAL_AD_INIT_DATE",nullable = false)
	private LocalDateTime initialDateTime;
	
	@DateTimeFormat(iso = ISO.DATE_TIME,pattern = "dd.MM.yyyy HH:mm:ss")
	@Column(name = "ORAL_AD_FINISH_DATE",nullable = false)
	private LocalDateTime finishDateTime;

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public LocalDateTime getInitialDateTime() {
		return initialDateTime;
	}

	public void setInitialDateTime(LocalDateTime initialDateTime) {
		this.initialDateTime = initialDateTime;
	}

	public LocalDateTime getFinishDateTime() {
		return finishDateTime;
	}

	public void setFinishDateTime(LocalDateTime finishDateTime) {
		this.finishDateTime = finishDateTime;
	}
	
}

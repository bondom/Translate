package ua.translate.model;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import ua.translate.model.status.EmailStatus;

@Entity
@NamedQueries({
	@NamedQuery(name =  "userByConfirmationUrl",
				query = "from User user where user.confirmationUrl = :confirmationUrl")
})
@Table(name = "USER_TEST")
public class User extends UserEntity{
	
	@Lob
	@Column(name = "AVATAR")
	private byte[] avatar;
	
	@NotBlank
	@Column(name = "COUNTRY",nullable = false)
	private String country;
	
	@NotBlank
	@Column(name = "CITY",nullable = false)
	private String city;
	
	@DateTimeFormat(iso = ISO.DATE,pattern = "dd.MM.yyyy")
	 /*!!!!Добавить проверку валидности даты!!!!*/
	@NotNull
	@Column(name = "BIRTHDAY",nullable = false)
	private LocalDate birthday;
	
	@NotNull
	@Column(name = "PHONE_NUMBER", nullable = false)
	@Pattern(regexp = "^[+]380\\d{9}$")
	private String phoneNumber;
	
	@Column(name = "EMAIL_STATUS",nullable = false)
	@Enumerated(EnumType.STRING)
	private EmailStatus emailStatus;
	
	@Column(name = "CONFIRM_URL")
	private String confirmationUrl;
	
	@Column(name = "BALANCE",nullable = false)
	private Double balance;
	
	/**
	 * Sets {@link #emailStatus} to {@link EmailStatus#NOTCONFIRMED 
	 * EmailStatus.NOTCONFIRMED}  
	 */
	public User(){
		super();
		emailStatus = EmailStatus.NOTCONFIRMED;
		balance = 0.0;
	}

	public byte[] getAvatar() {
		return avatar;
	}

	public void setAvatar(byte[] avatar) {
		this.avatar = avatar;
	}

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

	public LocalDate getBirthday() {
		return birthday;
	}

	public void setBirthday(LocalDate birthday) {
		this.birthday = birthday;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public EmailStatus getEmailStatus() {
		return emailStatus;
	}

	public void setEmailStatus(EmailStatus emailStatus) {
		this.emailStatus = emailStatus;
	}

	public String getConfirmationUrl() {
		return confirmationUrl;
	}

	public void setConfirmationUrl(String confirmationUrl) {
		this.confirmationUrl = confirmationUrl;
	}

	public Double getBalance() {
		return balance;
	}

	public void setBalance(Double balance) {
		this.balance = balance;
	}
	
	
	
}

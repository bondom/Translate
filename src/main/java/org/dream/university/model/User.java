package org.dream.university.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.stereotype.Component;

@Entity
@NamedQueries({
		@NamedQuery(name =  "userByEmail",
					query = "from User user where user.email = :email")
})
@Table(name = "USER_TEST")
@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
@Component
public class User implements Serializable{
	
	/**
	 * Version of this class in production 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "standart",initialValue = 1)
	@GeneratedValue(generator = "standart")
	@Column(name = "ID")
	private long id;
	
	@Email
	@NotEmpty
	@Column(name = "EMAIL",nullable =false,unique = true)
	private String email;
	
	@Size(min = 4, max = 100)
	@Column(name = "PASSWORD",nullable = false)
	private String password;

	@Lob
	@Column(name = "AVATAR")
	private byte[] avatar;
	
	@Size(min = 3, max = 15)
	@Column(name = "FIRST_NAME",nullable = false)
	private String firstName;
	
	@Size(min = 3, max = 15)
	@Column(name = "LAST_NAME",nullable = false)
	private String lastName;
	
	@NotNull
	@Column(name = "PHONE_NUMBER", nullable = false)
	@Pattern(regexp = "^[+]380\\d{9}$")
	private String phoneNumber;
	
	@DateTimeFormat(iso = ISO.DATE,pattern = "dd.MM.yyyy")
	@NotNull
	@Column(name = "BIRTHDAY",nullable = false)
	private LocalDate birthday;

	@NotNull
	@Column(name = "COUNTRY",nullable = false)
	private String country;
	
	@NotNull
	@Column(name = "CITY",nullable = false)
	private String city;
	
	@Column(name = "ROLE", nullable = false)
	@Enumerated(EnumType.STRING)
	private UserRole role;

	@Column(name = "STATUS",nullable = false)
	@Enumerated(EnumType.STRING)
	private UserStatus status;
	
	@Column(name = "REGISTRATION_DATE",nullable = false)
	private LocalDateTime registrationTime;
	
	public User(){}
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}

	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	public UserStatus getStatus() {
		return status;
	}
	public void setStatus(UserStatus status) {
		this.status = status;
	}

	public UserRole getRole() {
		return role;
	}

	public void setRole(UserRole role) {
		this.role = role;
	}

	public byte[] getAvatar() {
		return avatar;
	}

	public void setAvatar(byte[] avatar) {
		this.avatar = avatar;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public LocalDate getBirthday() {
		return birthday;
	}

	public void setBirthday(LocalDate birthday) {
		this.birthday = birthday;
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
	
	public LocalDateTime getRegistrationTime() {
		return registrationTime;
	}

	public void setRegistrationTime(LocalDateTime registrationTime) {
		this.registrationTime = registrationTime;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}


	@Override
	public boolean equals(Object obj){
		User user = (User)obj;
		if(user == null){
			return false;
		}
		return (this.getId() ==user.getId());
	}
	
	@Override
	public int hashCode(){
		return Long.hashCode(id);
	}
		
	
}

package org.dream.university.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.stereotype.Component;

@Entity
@NamedQueries({
		@NamedQuery(name =  "userByLoginAndPassword", 
					query = "from User user where user.login = :login and user.password = :password"),
		@NamedQuery(name =  "userByLogin",
					query = "from User user where user.login = :login"),
		@NamedQuery(name =  "userByEmail",
					query = "from User user where user.email = :email")
})
@Table(name = "USER_UPDATED_SITE")
@Component
public class User implements Serializable{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "USER_ID")
	private int id;
	
	@Size(min = 4,max = 15)
	@Column(name = "USER_LOGIN")
	private String login;
	
	@Size(min = 6, max = 20)
	@Column(name = "USER_PASSWORD")
	private String password;

	@Lob
	@Column(name = "USER_IMAGE")
	private byte[] image;
	
	@Email
	@NotEmpty
	@Column(name = "USER_EMAIL")
	private String email;
	
	
	@Column(name = "USER_ROLE")
	private String role;

	@Enumerated(EnumType.STRING)
	@Column(name = "USER_STATUS")
	private UserStatus status;

	@Size(min = 3, max = 15)
	@Column(name = "NAME")
	private String name;
	
	@Size(min = 3, max = 15)
	@Column(name = "SURNAME")
	private String surname;
	
	@Column(name = "PHONE_NUMBER")
	private String phoneNumber;
	
	public User(){}
	public User(int id,String login,String email,String password){
		this.id = id;
		this.login = login;
		this.email= email;
		this.password = password;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
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
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	
	public byte[] getImage() {
		return image;
	}
	public void setImage(byte[] image) {
		this.image = image;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSurname() {
		return surname;
	}
	public void setSurname(String surname) {
		this.surname = surname;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	@Override
	public boolean equals(Object obj){
		User user = (User)obj;
		return ((this.getId() ==user.getId())&&
				(this.getLogin() == user.getLogin()));
	}
	
		
	
}

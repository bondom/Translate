package org.dream.university.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
					query = "from User user where user.userLogin = :userLogin and user.userPassword = :userPassword"),
		@NamedQuery(name =  "userByLogin",
					query = "from User user where user.userLogin = :userLogin"),
		@NamedQuery(name =  "userByEmail",
					query = "from User user where user.userEmail = :userEmail")
})
@Table(name = "USER_UNIVERSITY_SITE")
@Component
public class User implements Serializable{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "USER_ID")
	private int id;
	
	@Size(min = 4,max = 15)
	@Column(name = "USER_LOGIN")
	private String userLogin;
	
	@Size(min = 4,max = 15)
	@Column(name = "USER_PASSWORD")
	private String userPassword;

	@Email
	@NotEmpty
	@Column(name = "USER_EMAIL")
	private String userEmail;
	
	
	@Column(name = "ROLE")
	private String role;

	@Enumerated(EnumType.STRING)
	private UserStatus userStatus;

	public User(){}
	public User(int id,String userLogin,String userEmail,String userPassword){
		this.id = id;
		this.userLogin = userLogin;
		this.userPassword = userPassword;
		this.userEmail = userEmail;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUserEmail() {
		return userEmail;
	}
	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}
	public String getUserLogin() {
		return userLogin;
	}
	public void setUserLogin(String userLogin) {
		this.userLogin = userLogin;
	}
	public String getUserPassword() {
		return userPassword;
	}
	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}
	
	public UserStatus getUserStatus() {
		return userStatus;
	}
	public void setUserStatus(UserStatus userStatus) {
		this.userStatus = userStatus;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	
	@Override
	public boolean equals(Object obj){
		User user = (User)obj;
		return ((this.getId() ==user.getId())&&
				(this.getUserLogin() == user.getUserLogin()));
	}
	
		
	
}

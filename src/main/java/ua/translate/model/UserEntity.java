package ua.translate.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

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
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.stereotype.Component;

import ua.translate.model.status.EmailStatus;
import ua.translate.model.status.UserStatus;

@Entity
@NamedQueries({
		@NamedQuery(name =  "userByEmail",
					query = "from UserEntity user where user.email = :email"),
})
@Table(name = "USER_ENTITY_TEST")
@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
public class UserEntity implements Serializable{
	
	/**
	 * Version of this class in production 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "standart",initialValue = 1)
	@GeneratedValue(generator = "standart",strategy =GenerationType.SEQUENCE)
	@Column(name = "ID")
	private long id;
	
	@Email
	/*!!!!Can be replaced with required=false and then need delete not needed hidden input!!!!*/
	@NotEmpty
	@Column(name = "EMAIL",nullable =false,unique = true)
	private String email;
	
	@Size(min = 4, max = 100)
	@Column(name = "PASSWORD",nullable = false)
	private String password;

	
	@Size(min = 3, max = 15)
	@Column(name = "FIRST_NAME",nullable = false)
	private String firstName;
	
	@Size(min = 3, max = 15)
	@Column(name = "LAST_NAME",nullable = false)
	private String lastName;
	
	@Column(name = "ROLE", nullable = false)
	@Enumerated(EnumType.STRING)
	protected UserRole role;

	public enum UserRole{
		ROLE_CLIENT,ROLE_TRANSLATOR,ROLE_ADMIN
	}
	@Column(name = "STATUS",nullable = false)
	@Enumerated(EnumType.STRING)
	private UserStatus status;
	
	
	@Column(name = "REGISTRATION_DATE",nullable = false)
	private LocalDateTime registrationTime;
	
	/**
	 * Sets {@link #status} to 
	 * {@link UserStatus#ACTIVE UserStatus.ACTIVE} 
	 * and {@link #registrationTime} to {@link LocalDateTime#now() LocalDateTime.now}
	 */
	public UserEntity(){
		status = UserStatus.ACTIVE;
		registrationTime = LocalDateTime.now();
	}
	
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

	public LocalDateTime getRegistrationTime() {
		return registrationTime;
	}
	
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	@Override
	public boolean equals(Object obj){
		if ( obj == null || getClass() != obj.getClass() ) {
            return false;
        }
		UserEntity user = (UserEntity)obj;
		return Objects.equals(email, user.getEmail());
	}
	
	@Override
	public int hashCode(){
		return Objects.hash(email);
	}
		
	
}

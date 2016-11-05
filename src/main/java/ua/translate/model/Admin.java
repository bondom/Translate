package ua.translate.model;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "ADMIN_TEST")
public class Admin extends UserEntity {

	public Admin(){
		super();
		role = UserRole.ROLE_ADMIN;
	}
}

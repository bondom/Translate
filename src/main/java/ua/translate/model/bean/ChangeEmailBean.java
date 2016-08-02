package ua.translate.model.bean;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import ua.translate.model.validator.FieldMatch;

@FieldMatch(first = "newEmail", message = "E-mails must match", second = "newEmailAgain")
public class ChangeEmailBean {
	
	@Email
	@NotEmpty
	private String newEmail;

	@Email
	@NotEmpty
	private String newEmailAgain;
	
	@NotEmpty
	private String currentPassword;
	
	public String getCurrentPassword() {
		return currentPassword;
	}
	public void setCurrentPassword(String currentPassword) {
		this.currentPassword = currentPassword;
	}
	
	public String getNewEmailAgain() {
		return newEmailAgain;
	}
	public void setNewEmailAgain(String newEmailAgain) {
		this.newEmailAgain = newEmailAgain;
	}
	public String getNewEmail() {
		return newEmail;
	}
	public void setNewEmail(String newEmail) {
		this.newEmail = newEmail;
	}
	
	
}

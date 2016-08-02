package ua.translate.model.bean;

import org.hibernate.validator.constraints.NotEmpty;

import ua.translate.model.validator.FieldMatch;

@FieldMatch(first = "newPassword", message = "Passwords must match", second = "newPasswordAgain")
public class ChangePasswordBean {
	
	@NotEmpty
	private String oldPassword;
	@NotEmpty
	private String newPassword;
	@NotEmpty
	private String newPasswordAgain;
	
	public String getOldPassword() {
		return oldPassword;
	}
	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}
	public String getNewPassword() {
		return newPassword;
	}
	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}
	public String getNewPasswordAgain() {
		return newPasswordAgain;
	}
	public void setNewPasswordAgain(String newPasswordAgain) {
		this.newPasswordAgain = newPasswordAgain;
	}
	
	
	
}

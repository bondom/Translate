package ua.translate.service.exception;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class NotConfirmedEmailException extends UsernameNotFoundException{

	public NotConfirmedEmailException(String msg) {
		super(msg);
	}

}

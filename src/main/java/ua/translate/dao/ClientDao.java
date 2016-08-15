package ua.translate.dao;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;

import ua.translate.model.Client;

public abstract class ClientDao extends AbstractUserDao<Long, Client> {
	public abstract Client getClientByEmail(String email);
}

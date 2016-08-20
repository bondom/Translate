package ua.translate.dao;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;

import ua.translate.model.Client;

public abstract class ClientDao extends AbstractUserDao<Long, Client> {
	
	/**
	 * Gets {@link Client} {@code client} from data storage by {@code email}
	 * @param email - email of client
	 * @return {@code Client} object or {@code null}, if client with such email doesn't
	 * exist in data storage
	 */
	public abstract Client getClientByEmail(String email);
}

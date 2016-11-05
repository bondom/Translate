package ua.translate.dao;

import java.io.Serializable;

import org.hibernate.Session;
import org.hibernate.exception.ConstraintViolationException;

public interface AbstractDao<PK extends Serializable,T> {
	
	public PK save (T t);
	public T get(PK id);
	public void delete(T entity);
	public T update(T t);
	public void flush() throws ConstraintViolationException;
	public void clear();
}

package org.dream.university.dao;


public interface GenericDAO<T> {
	public void create(T t);
	public T get(int id);
	public boolean delete(int id);
	public T update(T t);
	
}

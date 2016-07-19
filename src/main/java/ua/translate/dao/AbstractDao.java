package ua.translate.dao;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractDao<PK extends Serializable,T> {
	
	@Autowired
	SessionFactory sessionFactory;
	
	private final Class<T> persistentClass;
	
	@SuppressWarnings("unchecked")
	public AbstractDao(){
		this.persistentClass =(Class<T>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[1];
	}
	public PK save(T t){
		Session session = sessionFactory.getCurrentSession();
		return (PK)session.save(t);
	}
	
	public T get(PK id){
		Session session = sessionFactory.getCurrentSession();
		T user = session.get(persistentClass, id);
		return user;
	}
	public void delete(T entity){
		Session session = sessionFactory.getCurrentSession();
		session.delete(entity);
	}
	public T update(T t){
		Session session = sessionFactory.getCurrentSession();
		session.update(t);
		return t;
	}
	
	protected Criteria createEntityCriteria(){
        return sessionFactory.getCurrentSession().createCriteria(persistentClass);
    }
	
}

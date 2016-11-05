package ua.translate.dao.impl;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import ua.translate.dao.SettingsDao;
import ua.translate.model.settings.Settings;

@Repository
public class SettingsDaoImpl implements SettingsDao {
	
	@Autowired
	SessionFactory sessionFactory;
	
	@Override
	public Integer save(Settings t){
		Session session = sessionFactory.getCurrentSession();
		return (Integer) session.save(t);
	}

	@Override
	public Settings get(Integer id) {
		Session session = sessionFactory.getCurrentSession();
		Settings client = session.get(Settings.class, id);
		return client;
	}

	@Override
	public void delete(Settings entity) {
		Session session = sessionFactory.getCurrentSession();
		session.delete(entity);
		
	}

	@Override
	public Settings update(Settings t) {
		Session session = sessionFactory.getCurrentSession();
		session.update(t);
		return t;
	}
	
	public void flush() throws ConstraintViolationException{
		Session session = sessionFactory.getCurrentSession();
		session.flush();
	}

	
	@Override
	public void clear() {
		Session session = sessionFactory.getCurrentSession();
		session.clear();
	}

}

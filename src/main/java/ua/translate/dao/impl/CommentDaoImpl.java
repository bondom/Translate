package ua.translate.dao.impl;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import ua.translate.dao.CommentDao;
import ua.translate.model.Comment;
import ua.translate.model.ad.Ad;

@Repository
public class CommentDaoImpl implements CommentDao{

	@Autowired
	SessionFactory sessionFactory;

	
	@Override
	public Long save(Comment t) {
		Session session = sessionFactory.getCurrentSession();
		return (Long) session.save(t);
	}

	@Override
	public Comment get(Long id) {
		Session session = sessionFactory.getCurrentSession();
		Comment comment = session.get(Comment.class, id);
		return comment;
	}

	@Override
	public void delete(Comment entity) {
		Session session = sessionFactory.getCurrentSession();
		session.delete(entity);
	}

	@Override
	public Comment update(Comment t) {
		Session session = sessionFactory.getCurrentSession();
		session.update(t);
		return t;
	}
	
	@Override
	public void clear() {
		Session session = sessionFactory.getCurrentSession();
		session.clear();
	}

	@Override
	public void flush() throws ConstraintViolationException {
		Session session = sessionFactory.getCurrentSession();
		session.flush();
	}

	

}

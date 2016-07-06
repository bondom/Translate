package org.dream.university.dao;

import java.util.List;

import org.dream.university.model.Student;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class StudentDAOImpl implements StudentDAO {
	
	@Autowired
	SessionFactory sessionFactory;
	
	public StudentDAOImpl(){}
	
	@Override
	public List<Student> getStudentsByGroup(Student student) {
		// TODO Auto-generated method stub
		Session session = sessionFactory.getCurrentSession();
		Query query =  session.getNamedQuery("getStudentsOfGroup");
		query.setParameter("group", student.getStudentGroup());
		List<Student> studentsOfGroup= query.list();
		return studentsOfGroup;
	}
	
	@Override
	public void create(Student student) {
		Session session = sessionFactory.getCurrentSession();
		session.persist(student);
	}


	@Override
	public Student get(int id) {
		Session session = sessionFactory.getCurrentSession();
		Student student = session.get(Student.class, id);
		return student;
	}

	@Override
	public boolean delete(int id) {
		Student student = this.get(id);
		Session session = sessionFactory.getCurrentSession();
		if(student!=null){
			//Student with the same id exists in DB table
			session.delete(student);
			return true;
		}else return false;
	}


	@Override
	public Student update(Student student) {
		Session session = sessionFactory.getCurrentSession();
		session.update(student);
		return student;
	}
	
	

}

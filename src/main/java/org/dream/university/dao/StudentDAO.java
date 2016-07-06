package org.dream.university.dao;

import java.util.List;

import org.dream.university.model.Student;

public interface StudentDAO extends GenericDAO<Student>{
	public List<Student> getStudentsByGroup(Student student);
}	

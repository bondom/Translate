package org.dream.university.service;

import java.util.List;

import org.dream.university.dao.StudentDAO;
import org.dream.university.model.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.ModelAndView;

@Service("studentService")
@Transactional(readOnly = true)
public class StudentServiceImpl implements StudentService {

	@Autowired
	private StudentDAO studentDao;
	
	public StudentServiceImpl(){}
	
	@Override
	public List<Student> getStudentOfGroup(Student student) {
		// TODO Auto-generated method stub
		List<Student> studentsOfGroup= studentDao.getStudentsByGroup(student);
		return studentsOfGroup;
	}

}

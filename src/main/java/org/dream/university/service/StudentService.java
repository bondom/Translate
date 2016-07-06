package org.dream.university.service;

import java.util.List;

import org.dream.university.model.Student;
import org.springframework.web.servlet.ModelAndView;

public interface StudentService {
	public List<Student> getStudentOfGroup(Student student);
}

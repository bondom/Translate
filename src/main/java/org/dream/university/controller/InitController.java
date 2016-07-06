package org.dream.university.controller;

import java.util.List;

import javax.validation.Valid;

import org.dream.university.editors.StudentNameEditor;
import org.dream.university.model.Student;
import org.dream.university.model.User;
import org.dream.university.service.StudentService;
import org.dream.university.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;


@Controller
@RequestMapping(value="/")
public class InitController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private StudentService studentService;
	
	@InitBinder
	public void initBinder(WebDataBinder dataBinder){
		dataBinder.registerCustomEditor(String.class, "studentGroup", new StudentNameEditor());
	}
	
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public ModelAndView getLoginForm(){
		return new ModelAndView("login");
	}
	
	@RequestMapping(value = "/registration",method = RequestMethod.GET)
	public ModelAndView getRegistrationForm(){
		ModelAndView model = new ModelAndView("registration");
		User user = new User();
		model.addObject("user", user);
		return model;
	}
	
	@RequestMapping(value = "/students",method = RequestMethod.GET)
	public ModelAndView getGroupForm(){
		ModelAndView model = new ModelAndView("searchStudents");
		Student student = new Student();
		model.addObject("student", student);
		return model;
	}
	
	
	@RequestMapping(value = "/getStudents",method = RequestMethod.POST)
	public ModelAndView getStudents(@Valid @ModelAttribute("student") Student student,
								BindingResult result){
		if(result.hasErrors()){
			return new ModelAndView("searchStudents");
		}
		List<Student> students = studentService.getStudentOfGroup(student);
		if(!students.isEmpty()){
			ModelAndView studentsTableView = new ModelAndView("searchStudents");
			studentsTableView.addObject("studentsList", students);
			studentsTableView.addObject("groupName", student.getStudentGroup());
			return studentsTableView;
		}else{
			ModelAndView messageErrorView = new ModelAndView("searchStudents");
			messageErrorView.addObject("studentsList", students);
			return messageErrorView;
		}
		
	}
	
	@RequestMapping(value = "/registrationConfirm",method = RequestMethod.POST)
	public ModelAndView registration(@Valid @ModelAttribute("user") User user,
								BindingResult result){
		if(result.hasErrors()){
			return new ModelAndView("registration");
		}
		if(userService.registerUser(user)){
			ModelAndView loginView = new ModelAndView("login");
			loginView.addObject("resultRegistration", 
									"Success registration!");
			return loginView;
		}else{
			//Registration failed
			ModelAndView registrationView = new ModelAndView("registration");
			registrationView.addObject("resultRegistration", 
									"User with the same login or password "
									+ "is registered in system already");
			return registrationView;
		}
	}
}

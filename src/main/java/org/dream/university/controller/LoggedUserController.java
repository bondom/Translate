package org.dream.university.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.List;

import javax.validation.Valid;

import org.apache.commons.codec.binary.Base64;
import org.dream.university.model.Student;
import org.dream.university.model.User;
import org.dream.university.service.StudentService;
import org.dream.university.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/personal")
public class LoggedUserController {

	@Autowired
	private UserService userService;
	
	@Autowired
	private StudentService studentService;
	
	@RequestMapping(value = "/profile", method = RequestMethod.GET)
	public ModelAndView profile(){
		return new ModelAndView("/personal/profile");
	}
	
	@RequestMapping(value = "/profileInfo", method = RequestMethod.GET)
	public ModelAndView info(Principal user) throws UnsupportedEncodingException{
		User authenticatedUser = userService.getUser(user.getName());
		ModelAndView model = new ModelAndView("/personal/profile");
		model.addObject("authenticatedUser", authenticatedUser);
		if(authenticatedUser.getImage() == null){
			System.out.println("OOPS");
		}else{
			model.addObject("image", encoding64Image(authenticatedUser.getImage()));
		}
		return model;
	}
	
	@RequestMapping(value = "/settings",method = RequestMethod.GET)
	public ModelAndView settings(){
		return new ModelAndView("/personal/settings");
	}
	@RequestMapping(value = "/save",method = RequestMethod.POST)
	public ModelAndView saveSettings(Principal user, 
						@RequestParam("file") MultipartFile file){
		if(!file.isEmpty()){
			try {
				byte[] image = file.getBytes();
				User updatedUser = userService.update(user.getName(), image);
				ModelAndView model = new ModelAndView("/personal/profile");
				model.addObject("authenticatedUser", updatedUser);
				model.addObject("image", encoding64Image(updatedUser.getImage()));
				return model;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				/**
				 * Заглушка, поменять!!
				 */
				return new ModelAndView("/personal/exception");
			}
		}else{
			return new ModelAndView("/personal/settings");
		}
		
	}
	
	@RequestMapping(value = "/search",method = RequestMethod.GET)
	public ModelAndView searchPage(){
		ModelAndView model = new ModelAndView("/personal/searchPage");
		Student student = new Student();
		model.addObject("student", student);
		return model;
	}
	
	@RequestMapping(value = "/students",method = RequestMethod.POST)
	public ModelAndView getStudents(@Valid @ModelAttribute("student") Student student,
								BindingResult result){
		if(result.hasErrors()){
			return new ModelAndView("/personal/searchPage");
		}
		List<Student> students = studentService.getStudentOfGroup(student);
		if(!students.isEmpty()){
			ModelAndView studentsTableView = new ModelAndView("/personal/searchPage");
			studentsTableView.addObject("studentsList", students);
			studentsTableView.addObject("groupName", student.getStudentGroup());
			return studentsTableView;
		}else{
			ModelAndView messageErrorView = new ModelAndView("/personal/searchPage");
			messageErrorView.addObject("studentsList", students);
			return messageErrorView;
		}
		
	}
	
	public String encoding64Image(byte[] image) throws UnsupportedEncodingException{
		byte[] encodeBase64 = Base64.encodeBase64(image); 
		String base64Encoded = new String(encodeBase64,"UTF-8");
		return base64Encoded;
	}
	
}

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
	
	/**
	 * Returns ModelAndView object with user's profile view
	 * 
	 * @param user
	 * @return user's profile
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/profile", method = RequestMethod.GET)
	public ModelAndView profile(Principal user) throws UnsupportedEncodingException{
		User userFromDB = userService.getUserByLogin(user.getName());
		ModelAndView model = new ModelAndView("/personal/profile");
		model.addObject("user", userFromDB);
		if(userFromDB.getImage() == null){
			System.out.println("OOPS");
			/**
			 * Поменять!!!
			 */
		}else{
			model.addObject("image", convertImageForRendering(userFromDB.getImage()));
		}
		return model;
	}
	
	/**
	 * @param user
	 * @return page for editing a user's profile
	 */
	@RequestMapping(value = "/edit",method = RequestMethod.GET)
	public ModelAndView editPage(Principal user){
		User userFromDB = userService.getUserByLogin(user.getName());
		ModelAndView model = new ModelAndView("/personal/edit");
		model.addObject("user", userFromDB);
		return model;
	}
	
	/**
	 * Checks if new email exist and
	 * if some errors exist returns initial page(for editing user's profile)
	 * 
	 * If no errors exist updates the user and returns user's profile page
	 * @param newUser - {User.class} object with changes made by user
	 * @param result
	 * @param user - {Principal.class} object with name equals to {User.class} object's login
	 * @return
	 */
	@RequestMapping(value = "/saveEdits",method = RequestMethod.POST)
	public ModelAndView saveEdits(
			@Valid @ModelAttribute("user") User newUser,
			BindingResult result,
			Principal user){
		if(result.hasErrors()){
			return new ModelAndView("/personal/edit");
		}else if(!emailIsUnique(newUser.getEmail())){
			ModelAndView model = new ModelAndView("/personal/edit");
			model.addObject("emailExists","Such email is registered in system already");
			return model;
		}else{
			//All data entered by user is valid and we can save refreshed user's profile
			User userFromDB = userService.getUserByLogin(user.getName());
			User refreshedUser = userService.editUserProfile(userFromDB, newUser);
			ModelAndView browseRefreshedProfile = new ModelAndView("/personal/profile");
			browseRefreshedProfile.addObject("user", refreshedUser);
			return browseRefreshedProfile;
		}
	}
	
	/**
	 * Saves new avatar in DB
	 * 
	 * @param user - {Principal.class} object with name equals to {User.class} object's login
	 * @param file - file chosen to be a avatar
	 * @return user's profile page or page for editing profile if no file is chosen
	 * or file is not image
	 */
	@RequestMapping(value = "/saveAvatar",method = RequestMethod.POST)
	public ModelAndView saveAvatar(Principal user, 
						@RequestParam("file") MultipartFile file){
		if(!file.isEmpty()){
			try {
				String contentType = file.getContentType();
				if(contentType.startsWith("image/")){
					byte[] avatar = file.getBytes();
					User updatedUser = userService.updateAvatar(user.getName(), avatar);
					ModelAndView model = new ModelAndView("/personal/profile");
					model.addObject("user", updatedUser);
					model.addObject("image", convertImageForRendering(updatedUser.getImage()));
					return model;
				}else{
					User userFromDB = userService.getUserByLogin(user.getName());
					ModelAndView model = new ModelAndView("/personal/edit");
					model.addObject("user", userFromDB);
					model.addObject("wrongFile", "Please, choose image.");
					return model;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				/**
				 * Заглушка, поменять!!
				 */
				return new ModelAndView("/personal/exception");
			}
		}else{
			ModelAndView model = new ModelAndView("/personal/edit");
			User userFromDB = userService.getUserByLogin(user.getName());
			model.addObject("user", userFromDB);
			return model;
		}
		
	}
	
	private String convertImageForRendering(byte[] image) throws UnsupportedEncodingException{
		byte[] encodeBase64 = Base64.encodeBase64(image); 
		String base64Encoded = new String(encodeBase64,"UTF-8");
		return base64Encoded;
	}
	
	private boolean emailIsUnique(String email){
		User userFromDB = userService.getUserByEmail(email);
		if(userFromDB==null){
			//such email doesn't exist
			return true;
		}else{
			//user with the same email is registered in system already
			return false;
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
	
	
}

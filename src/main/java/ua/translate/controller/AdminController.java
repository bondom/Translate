package ua.translate.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/admin")
public class AdminController {

	@RequestMapping(value = "/adminPage", method = RequestMethod.GET)
	public ModelAndView getAdminPage(){
		return new ModelAndView("adminPage");
	}
	
}

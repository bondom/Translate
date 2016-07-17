package org.dream.university.controller;


import java.text.SimpleDateFormat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.dream.university.model.User;
import org.dream.university.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;


@Controller
@RequestMapping(value="/")
public class InitController {

	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public ModelAndView index(){
		return new ModelAndView("index");
	}
	
	@RequestMapping(value = "/bulbular", method = RequestMethod.GET)
	public ModelAndView getAdminForm(){
		return new ModelAndView("bulbular");
	}
	
	
}

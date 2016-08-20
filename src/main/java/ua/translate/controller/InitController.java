package ua.translate.controller;



import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value="/")
public class InitController {

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView start(){
		return new ModelAndView("index");
	}
	
	@RequestMapping(value = "/404",method = RequestMethod.GET)
	public ModelAndView notFound(){
		return new ModelAndView("/exception/404");
	}
	
	
}

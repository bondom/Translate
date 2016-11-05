package ua.translate.controller.client;

import java.security.Principal;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ua.translate.model.Comment;
import ua.translate.service.CommentService;

/**
 * Contains handler methods for executing some actions,
 * related to {@link Comment}s, such as adding
 * 
 * @author Yuriy Phediv
 *
 */
@Controller
@RequestMapping("/client")
public class CommentClientController {
	
	@Autowired
	private CommentService commentService;
	
	/**
	 * Attempts to add comment to {@link Translator} with id={@code translatorId}
	 * Redirects to profile of this translator.
	 */
	@RequestMapping(value = "/addComment",method = RequestMethod.POST)
	public ModelAndView addComment(@Valid @ModelAttribute("comment") Comment comment,
									BindingResult result,
									@RequestParam(name = "translatorId",required=true) long translatorId,
									Principal user,
									RedirectAttributes attr){
		if(result.hasErrors()){
			attr.addFlashAttribute("comment", comment);
			attr.addFlashAttribute(
					"org.springframework.validation.BindingResult.comment", result);
			return new ModelAndView(
					"redirect:/translators/"+translatorId);
		}
		commentService.save(comment,user.getName(),translatorId);
		return new ModelAndView(
				"redirect:/translators/"+translatorId);
	}
}

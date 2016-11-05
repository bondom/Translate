package ua.translate.controller.translator;

import java.security.Principal;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

import ua.translate.model.Translator;
import ua.translate.model.viewbean.FundsBean;
import ua.translate.service.BalanceService;
import ua.translate.service.TranslatorService;
import ua.translate.service.exception.InsufficientFunds;
import ua.translate.service.exception.InvalidPositiveNumber;
import ua.translate.service.exception.TemporarilyUnavailableAccessException;

@Controller
@RequestMapping("/translator/balance")
public class TranslatorBalanceController {
	
	@Autowired
	private TranslatorService translatorService;
	
	@Autowired
	private BalanceService balanceService;
	
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView balance(Principal user,
								HttpSession session){
		Translator translator = translatorService.getTranslatorByEmail(user.getName());
		ModelAndView model = new ModelAndView("/translator/balance/init");
		model.addObject("balance",translator.getBalance());
		return model;
	}
	
	@RequestMapping(value = "/withdrawp",method = RequestMethod.GET)
	public ModelAndView getWithdrawPage(Principal user,HttpServletRequest request){
		Translator translator = translatorService.getTranslatorByEmail(user.getName());
		ModelAndView model = new ModelAndView("/translator/balance/withdrawPage");
		model.addObject("balance",translator.getBalance());
		Map<String, ?> inputFlashMap = RequestContextUtils.getInputFlashMap(request);
	    if (inputFlashMap == null || !inputFlashMap.containsKey("fundsBean")) {
	    	model.addObject("fundsBean", new FundsBean());
	    }
		return model;
	}
	
	
	@RequestMapping(value = "/withdraw",method = RequestMethod.POST)
	public ModelAndView withdraw(Principal user,
									@Valid @ModelAttribute("fundsBean") FundsBean fundsBean,
									BindingResult result,
									RedirectAttributes attr){
		if(result.hasErrors()){
			attr.addFlashAttribute("org.springframework.validation.BindingResult.fundsBean", 
					result);
			attr.addFlashAttribute("fundsBean", fundsBean);
			return new ModelAndView("redirect:/translator/balance/withdrawp");
		}
		try {
			balanceService.withdrawMoney(user.getName(), fundsBean.getFunds());
		} catch (InvalidPositiveNumber e) {
			attr.addFlashAttribute("error","Amount must be more than 0");
			return new ModelAndView("redirect:/translator/balance/withdrawp");
		} catch (InsufficientFunds e) {
			attr.addFlashAttribute("error","You hasn't sufficient funds for this operation");
			return new ModelAndView("redirect:/translator/balance/withdrawp");
		} catch (TemporarilyUnavailableAccessException e) {
			attr.addFlashAttribute("error","You can't withdraw money, when you are executing"
					+ " translate.");
			return new ModelAndView("redirect:/translator/balance/withdrawp");
		}
		attr.addFlashAttribute("success","Withdrawing is successful");
		return new ModelAndView("redirect:/translator/balance/withdrawp");
	}
}

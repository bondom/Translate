package ua.translate.controller.client;

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

import ua.translate.model.Client;
import ua.translate.model.viewbean.FundsBean;
import ua.translate.service.BalanceService;
import ua.translate.service.ClientService;
import ua.translate.service.exception.InsufficientFunds;
import ua.translate.service.exception.InvalidPositiveNumber;
import ua.translate.service.exception.TemporarilyUnavailableAccessException;

@Controller
@RequestMapping("/client/balance")
public class BalanceClientController {
	
	@Autowired
	private ClientService clientService;
	
	@Autowired
	private BalanceService balanceService;
	
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView balance(Principal user,
								HttpSession session){
		Client client = clientService.getClientByEmail(user.getName());
		ModelAndView model = new ModelAndView("/client/balance/init");
		model.addObject("balance",client.getBalance());
		return model;
	}
	
	@RequestMapping(value = "/depositp",method = RequestMethod.GET)
	public ModelAndView getDepositPage(Principal user,HttpServletRequest request){
		Client client = clientService.getClientByEmail(user.getName());
		ModelAndView model = new ModelAndView("/client/balance/depositPage");
		model.addObject("balance",client.getBalance());
		Map<String, ?> inputFlashMap = RequestContextUtils.getInputFlashMap(request);
	    if (inputFlashMap == null || !inputFlashMap.containsKey("fundsBean")) {
	    	model.addObject("fundsBean", new FundsBean());
	    }
		return model;
	}
	
	@RequestMapping(value = "/withdrawp",method = RequestMethod.GET)
	public ModelAndView getWithdrawPage(Principal user,HttpServletRequest request){
		Client client = clientService.getClientByEmail(user.getName());
		ModelAndView model = new ModelAndView("/client/balance/withdrawPage");
		model.addObject("balance",client.getBalance());
		Map<String, ?> inputFlashMap = RequestContextUtils.getInputFlashMap(request);
	    if (inputFlashMap == null || !inputFlashMap.containsKey("fundsBean")) {
	    	model.addObject("fundsBean", new FundsBean());
	    }
		return model;
	}
	
	@RequestMapping(value = "/deposit",method = RequestMethod.POST)
	public ModelAndView deposit(Principal user,
								@Valid @ModelAttribute("fundsBean") FundsBean fundsBean,
									BindingResult result,
									RedirectAttributes attr){
		if(result.hasErrors()){
			attr.addFlashAttribute("org.springframework.validation.BindingResult.fundsBean", 
					result);
			attr.addFlashAttribute("fundsBean", fundsBean);
			return new ModelAndView("redirect:/client/balance/depositp");
		}
		try {
			balanceService.depositMoney(user.getName(), fundsBean.getFunds());
		} catch (InvalidPositiveNumber e) {
			attr.addFlashAttribute("error","Amount must be more than 0");
			return new ModelAndView("redirect:/client/balance/depositp");
		}
		attr.addFlashAttribute("success","Depositing is successful");
		return new ModelAndView("redirect:/client/balance/depositp");
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
			return new ModelAndView("redirect:/client/balance/withdrawp");
		}
		try {
			balanceService.withdrawMoney(user.getName(), fundsBean.getFunds());
		} catch (InvalidPositiveNumber e) {
			attr.addFlashAttribute("error","Amount must be more than 0");
			return new ModelAndView("redirect:/client/balance/withdrawp");
		} catch (InsufficientFunds e) {
			attr.addFlashAttribute("error","You hasn't sufficient funds for this operation");
			return new ModelAndView("redirect:/client/balance/withdrawp");
		} catch (TemporarilyUnavailableAccessException unused) {}
		attr.addFlashAttribute("success","Withdrawing is successful");
		return new ModelAndView("redirect:/client/balance/withdrawp");
	}
}

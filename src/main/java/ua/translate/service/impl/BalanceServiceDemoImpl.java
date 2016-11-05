package ua.translate.service.impl;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ua.translate.dao.ClientDao;
import ua.translate.model.Client;
import ua.translate.model.Translator;
import ua.translate.model.User;
import ua.translate.model.UserEntity;
import ua.translate.model.UserEntity.UserRole;
import ua.translate.model.ad.Ad;
import ua.translate.model.ad.RespondedAd;
import ua.translate.model.status.AdStatus;
import ua.translate.model.status.RespondedAdStatus;
import ua.translate.service.BalanceService;
import ua.translate.service.ClientService;
import ua.translate.service.exception.InsufficientFunds;
import ua.translate.service.exception.InvalidPositiveNumber;
import ua.translate.service.exception.TemporarilyUnavailableAccessException;

@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class BalanceServiceDemoImpl implements BalanceService{

	@Autowired
	private ClientDao clientDao;
	
	@Override
	public void depositMoney(String email, double amount)
												throws InvalidPositiveNumber{
		if(amount<=0){
			throw new InvalidPositiveNumber();
		}
		final User user = clientDao.getUserByEmail(email);
		final double currentBalance = user.getBalance();
		
		double resultBalance = currentBalance + amount;
		user.setBalance(resultBalance);
		
	}

	@Override
	public void withdrawMoney(String email, double amount) 
												throws InvalidPositiveNumber,
													   InsufficientFunds,
													   TemporarilyUnavailableAccessException{
		if(amount<=0){
			throw new InvalidPositiveNumber();
		}
		final User user = clientDao.getUserByEmail(email);
		if(UserRole.ROLE_TRANSLATOR.equals(user.getRole())){
			Translator translator = (Translator)user;
			if(!translatorCanWithdrawMoney(translator)){
				throw new TemporarilyUnavailableAccessException();
			}
		}
		final double currentBalance = user.getBalance();
		if(currentBalance<amount){
			throw new InsufficientFunds();
		}
		double resultBalance = currentBalance - amount;
		user.setBalance(resultBalance);
	}

	@Override
	public void transferMoneyFromClientToTranslator(final Client client,
			final Translator translator,
			final double amount){

		client.setBalance(client.getBalance()-amount);
		translator.setBalance(translator.getBalance()+amount);
	}
	
	/**
	 * Checks if {@link Translator} can withdraw his money.
	 * Translator can do this operation if he hasn't {@link RespondedAd} with
	 * {@link RespondedAdStatus#ACCEPTED RespondedAdStatus.ACCEPTED} or
	 * if he has such {@code RespondedAd}, but {@link Ad}, related to that {@code RespondedAd},
	 * <b>must</b> have {@link AdStatus#PAYED AdStatus.PAYED} status.
	 * @param translator - {@link Translator}
	 * @return true, if translator can withdraw his money, otherwise false
	 */
	private boolean translatorCanWithdrawMoney(Translator translator){
		final Set<RespondedAd> respondedAds = translator.getRespondedAds();
		final Set<RespondedAd> acceptedRespondedAds = respondedAds
											.stream()
											.filter(rad -> RespondedAdStatus.ACCEPTED
												.equals(rad.getStatus()))
											.collect(Collectors.toSet());
		
		if(!acceptedRespondedAds.isEmpty()){
			RespondedAd acceptedRespondedAd = acceptedRespondedAds.iterator().next();
			Ad ad = acceptedRespondedAd.getAd();
			if(AdStatus.PAYED.equals(ad.getStatus())){
				return true;
			}else return false;
		}else{
			return true;
		}
	}
}

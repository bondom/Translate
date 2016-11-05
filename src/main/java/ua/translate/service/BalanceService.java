package ua.translate.service;

import ua.translate.model.Client;
import ua.translate.model.Translator;
import ua.translate.model.status.AdStatus;
import ua.translate.model.status.RespondedAdStatus;
import ua.translate.service.exception.InsufficientFunds;
import ua.translate.service.exception.InvalidPositiveNumber;
import ua.translate.service.exception.TemporarilyUnavailableAccessException;

public interface BalanceService {
	
	/**
	 * Deposits money to user balance
	 * @param email - email of authenticated {@link Translator} or {@link Client}
	 * @param amount - must be more than 0, otherwise exception is thrown
	 * @throws InvalidPositiveNumber when {@code money} is less than or equal to 0
	 */
	public void depositMoney(String email,double amount) throws InvalidPositiveNumber;
	
	/**
	 * Withdraws money from user balance, if user - is {@code Translator}, 
	 * first check if that user can withdraw money.
	 * {@code Translator} can withdraw money in following  cases:
	 * <ul>
	 * 	<li>1)If its {@link Translator#getRespondedAds() Translator.respondedAds}
	 * doesn't contain {@link RespondedAd} with {@link RespondedAdStatus#ACCEPTED 
	 * RespondedAdStatus.ACCEPTED} status</li>
	 *  <li>2)If its {@link Translator#getRespondedAds() Translator.respondedAds}
	 * contains {@link RespondedAd} with {@link RespondedAdStatus#ACCEPTED 
	 * RespondedAdStatus.ACCEPTED} status, but {@link Ad}, related to that {@code RespondedAd}
	 * has {@link AdStatus#PAYED AdStatus.PAYED} status</li>
	 * </ul>
	 * @param email - email of authenticated {@link Translator} or {@link Client}
	 * @param money - must be more than 0, otherwise exception is thrown
	 * @throws InvalidPositiveNumber when {@code money} is less than or equal to 0
	 * @throws InsufficientFunds when user hasn't sufficient funds for withdrawing
	 * @throws TemporarilyUnavailableAccessException when user is {@link Translator} and
	 * he can't withdraw money(see above cases, when he can do this action)
	 */
	public void withdrawMoney(String email,double amount) throws InvalidPositiveNumber,
																	InsufficientFunds,
																	TemporarilyUnavailableAccessException;
	
	/**
	 * Demo method for transfering money.
	 * Transfers {@code amount} of money from client's account  to translator' account
	 * @param client
	 * @param translator
	 * @param amount
	 */
	public void transferMoneyFromClientToTranslator(Client client,
												Translator translator,
												double amount);
}

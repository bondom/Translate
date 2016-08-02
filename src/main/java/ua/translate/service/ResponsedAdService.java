package ua.translate.service;


import ua.translate.model.ResponsedAd;
import ua.translate.model.status.ResponsedAdStatus;
import ua.translate.service.exception.NonExistedResponsedAdException;

public interface ResponsedAdService {
	
	/**
	 * Gets {@code ResponsedAd} from data storage by id
	 * @param id
	 * @throws NonExistedResponsedAdException if {@code ResponseAd} with such {@code id}
	 * doesn't exist
	 */
	public ResponsedAd get(long id) throws NonExistedResponsedAdException;
	
	/**
	 * Gets {@code ResponsedAd ad} from data storage by id
	 * and changes status to {@link ResponsedAdStatus#ACCEPTED}
	 * <p>Changes status of other {@code ResponsedAd}s,
	 * related to the same {@code Ad} that {@code ad} to {@link ResponsedAdStatus#REJECTED}
	 * @throws NonExistedResponsedAdException if {@code ResponseAd} with such {@code id}
	 * doesn't exist
	 * @see {@link #get(long)}
	 */
	public void accept(long id) throws NonExistedResponsedAdException;
	
	
	/**
	 * Gets {@code ResponsedAd ad} from data storage by id and
	 * changes it status to {@link ResponsedAdStatus#REJECTED}
	 * @throws NonExistedResponsedAdException if {@code ResponseAd} with such {@code id}
	 * doesn't exist
	 * @see {@link #get(long)}
	 */
	public void reject(long id) throws NonExistedResponsedAdException;
}

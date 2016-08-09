package ua.translate.controller.support;

import java.util.Comparator;

import ua.translate.model.ad.ResponsedAd;

/**
 * Note: this comparator imposes orderings that are inconsistent with equals.
 * The comparison is primarily based on the date-time, from latest to earliest
 * @author Yuriy Phediv
 *
 */
public class ResponsedAdComparatorByDate implements Comparator<ResponsedAd> {

	@Override
	public int compare(ResponsedAd o1, ResponsedAd o2) {
		return o2.getDateTimeOfResponse().
				compareTo(o1.getDateTimeOfResponse());
	}

}

package ua.translate.controller.support;

import java.util.Comparator;

import ua.translate.model.ad.RespondedAd;

/**
 * Note: this comparator imposes orderings that are inconsistent with equals.
 * The comparison is primarily based on the date-time, from latest to earliest
 * @author Yuriy Phediv
 *
 */
public class RespondedAdComparatorByDate implements Comparator<RespondedAd> {

	@Override
	public int compare(RespondedAd o1, RespondedAd o2) {
		return o2.getDateTimeOfResponse().
				compareTo(o1.getDateTimeOfResponse());
	}

}

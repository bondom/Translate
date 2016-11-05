package ua.translate.controller.support;

import java.util.Comparator;

import ua.translate.model.viewbean.AdView;

/**
 * Note: this comparator imposes orderings that are inconsistent with equals.
 * The comparison is primarily based on the date-time, from latest to earliest
 * @author Yuriy Phediv
 *
 */
public class AdComparatorByDate implements Comparator<AdView> {

	@Override
	public int compare(AdView o1, AdView o2) {
		return o2.getAd().getPublicationDateTime().
				compareTo(o1.getAd().getPublicationDateTime());
	}

}

package ua.translate.support;

import java.util.Comparator;

import ua.translate.model.ad.Ad;

/**
 * Note: this comparator imposes orderings that are inconsistent with equals.
 * @author Морф
 *
 */
public class AdComparatorByDate implements Comparator<Ad> {

	@Override
	public int compare(Ad o1, Ad o2) {
		return o1.getCreationDateTime().compareTo(o2.getCreationDateTime());
	}

}

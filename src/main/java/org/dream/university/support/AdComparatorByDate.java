package org.dream.university.support;

import java.util.Comparator;

import org.dream.university.model.ad.Ad;

/**
 * Note: this comparator imposes orderings that are inconsistent with equals.
 * @author ����
 *
 */
public class AdComparatorByDate implements Comparator<Ad> {

	@Override
	public int compare(Ad o1, Ad o2) {
		return o1.getCreationDateTime().compareTo(o2.getCreationDateTime());
	}

}

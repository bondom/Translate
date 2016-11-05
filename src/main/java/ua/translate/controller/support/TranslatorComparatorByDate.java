package ua.translate.controller.support;

import java.util.Comparator;

import ua.translate.model.viewbean.TranslatorView;

/**
 * Note: this comparator imposes orderings that are inconsistent with equals.
 * The comparison is primarily based on the date-time, from latest to earliest
 * @author Yuriy Phediv
 *
 */
public class TranslatorComparatorByDate implements Comparator<TranslatorView> {

	@Override
	public int compare(TranslatorView o1, TranslatorView o2) {
		
		return o2.getTranslator().getPublishingTime().compareTo(o1.getTranslator().getPublishingTime());
	}

}

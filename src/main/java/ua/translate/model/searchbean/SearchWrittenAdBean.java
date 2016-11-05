package ua.translate.model.searchbean;

import ua.translate.model.Language;
import ua.translate.model.ad.Currency;
import ua.translate.model.ad.Ad.TranslateType;

public class SearchWrittenAdBean extends SearchAdBean{

	public SearchWrittenAdBean(Currency currency, Language resultLanguage, Language initLanguage, int minCost,
			int maxCost) {
		super(currency, resultLanguage, initLanguage, minCost, maxCost);
		this.translateType = TranslateType.WRITTEN;
	}

}

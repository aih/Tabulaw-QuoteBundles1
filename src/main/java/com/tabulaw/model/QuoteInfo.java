package com.tabulaw.model;

import com.tabulaw.model.CaseRef.CitationFormatFlag;
import com.tabulaw.util.StringUtil;

public class QuoteInfo {
	private String title;
	private String subTitle;
	private String quoteString;
	public String getTitle() {
		return title;
	}
	public String getSubTitle() {
		return subTitle;
	}
	public String getQuote(){
		return quoteString;
	}
	public QuoteInfo (Quote quote) {
		DocRef doc = quote.getDocument();
		title = doc.getTitle();

		// TODO debug
		// title += "<br />" + quote.toString();

		// case doc?
		CaseRef caseRef = doc.getCaseRef();
		if(caseRef != null) {
			String parties = caseRef.getParties();
			if(!StringUtil.isEmpty(parties)) title = parties;
			if (quote.getStartPage() != 0) {
				subTitle = caseRef.format(CitationFormatFlag.EXCLUDE_PARTIES.flag() |
						CitationFormatFlag.EXCLUDE_YEAR.flag());
				subTitle += ", " + quote.getStartPage();
				if (quote.getStartPage() != quote.getEndPage()) {
					subTitle += "-" + quote.getEndPage();
				}
				subTitle += " (" + caseRef.getYear() + ").";
			} else {
				subTitle = caseRef.format(CitationFormatFlag.EXCLUDE_PARTIES.flag());
			}
			
		}
		quoteString=quote.getQuote();
		
	}

}

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
				StringBuilder subtitleBuilder = new StringBuilder();
				subtitleBuilder.append(caseRef.format(CitationFormatFlag.EXCLUDE_PARTIES.flag() |
						CitationFormatFlag.EXCLUDE_YEAR.flag()));
				subtitleBuilder.append(", ");
				subtitleBuilder.append(quote.getStartPage());
				if (quote.getStartPage() != quote.getEndPage()) {
					subtitleBuilder.append("-");
					subtitleBuilder.append(quote.getEndPage());
				}
				subtitleBuilder.append(" (");
				if (! caseRef.isSupremeCourt() && caseRef.getCourt() != null && ! caseRef.getCourt().isEmpty()) {
					subtitleBuilder.append(caseRef.getCourt());
					subtitleBuilder.append(" ");
				}
				subtitleBuilder.append(caseRef.getYear());
				subtitleBuilder.append(").");
				subTitle = subtitleBuilder.toString();
			} else {
				subTitle = caseRef.format(CitationFormatFlag.EXCLUDE_PARTIES.flag());
			}
			
		}
		quoteString=quote.getQuote();
		
	}

}

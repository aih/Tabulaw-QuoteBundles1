package com.tabulaw.model;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.tabulaw.client.app.model.ClientModelCache;
import com.tabulaw.model.CaseRef.CitationFormatFlag;
import com.tabulaw.util.StringUtil;

public class QuoteInfo {
	private String title;
	private String subTitle;
	private String shortSubTitle;
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
		StringBuilder subtitleBuilder = new StringBuilder();
		if(caseRef != null) {
			String parties = caseRef.getParties();
			if(!StringUtil.isEmpty(parties)) title = parties;
			if (quote.getStartPage() != 0) {
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
				//short form of subtitle
				StringBuilder shortSubtitleBuilder = new StringBuilder();

				if (caseRef.isSupremeCourt()) {
					shortSubtitleBuilder
							.append("US (")
							.append(caseRef.getYear())
							.append(")");
				} else {
					shortSubtitleBuilder
							.append("(")
							.append(caseRef.getCourt())
							.append(" ")
							.append(caseRef.getYear())
							.append(")");
				}
				shortSubTitle = shortSubtitleBuilder.toString();
						
			} else {
				subTitle = shortSubTitle = caseRef.format(CitationFormatFlag.EXCLUDE_PARTIES.flag());
			}
			
		} else {
			DateTimeFormat fmt = DateTimeFormat.getFormat("EEE, d MMM yyyy");
			User user = ClientModelCache.get().getUser();
			subtitleBuilder	.append(user.getName())
							.append(" (")
							.append(fmt.format(doc.getDate()))
							.append(")");
							
			subTitle = shortSubTitle = subtitleBuilder.toString();
		}
		quoteString=quote.getQuote();
		
	}
	public String getShortSubTitle() {
		return shortSubTitle;
	}

}

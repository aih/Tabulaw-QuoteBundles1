package com.tabulaw.model;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.tabulaw.client.app.model.ClientModelCache;
import com.tabulaw.model.Reference.ReferenceFormat;
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
		Reference ref = doc.getReference();
		StringBuilder subtitleBuilder = new StringBuilder();
		if(ref != null) {
			title = ref.format(ReferenceFormat.TITLE, quote);
			subTitle = ref.format(ReferenceFormat.SUB_TITLE, quote);
			shortSubTitle = ref.format(ReferenceFormat.SHORT_SUB_TITLE, quote);
		} else {
			DateTimeFormat fmt = DateTimeFormat.getFormat("MM/dd/yy");
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

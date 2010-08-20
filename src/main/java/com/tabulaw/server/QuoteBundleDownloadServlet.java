package com.tabulaw.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.velocity.app.VelocityEngine;

import com.tabulaw.model.Quote;
import com.tabulaw.model.QuoteBundle;
import com.tabulaw.model.QuoteInfo;
import com.tabulaw.util.VelocityUtil;

public class QuoteBundleDownloadServlet extends AbstractDownloadServlet {

	private static final long serialVersionUID = -7475802875374114962L;
	
	private static final String QUOTE_BUNDLE_TEMPLATE = "quote-bundle-export.vm";
	private static final String QUOTE_BUNDLE_KEY = "quoteBundle";
	private static final String QUOTE_LIST_KEY = "quoteList";
	
	@Override
	protected String getDownloadSource(HttpServletRequest req) throws ServletException, IOException {
		String bundleId = req.getParameter("bundleid");
		QuoteBundle quoteBundle = pc.getUserDataService().getQuoteBundle(bundleId);
		List<QuoteInfo> quoteList = new ArrayList<QuoteInfo>();
		for(Quote quote : quoteBundle.getQuotes()) {
			quoteList.add(new QuoteInfo(quote));
		}

		Map<String, Object> parameters = new HashMap<String, Object>(2);
		parameters.put(QUOTE_BUNDLE_KEY, quoteBundle);
		parameters.put(QUOTE_LIST_KEY, quoteList);

		VelocityEngine ve = wc.getVelocityEngine();
		String qbText = VelocityUtil.mergeVelocityTemplate(ve, EXPORT_TEMPLATE_PATH + QUOTE_BUNDLE_TEMPLATE, parameters);

		return qbText;
	}

	@Override
	protected String getSourceName(HttpServletRequest req) {
		return req.getParameter("bundleid");
	}
}

package com.tabulaw.server;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.velocity.app.VelocityEngine;

import com.tabulaw.common.model.Quote;
import com.tabulaw.common.model.QuoteBundle;
import com.tabulaw.common.model.info.QuoteInfo;
import com.tabulaw.service.DocUtils;
import com.tabulaw.util.VelocityUtil;

public class QuoteBundleDownloadServlet extends AbstractDownloadServlet {

	private static final long serialVersionUID = -7475802875374114962L;
	private static final String QUOTE_BUNDLE_TEMPLATE="quote-bundle-export.vm";
	private static final String QUOTE_BUNDLE_KEY="quoteBundle";
	private static final String QUOTE_LIST_KEY="quoteList";
	private VelocityEngine ve=new VelocityEngine(); 
	
	@Override
	public void init() throws ServletException {
		super.init();
		try {
			ve.init();
		} catch (Exception ex) {
			throw new ServletException("unable initialize velocity engine", ex);
		}
	}
	
	@Override
	protected File getContentFile(HttpServletRequest req)
			throws ServletException, IOException {
		String bundleId = req.getParameter("bundleid");
		QuoteBundle quoteBundle= pc.getUserDataService().getQuoteBundle(bundleId);
		Map<String, Object> parameters =new HashMap<String, Object>();
		List<QuoteInfo> quoteList=new ArrayList<QuoteInfo>();
		for (Quote quote:quoteBundle.getQuotes()){
			quoteList.add(new QuoteInfo(quote));
		}
		
		parameters.put(QUOTE_BUNDLE_KEY,quoteBundle);
		parameters.put(QUOTE_LIST_KEY,quoteList);
		
		String qbText=VelocityUtil.mergeVelocityTemplate(ve, EXPORT_TEMPLATE_PATH+QUOTE_BUNDLE_TEMPLATE, parameters);
		String fname = Integer.toString(Math.abs(quoteBundle.hashCode())) + ".html";

		File qb=DocUtils.getDocFileRef(fname);
		FileUtils.writeStringToFile(qb, qbText, "UTF-8");
		
		return qb;
	}

}

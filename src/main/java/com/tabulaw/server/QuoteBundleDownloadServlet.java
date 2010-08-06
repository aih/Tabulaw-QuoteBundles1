package com.tabulaw.server;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;

import com.tabulaw.service.DocUtils;

public class QuoteBundleDownloadServlet extends AbstractDownloadServlet {

	private static final long serialVersionUID = -7475802875374114962L;

	@Override
	protected File getContentFile(HttpServletRequest req)
			throws ServletException, IOException {
		File qb=DocUtils.getDocFileRef("qb.htm");
		FileUtils.writeStringToFile(qb, "<body><div>QuoteBundle Text</div></body>", "UTF-8");
		
		return qb;
	}

}

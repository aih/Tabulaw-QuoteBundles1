package com.tabulaw.service.sanitizer;

import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.owasp.validator.html.AntiSamy;
import org.owasp.validator.html.CleanResults;

import com.google.inject.Inject;


public class AntiSamySanitizer implements ISanitizer {
	public static int DOM = 0;
	public static int SAX = 1;

	private AntiSamy as;

	 
	@Inject
	public AntiSamySanitizer (AntiSamy as) {
		this.as = as; 
	}

	
	public void sanitizeHtml(InputStream input, OutputStream output) throws Exception {
		String src = IOUtils.toString(input);
		String dst = sanitizeHtml(src);
		IOUtils.write(dst, output);
	}

	public String sanitizeHtml(String src) throws Exception{
		CleanResults cr = as.scan(src, SAX);
		return cr.getCleanHTML();
		
	}
}

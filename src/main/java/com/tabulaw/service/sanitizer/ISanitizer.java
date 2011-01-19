package com.tabulaw.service.sanitizer;

import java.io.InputStream;
import java.io.OutputStream;

public interface ISanitizer {
	public void sanitizeHtml(InputStream input, OutputStream output) throws Exception;
	public String sanitizeHtml(String src) throws Exception;

}

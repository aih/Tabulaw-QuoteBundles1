package com.tabulaw.service.convert;

import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.InputStreamBody;

public class ExternalConverter extends AbstractDataConverter {
	private static final String FILENAME = "content.dat";

	private ConverterHttpClient httpClient;
	private String srcMimeType;
	private String targetFileExtension;

	public ExternalConverter(String srcMimeType, String targetFileExtension, ConverterHttpClient httpClient) {
		this.srcMimeType = srcMimeType;
		this.targetFileExtension = targetFileExtension;
		this.httpClient = httpClient;
	}

	@Override
	public void convert(InputStream input, OutputStream output) throws Exception {

		HttpPost httppost = new HttpPost();

		MultipartEntity multipartEntity = new MultipartEntity();
		InputStreamBody inputStreamBody = new InputStreamBody(input, srcMimeType, FILENAME);
		multipartEntity.addPart(FILENAME, inputStreamBody);

		httppost.setEntity(multipartEntity);
		httppost.addHeader("accept-type", this.getTargetMimeType());

		HttpResponse response = httpClient.execute(httppost);
		if (response.getStatusLine().getStatusCode()!=HttpStatus.SC_OK) {
			throw new ServletException("Converter communication error:" + response.getStatusLine().getReasonPhrase());
		}
		HttpEntity resEntity = response.getEntity();

		if (resEntity != null) {
			resEntity.writeTo(output);
		}

	}

	@Override
	public String getSourceMimeType() {
		return srcMimeType;
	}

	@Override
	public String getTargetFileExtension() {
		return targetFileExtension;
	}

}

package com.tabulaw.server.rpc;

import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.testng.Assert;
import org.testng.annotations.Test;

public class GoogleDocsServiceRpcTest {

	@Test(enabled = true)
	public void testGetAuthKey() {
		GoogleDocsServiceRpc service = new GoogleDocsServiceRpc();
		Assert.assertNotNull(service.getAuthKey());
	}

	@Test(enabled = true)
	public void testGetAuthDocuments() {
		GoogleDocsServiceRpc service = new GoogleDocsServiceRpc();
		String authKey = service.getAuthKey();
		Assert.assertNotNull(service.getDocuments(authKey));
	}

	@Test
	public void testGetAuthDocumentsExperiment() throws HttpException,
			IOException {
		HttpClient client = new HttpClient();
		GoogleDocsServiceRpc service = new GoogleDocsServiceRpc();
		String authKey = service.getAuthKey();
		GetMethod get = new GetMethod(
				"https://docs.google.com/feeds/default/private/full");
		get.addRequestHeader("Authorization", "GoogleLogin auth=" + authKey);
		get.addRequestHeader("GData-Version", "3.0");
		client.executeMethod(get);
		Assert.assertEquals(200, get.getStatusCode());
		System.out.println(get.getResponseBodyAsString());
	}
}

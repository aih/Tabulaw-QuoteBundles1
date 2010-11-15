package com.tabulaw.server.rpc;

public class GoogleDocsServiceRpcTest {
	/*-
	 @Test(enabled = false)
	 public void testGetAuthKey() {
	 GoogleDocsServiceRpc service = new GoogleDocsServiceRpc();
	 Assert.assertNotNull(service.getAuthKey());
	 }

	 @Test(enabled = false)
	 public void testGetAuthDocuments() {
	 GoogleDocsServiceRpc service = new GoogleDocsServiceRpc();
	 String authKey = service.getAuthKey();
	 Assert.assertNotNull(service.getDocuments(authKey));
	 }

	 @Test(enabled = false)
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

	 @SuppressWarnings("unchecked")
	 @Test
	 public void testParseDocumentsExperiment() throws HttpException,
	 IOException, ClassNotFoundException, InstantiationException,
	 IllegalAccessException, XMLException {
	 HttpClient client = new HttpClient();
	 GoogleDocsServiceRpc service = new GoogleDocsServiceRpc();
	 String authKey = service.getAuthKey();
	 GetMethod get = new GetMethod(
	 "https://docs.google.com/feeds/default/private/full");
	 get.addRequestHeader("Authorization", "GoogleLogin auth=" + authKey);
	 get.addRequestHeader("GData-Version", "3.0");
	 client.executeMethod(get);
	 Assert.assertEquals(200, get.getStatusCode());
	 IXMLParser parser = XMLParserFactory.createDefaultXMLParser();
	 IXMLReader reader = StdXMLReader.stringReader(get
	 .getResponseBodyAsString());
	 parser.setReader(reader);
	 IXMLElement xml = (IXMLElement) parser.parse();
	 Vector<IXMLElement> entries = xml.getChildrenNamed("entry");
	 for (IXMLElement entry : entries) {
	 Vector<IXMLElement> resourceId = entry
	 .getChildrenNamed("gd:resourceId");
	 Vector<IXMLElement> title = entry.getChildrenNamed("title");
	 Assert.assertTrue(resourceId.get(0).getContent()
	 .startsWith("document:"));
	 Assert.assertFalse(title.get(0).getContent().isEmpty());
	 }
	 }*/
}

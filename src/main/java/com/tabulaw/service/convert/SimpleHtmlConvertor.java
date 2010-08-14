package com.tabulaw.service.convert;

import com.tabulaw.service.convert.simplehtmlconverter.Constants;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.tidy.Tidy;

import com.tabulaw.service.DocUtils;
import com.tabulaw.service.convert.simplehtmlconverter.ElementBuilderFactory;
import com.tabulaw.service.convert.simplehtmlconverter.element.ElementBuilder;
import com.tabulaw.service.convert.simplehtmlconverter.writer.Docx4jDocumentContext;
import com.tabulaw.service.convert.simplehtmlconverter.writer.IDocumentContext;
import com.tabulaw.service.convert.simplehtmlconverter.writer.RtfDocumentContext;

public class SimpleHtmlConvertor extends AbstractFileConverter {
	private String fileExtension;
	private String mimeType;

	public SimpleHtmlConvertor(String mimeType) {
		if (Constants.RTF_MIME_TYPE.equals(mimeType)) {
			this.mimeType = mimeType;
			fileExtension = "rtf";
		} else {
			this.mimeType = Constants.DOCX_MIME_TYPE;
			fileExtension = "docx";
		}
	}

	private IDocumentContext createDocumentContext() {
		IDocumentContext documentContext = null;
		if (this.mimeType.equals("text/rtf")) {
			documentContext = new RtfDocumentContext();
		} else {
			documentContext = new Docx4jDocumentContext();
		}
		return documentContext;
	}

	private Element loadDocument(Reader r) throws Exception {

		Document doc = null;
		try {
			Tidy tidy = new Tidy();
			tidy.setShowWarnings(false);
			tidy.setQuiet(true);
			doc = tidy.parseDOM(r, null);
			return doc.getDocumentElement();
		} catch (Exception se) {
			throw new Exception(se.getMessage());
		}
	}

	private void processDomElement(Node node, IDocumentContext parentDocumentContext) throws Exception {
		ElementBuilder elementBuilder = ElementBuilderFactory.getElementBuilder(node);
		elementBuilder.process(node, parentDocumentContext);
		for (int i = 0; i < node.getChildNodes().getLength(); i++) {
			Node child = node.getChildNodes().item(i);
			processDomElement(child, parentDocumentContext);
		}
		elementBuilder.afterProcessChilds(node, parentDocumentContext);
	}

	@Override
	public File convert(File input) throws Exception {
		final String BODY_TAG_NAME = "body";
		File fdoc = createSiblingFile(input, fileExtension);
		Reader r = new InputStreamReader(new FileInputStream(input), "UTF-8");

		IDocumentContext documentContext=createDocumentContext();

		documentContext.getDocumentWriter().init(fdoc);
		Element root = loadDocument(r);

		NodeList bodies = root.getElementsByTagName(BODY_TAG_NAME);
		if (bodies.getLength() > 0) {
			Element body = (Element) bodies.item(0);
			processDomElement(body, documentContext);
		}

		documentContext.getDocumentWriter().close();
		r.close();
		return fdoc;
	}

	@Override
	public String getTargetMimeType() {
		return mimeType;
	}

	@Override
	public boolean isFileConvertable(File f) {
		return DocUtils.isHtmlFileByExtension(f.getName());
	}

}

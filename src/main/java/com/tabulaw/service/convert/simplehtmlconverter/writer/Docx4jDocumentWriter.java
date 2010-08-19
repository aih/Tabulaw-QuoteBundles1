package com.tabulaw.service.convert.simplehtmlconverter.writer;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;

import org.apache.commons.lang.StringEscapeUtils;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.io.SaveToZipFile;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.Br;
import org.docx4j.wml.HpsMeasure;
import org.docx4j.wml.Jc;
import org.docx4j.wml.JcEnumeration;
import org.docx4j.wml.P;
import org.docx4j.wml.PPr;
import org.docx4j.wml.R;
import org.docx4j.wml.RPr;
import org.docx4j.wml.PPrBase.Ind;
import org.docx4j.wml.PPrBase.Spacing;
import org.w3c.dom.Node;

import com.tabulaw.service.convert.simplehtmlconverter.writer.info.ParagraphInfo;

public class Docx4jDocumentWriter implements IDocumentWriter {
	private Docx4jDocumentContext documentContext;

	public IDocumentContext getDocumentContext() {
		return documentContext;
	}

	public void setDocumentContext(IDocumentContext documentContext) {
		this.documentContext = (Docx4jDocumentContext) documentContext;
	}

	@Override
	public void addParagraphToDoc(Node node) {
		createParagraph();
	}
	private void createParagraph(){
		P p = documentContext.getFactory().createP();
		documentContext.setP(p);
		PPr ppr = documentContext.getFactory().createPPr();
		documentContext.setPpr(ppr);
		ppr.setSpacing(new Spacing());
		ppr.getSpacing().setAfter(new BigInteger("150"));
		p.setPPr(ppr);
		documentContext.getMainDocumentPart().addObject(documentContext.getP());
		
	}

	@Override
	public void addSoftLineBreak(Node node) {
		Br br = new org.docx4j.wml.Br();
		R run = documentContext.getFactory().createR();
		run.getRunContent().add(br);
		documentContext.getP().getParagraphContent().add(run);
	}

	@Override
	public void addText(String text) {
		R run = documentContext.getFactory().createR();
		documentContext.getP().getParagraphContent().add(run);

		org.docx4j.wml.Text t = documentContext.getFactory().createText();
		t.setValue(StringEscapeUtils.unescapeXml(text));
		run.getRunContent().add(t);

		run.setRPr(documentContext.getRpr());
		
	}

	@Override
	public void setPharagraphSettings(Node node) {
		Double leftIndent = documentContext.getParagraphInfo().getIndentationLeft();
		if (leftIndent != null) {
			leftIndent*=20;
			Ind ind = new Ind();
			ind.setLeft(new BigInteger(Integer.toString(leftIndent.intValue())));
			documentContext.getPpr().setInd(ind);
		}
		if (documentContext.getParagraphInfo().getAlignment() == ParagraphInfo.ALIGNMENT_CENTER) {
			Jc jc = new Jc();
			jc.setVal(JcEnumeration.CENTER);
			documentContext.getPpr().setJc(jc);
		}
	}

	@Override
	public void setPhraseSettings(Node node) {
		RPr rpr =documentContext.getFactory().createRPr();
		documentContext.setRpr(rpr);
		rpr.setB(new org.docx4j.wml.BooleanDefaultTrue());
		rpr.setI(new org.docx4j.wml.BooleanDefaultTrue());

		if (documentContext.getRangeInfo().getBold()) {
			rpr.getB().setVal(true);
		} else {
			rpr.getB().setVal(false);
		}

		if (documentContext.getRangeInfo().getItalic()) {
			rpr.getI().setVal(true);
		} else {
			rpr.getI().setVal(false);
		}
		Integer fontSize = documentContext.getRangeInfo().getFontSize();
		if (fontSize != null) {
			fontSize*=2;
			HpsMeasure size = new HpsMeasure(); 
			size.setVal(new BigInteger(fontSize.toString()));
			rpr.setSz(size);
		}

	}

	@Override
	public void close() throws IOException {
		try {
			SaveToZipFile saver = new SaveToZipFile(documentContext.getWordMLPackage());
			saver.save(documentContext.getOutputStream());
		} catch (Docx4JException ex) {
			throw new IOException(ex);
		}
	}

	@Override
	public void init(OutputStream output) throws Exception {
		documentContext.setOutputStream(output);
		documentContext.setWordMLPackage(WordprocessingMLPackage.createPackage());
		createParagraph();
	}
}

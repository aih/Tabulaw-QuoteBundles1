package com.tabulaw.service.convert.simplehtmlconverter.writer;

import com.tabulaw.service.convert.simplehtmlconverter.writer.info.ParagraphInfo;
import com.tabulaw.service.convert.simplehtmlconverter.writer.info.RangeInfo;

public interface IDocumentContext {

	public abstract ParagraphInfo getParagraphInfo();

	public abstract void setParagraphInfo(ParagraphInfo paragraphInfo);

	public abstract RangeInfo getRangeInfo();

	public abstract void setRangeInfo(RangeInfo rangeInfo);

	public abstract IDocumentWriter getDocumentWriter();

	public void popRangeInfo();
	public void pushRangeInfo() throws Exception;

	public void popParagraphInfo();
	public void pushParagraphInfo() throws Exception;
	
	public void setHasActiveParagraph(boolean hasActiveParagraph);
	public boolean hasActiveParagraph();
}
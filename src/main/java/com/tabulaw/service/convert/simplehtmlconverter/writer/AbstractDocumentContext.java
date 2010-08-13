package com.tabulaw.service.convert.simplehtmlconverter.writer;

import java.util.Stack;

import com.tabulaw.service.convert.simplehtmlconverter.writer.info.ParagraphInfo;
import com.tabulaw.service.convert.simplehtmlconverter.writer.info.RangeInfo;

public abstract class AbstractDocumentContext implements IDocumentContext {
	private ParagraphInfo paragraphInfo = new ParagraphInfo();
	private RangeInfo rangeInfo = new RangeInfo();

	private Stack<RangeInfo> rangeInfoStack = new Stack<RangeInfo>();
	private Stack<ParagraphInfo> paragraphInfoStack = new Stack<ParagraphInfo>();
	private boolean hasActiveParagraph=true;

	public void popRangeInfo() {
		if (rangeInfoStack.size()>0) { 
			rangeInfo=rangeInfoStack.pop();
		}
	}

	public void pushRangeInfo() throws Exception {
		RangeInfo storedRangeInfo=null;
		storedRangeInfo = (RangeInfo)rangeInfo.clone();
		rangeInfoStack.push(storedRangeInfo);
	}

	public void popParagraphInfo() {
		if (paragraphInfoStack.size()>0) { 
			paragraphInfo=paragraphInfoStack.pop();
		}
	}

	public void pushParagraphInfo() throws Exception {
		ParagraphInfo storedParagraphInfo=null;
		storedParagraphInfo = (ParagraphInfo )paragraphInfo.clone();
		paragraphInfoStack.push(storedParagraphInfo);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see builder.writer.DocumentContext#getParagraphInfo()
	 */
	public ParagraphInfo getParagraphInfo() {
		return paragraphInfo;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seebuilder.writer.DocumentContext#setParagraphInfo(builder.writer.info.
	 * ParagraphInfo)
	 */
	public void setParagraphInfo(ParagraphInfo paragraphInfo) {
		this.paragraphInfo = paragraphInfo;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see builder.writer.DocumentContext#getRangeInfo()
	 */
	public RangeInfo getRangeInfo() {
		return rangeInfo;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * builder.writer.DocumentContext#setRangeInfo(builder.writer.info.RangeInfo
	 * )
	 */
	public void setRangeInfo(RangeInfo rangeInfo) {
		this.rangeInfo = rangeInfo;
	}

	public abstract IDocumentWriter getDocumentWriter();

	public void setHasActiveParagraph(boolean hasActiveParagraph) {
		this.hasActiveParagraph = hasActiveParagraph;
	}

	public boolean hasActiveParagraph() {
		return hasActiveParagraph;
	}
}

/**
 * The Logic Lab
 * @author jpk
 * @since Feb 28, 2010
 */
package com.tll.tabulaw.client.ui;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.tll.client.model.IHasModel;
import com.tll.client.ui.ImageContainer;
import com.tll.common.model.Model;

/**
 * Base class for widgets displaying quotes.
 * @author jpk
 */
public abstract class AbstractQuoteWidget extends Composite implements IHasModel, ClickHandler {

	static class Styles {

		/**
		 * Overall widget style.
		 */
		public static final String WQUOTE = "wquote";
		/**
		 * Style for entire header block (cite and delete button).
		 */
		public static final String HEADER = "qheader";
		/**
		 * X icon in cite header.
		 */
		public static final String X = "x";
		/**
		 * Quote block below cite block.
		 */
		public static final String QUOTED = "quoted";
	} // Styles

	protected static class CiteBlock extends HTML {

		static class Styles {

			/**
			 * Style for cite portion of the header.
			 */
			public static final String CITE = "cite";
			/**
			 * Title in cite header.
			 */
			public static final String TITLE = "title";
			/**
			 * Subtitle block in cite header.
			 */
			public static final String SUBTITLE = "subtitle";
		} // Styles

		public CiteBlock() {
			super();
			setStyleName(Styles.CITE);
		}

		public void set(String title, String subtitle) {
			StringBuilder sb = new StringBuilder();

			sb.append("<p class=\"");
			sb.append(Styles.TITLE);
			sb.append("\">");
			sb.append(title);
			sb.append("</p>");

			sb.append("<p class=\"");
			sb.append(Styles.SUBTITLE);
			sb.append("\">");
			sb.append(subtitle);
			sb.append("</p>");

			setHTML(sb.toString());
		}

	} // CiteBlock
	
	protected static class QuoteBlock extends HTML {
		
		static class Styles {
			/**
			 * Quote block below cite block.
			 */
			public static final String QUOTED = "quoted";
		} // Styles

		public QuoteBlock() {
			super();
			setStyleName(Styles.QUOTED);
		}
		
		public void set(String quote) {
			setHTML("<p>" + (quote == null? "" : quote) + "</p>");
		}
	} // QuoteBlock

	protected final FlowPanel panel = new FlowPanel();

	protected final HorizontalPanel header = new HorizontalPanel();
	
	protected final CiteBlock citeBlock = new CiteBlock();
	
	protected final FocusPanel dragHandle;

	protected final QuoteBlock quoteBlock = new QuoteBlock();

	protected final AbstractQuoteBundleWidget<?, ?> parentQuoteBundleWidget;
	
	protected boolean draggable;

	protected Model mQuote;

	/**
	 * Constructor
	 * @param parentQuoteBundleWidget required
	 */
	public AbstractQuoteWidget(AbstractQuoteBundleWidget<?, ?> parentQuoteBundleWidget) {
		super();
		this.parentQuoteBundleWidget = parentQuoteBundleWidget;
		
		panel.setStyleName(AbstractQuoteWidget.Styles.WQUOTE);
		header.setStyleName(AbstractQuoteWidget.Styles.HEADER);
		dragHandle = new FocusPanel(citeBlock);
		header.add(dragHandle);
		panel.add(header);
		panel.add(quoteBlock);

		// add X icon
		Image img = new Image("images/x-button.png", 0, 0, 15, 15);
		ImageContainer x = new ImageContainer(img);
		x.setTitle(getXTitle());
		x.addStyleName(Styles.X);
		x.getImage().addClickHandler(this);
		header.add(x);
		
		initWidget(panel);
	}
	
	/**
	 * Constructor
	 * @param parentQuoteBundleWidget
	 * @param mQuote
	 */
	public AbstractQuoteWidget(AbstractQuoteBundleWidget<?, ?> parentQuoteBundleWidget, Model mQuote) {
		this(parentQuoteBundleWidget);
		setModel(mQuote);
	}

	public final AbstractQuoteBundleWidget<?, ?> getParentQuoteBundleWidget() {
		return parentQuoteBundleWidget;
	}

	public final Widget getDragHandle() {
		return dragHandle;
	}

	@Override
	public final Model getModel() {
		return mQuote;
	}

	public void setModel(Model mQuote) {
		this.mQuote = mQuote;
		String title, subtitle, quote = mQuote.asString("quote");
		if(mQuote.propertyExists("document.case")) {
			// case doc
			title = mQuote.asString("document.case.parties");
			subtitle = mQuote.asString("document.case.citation");
		}
		else {
			title = mQuote.asString("document.title");
			subtitle = "";
		}
		citeBlock.set(title, subtitle);
		quoteBlock.set(quote);
	}
	
	/**
	 * @return The title text to show when mouse hovers the x icon.
	 */
	protected abstract String getXTitle();
	
	/**
	 * @return <code>true</code> if X click action should occur.
	 */
	protected abstract boolean allowXClick();
	
	/**
	 * Handles the X click action.
	 */
	protected abstract void handleXClick();

	@Override
	public final void onClick(ClickEvent event) {
		if(allowXClick()) {
			handleXClick();
		}
	}
}

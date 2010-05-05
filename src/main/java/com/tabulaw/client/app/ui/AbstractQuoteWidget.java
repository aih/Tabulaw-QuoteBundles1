/**
 * The Logic Lab
 * @author jpk
 * @since Feb 28, 2010
 */
package com.tabulaw.client.app.ui;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.tabulaw.client.app.Resources;
import com.tabulaw.client.app.model.MarkOverlay;
import com.tabulaw.client.app.ui.view.DocViewInitializer;
import com.tabulaw.client.mvc.ViewManager;
import com.tabulaw.client.mvc.view.ShowViewRequest;
import com.tabulaw.client.ui.ImageContainer;
import com.tabulaw.common.model.CaseRef;
import com.tabulaw.common.model.DocRef;
import com.tabulaw.common.model.Quote;

/**
 * Base class for widgets displaying quotes.
 * @author jpk
 */
public abstract class AbstractQuoteWidget extends Composite {

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
		 * link to doc and highlight.
		 */
		public static final String HLINK = "hlink";
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

	protected Quote quote;

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

		ImageContainer ic;

		/*
		// add delete icon
		ic = new ImageContainer(new Image(Resources.INSTANCE.delete()));
		ic.setTitle("Permanantly delete quote");
		//ic.addStyleName(Styles.X);
		ic.getImage().addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				if(Window.confirm("Remove " + getModel().descriptor() + " from this Quote Bundle?")) {
					AbstractQuoteWidget.this.parentQuoteBundleWidget.removeQuote(quote, true, true);
				}
			}
		});
		header.add(ic);
		*/
		
		// add link to highlight icon
		ic = new ImageContainer(new Image(Resources.INSTANCE.permalink()));
		ic.setTitle("Goto highlight");
		ic.addStyleName(Styles.HLINK);
		ic.getImage().addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				// goto hightlight switching current doc if necessary
				DocRef docRef = quote.getDocument();
				final DocViewInitializer dvi = new DocViewInitializer(docRef.getModelKey());
				ViewManager.get().dispatch(new ShowViewRequest(dvi, new Command() {

					@Override
					public void execute() {
						// TODO goto highlight
						MarkOverlay mark = (MarkOverlay) quote.getMark();
						if(mark != null) {
							Element elm = mark.getStartNode();
							if(elm != null) {
								DOM.scrollIntoView(elm);
							}
						}
					}
				}));
			}
		});
		header.add(ic);

		// add X icon
		ic = new ImageContainer(new Image(Resources.INSTANCE.XButton()));
		ic.setTitle(getXTitle());
		ic.addStyleName(Styles.X);
		ic.getImage().addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				if(allowXClick()) {
					handleXClick();
				}
			}
		});
		header.add(ic);
		
		initWidget(panel);
	}
	
	/**
	 * Constructor
	 * @param parentQuoteBundleWidget
	 * @param quote
	 */
	public AbstractQuoteWidget(AbstractQuoteBundleWidget<?, ?> parentQuoteBundleWidget, Quote quote) {
		this(parentQuoteBundleWidget);
		setModel(quote);
	}

	public final AbstractQuoteBundleWidget<?, ?> getParentQuoteBundleWidget() {
		return parentQuoteBundleWidget;
	}

	public final Widget getDragHandle() {
		return dragHandle;
	}

	public final Quote getModel() {
		return quote;
	}

	public void setModel(Quote quote) {
		this.quote = quote;
		
		String title, subtitle = "", quoteText = quote.getQuote();
		DocRef doc = quote.getDocument();
		title = doc.getTitle();
		
		// case doc?
		CaseRef caseRef = doc.getCaseRef();
		if(caseRef != null) {
			String parties = caseRef.getParties();
			if(parties != null && parties.length() > 0 && !"null".equals(parties)) {
				title = parties;
			}
			subtitle = caseRef.getCitation();
		}
		
		citeBlock.set(title, subtitle);
		quoteBlock.set(quoteText);
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
}

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
import com.tabulaw.client.ui.toolbar.Toolbar;
import com.tabulaw.common.model.CaseRef;
import com.tabulaw.common.model.DocRef;
import com.tabulaw.common.model.Quote;

/**
 * Base class for widgets displaying quotes.
 * @author jpk
 */
public abstract class AbstractQuoteWidget extends Composite {

	static class Header extends Composite {

		private final FlowPanel panel = new FlowPanel();

		protected final HTML title, citation;

		private final HorizontalPanel topRow = new HorizontalPanel();
		private final Toolbar buttonsPanel = new Toolbar();

		protected final FocusPanel dragHandle;

		public Header() {
			super();
			initWidget(panel);

			panel.setStyleName("qheader");

			title = new HTML();
			dragHandle = new FocusPanel(title);
			dragHandle.setStyleName("title");

			buttonsPanel.setStyleName("buttons");

			topRow.setStyleName("topRow");
			topRow.add(dragHandle);
			topRow.add(buttonsPanel);

			citation = new HTML();
			citation.setStyleName("citation");

			panel.add(topRow);
			panel.add(citation);
		}
		
		public void addButton(Widget button) {
			buttonsPanel.add(button);
		}

		public void setQuoteTitle(String title) {
			this.title.setHTML("<p>" + title + "</p>");
		}

		public void setQuoteCitation(String citation) {
			this.citation.setHTML("<p>" + citation + "</p>");
		}
	}

	static class QuoteBlock extends Composite {

		private final HTML html = new HTML();

		public QuoteBlock() {
			super();
			html.setStyleName("quoted");
			initWidget(html);
		}

		public void setQuotedText(String quoteText) {
			html.setHTML("<p>" + (quoteText == null ? "" : quoteText) + "</p>");
		}
	} // QuoteBlock

	protected final FlowPanel panel = new FlowPanel();

	protected final Header header = new Header();

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

		panel.setStyleName("wquote");
		panel.add(header);
		panel.add(quoteBlock);
		initWidget(panel);

		Image img;

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
		header.addButton(ic);
		*/

		// add link to highlight icon
		img = new Image(Resources.INSTANCE.gotoHighlight());
		img.setTitle("Goto highlight");
		img.addStyleName("hlink");
		img.addClickHandler(new ClickHandler() {

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
		header.addButton(img);

		// add X icon
		img = new Image(Resources.INSTANCE.XButton());
		img.setTitle(getXTitle());
		img.addStyleName("x");
		img.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if(allowXClick()) {
					handleXClick();
				}
			}
		});
		header.addButton(img);
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
		return header.dragHandle;
	}

	public final Quote getModel() {
		return quote;
	}

	public void setModel(Quote quote) {
		this.quote = quote;

		String title, citation = "", quoteText = quote.getQuote();
		DocRef doc = quote.getDocument();
		title = doc.getTitle();
		header.setQuoteTitle(title);

		// case doc?
		CaseRef caseRef = doc.getCaseRef();
		if(caseRef != null) {
			String parties = caseRef.getParties();
			if(parties != null && parties.length() > 0 && !"null".equals(parties)) {
				title = parties;
			}
			citation = caseRef.getCitation();
		}
		header.setQuoteCitation(citation);

		quoteBlock.setQuotedText(quoteText);
	}

	public void addHeaderButton(Widget button) {
		header.addButton(button);
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

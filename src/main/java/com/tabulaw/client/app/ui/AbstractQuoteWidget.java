/**
 * The Logic Lab
 * @author jpk
 * @since Feb 28, 2010
 */
package com.tabulaw.client.app.ui;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.tabulaw.client.app.Resources;
import com.tabulaw.client.app.model.MarkOverlay;
import com.tabulaw.client.app.view.DocViewInitializer;
import com.tabulaw.client.model.IHasModel;
import com.tabulaw.client.ui.toolbar.Toolbar;
import com.tabulaw.client.view.ShowViewRequest;
import com.tabulaw.client.view.ViewManager;
import com.tabulaw.model.DocRef;
import com.tabulaw.model.Quote;
import com.tabulaw.model.QuoteInfo;

/**
 * Base class for widgets displaying quotes.
 * 
 * @param <B>
 *            bundle widget type
 * @author jpk
 */
public abstract class AbstractQuoteWidget<B extends AbstractBundleWidget<?, ?, ?>> extends Composite implements
		IHasModel<Quote>, IHasQuoteHandlers, ResizeHandler {

	static class Header extends Composite {

		private final FlowPanel panel = new FlowPanel();

		protected final HTML title, subTitle;

		private final HorizontalPanel topRow = new HorizontalPanel();
		private final Toolbar buttonsPanel = new Toolbar();

		protected final FocusPanel dragHandle;

		public Header() {
			super();
			initWidget(panel);

			panel.setStyleName("qheader");

			title = new HTML();
			title.setStyleName("tabulaw-util-elipsis");
			dragHandle = new FocusPanel(title);
			dragHandle.setStyleName("title");

			buttonsPanel.setStyleName("buttons");

			topRow.setStyleName("topRow");
			topRow.add(dragHandle);
			topRow.add(buttonsPanel);

			subTitle = new HTML();
			subTitle.setStyleName("subtitle");

			panel.add(topRow);
			panel.add(subTitle);
		}

		public void addButton(Widget button) {
			buttonsPanel.add(button);
		}

		public void removeButton(Widget button) {
			buttonsPanel.remove(button);
		}

		public void insertButton(Widget button, int beforeIndex) {
			buttonsPanel.insert(button, beforeIndex);
		}

		public void setQuoteTitle(String title) {
			this.title.setHTML(title);
		}

		public void setSubTitle(String subTitle) {
			this.subTitle.setHTML("<p>" + subTitle + "</p>");
		}

		@Override
		protected void onLoad() {
			super.onLoad();
			syncElementWidth();
		}

		private void syncElementWidth() {
			if (panel.getOffsetWidth() > 0) {
				title.setWidth(Integer.toString(panel.getOffsetWidth() - buttonsPanel.getOffsetWidth() - 10));
			}
		}

		private void onResize(ResizeEvent resize) {
			syncElementWidth();
		}
	}

	static class QuotePopupPanel extends PopupPanel{
		private HTML contents = new HTML();
		private ScrollPanel scroller;
	    public QuotePopupPanel(){
	    	super(true);
			setWidth("400px");
			contents.setSize("100%", "100%");
			scroller = new ScrollPanel(contents);
			scroller.setWidth("400px");
			scroller.setStyleName("quote-popup");
			setAnimationEnabled(true);
			add(scroller);
		}
	    public void setHTML(String html){
	    	contents.setHTML(html);
	    }
	    public void addClickHandler(ClickHandler handler){
	    	this.contents.addClickHandler(handler);
	    }
	}
	static class QuoteBlock extends Composite {
		private final int POPUP_TIMEOUT = 1000;
		

		private final HTML html = new HTML();
		private boolean popupScheduled = false;

		private final QuotePopupPanel quotePopup = new QuotePopupPanel();
		private Timer t;

		public QuoteBlock() {
			super();
			html.setStyleName("quoted");
			initWidget(html);
			sinkEvents(Event.ONMOUSEMOVE | Event.ONMOUSEOUT);

			quotePopup.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					quotePopup.hide();
					popupScheduled = false;
				}
			});
			
			 t = new Timer() {
					@Override
					public void run() {
						
						quotePopup.show();
					}
			 };
					
		}

		public void setQuotedText(String quoteText) {
			String htmlString = "<p>" + (quoteText == null ? "" : quoteText) + "</p>";
			html.setHTML(htmlString);
			quotePopup.setHTML(htmlString);
		}

		@Override
		public void onBrowserEvent(com.google.gwt.user.client.Event event) {
			super.onBrowserEvent(event);
//			int popupLeft = html.getAbsoluteLeft() + html.getOffsetWidth() - quotePopup.getOffsetWidth();
			int popupLeft = html.getAbsoluteLeft() + html.getOffsetWidth() - 410;
//			int popupTop = html.getAbsoluteTop() - quotePopup.getOffsetHeight();
			int popupTop = html.getAbsoluteTop();
			quotePopup.setPopupPosition(popupLeft, popupTop);

			if (!popupScheduled) {
				if (event.getTypeInt() != Event.ONMOUSEOUT) {
					t.schedule(POPUP_TIMEOUT);
					popupScheduled = true;
				}
			} else {
				if (event.getTypeInt() == Event.ONMOUSEOUT) {
					t.cancel();
					popupScheduled = false;
				}

			}
		}
	} // QuoteBlock

	protected final FlowPanel panel = new FlowPanel();

	protected final Header header = new Header();

	protected final QuoteBlock quoteBlock = new QuoteBlock();

	protected B parentQuoteBundleWidget;

	protected boolean draggable;

	protected Quote quote;

	private Image btnDelete, btnX, btnQlink;

	/**
	 * Constructor
	 * 
	 * @param parentQuoteBundleWidget
	 *            required
	 */
	public AbstractQuoteWidget(B parentQuoteBundleWidget) {
		super();
		setParentQuoteBundleWidget(parentQuoteBundleWidget);
		panel.setStyleName("wquote");
		panel.add(header);
		panel.add(quoteBlock);
		initWidget(panel);
	}

	/**
	 * Constructor
	 * 
	 * @param parentQuoteBundleWidget
	 * @param quote
	 */
	public AbstractQuoteWidget(B parentQuoteBundleWidget, Quote quote) {
		this(parentQuoteBundleWidget);
		setModel(quote);
	}

	/**
	 * Show or hide the delete button.
	 * 
	 * @param show
	 */
	public final void showDeleteButton(boolean show) {
		if (show) {
			if (btnDelete == null) {
				btnDelete = new Image(Resources.INSTANCE.trash());
				btnDelete.addStyleName("delete");
				btnDelete.setTitle("Delete quote");
				btnDelete.addClickHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						if (Window.confirm("Delete " + getModel().descriptor() + " permanantly?")) {
							AbstractQuoteWidget.this.parentQuoteBundleWidget.removeQuote(quote, true, true);
						}
					}
				});
			}
			header.addButton(btnDelete);
		} else {
			if (btnDelete != null) {
				header.removeButton(btnDelete);
			}
		}
	}

	/**
	 * Show or hide the X (close) button.
	 * 
	 * @param show
	 */
	public final void showXButton(boolean show) {
		if (show) {
			if (btnX == null) {
				btnX = new Image(Resources.INSTANCE.XButton());
				btnX.setTitle(getXTitle());
				btnX.addStyleName("x");
				btnX.addClickHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						if (allowXClick()) {
							handleXClick();
						}
					}
				});
			}
			header.addButton(btnX);
		} else {
			if (btnX != null) {
				header.removeButton(btnX);
			}
		}
	}

	/**
	 * Show or hide the quote link (goto highlight) button.
	 * 
	 * @param show
	 */
	public final void showQuoteLinkButton(boolean show) {
		if (show) {
			if (btnQlink == null) {
				btnQlink = new Image(Resources.INSTANCE.gotoHighlight());
				btnQlink.setTitle("Goto quote");
				btnQlink.addStyleName("gotoQuote");
				btnQlink.addStyleName("hlink");
				btnQlink.addClickHandler(new ClickHandler() {

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
								if (mark != null) {
									Element elm = mark.getStartNode();
									Window.alert("goto quote element: " + elm);
									if (elm != null) {
										DOM.scrollIntoView(elm);
									}
								}
							}
						}));
					}
				});
			}
			header.insertButton(btnQlink, 0);
		} else {
			if (btnQlink != null) {
				header.removeButton(btnQlink);
			}
		}
	}

	public B getParentQuoteBundleWidget() {
		return parentQuoteBundleWidget;
	}

	public void setParentQuoteBundleWidget(B parentQuoteBundleWidget) {
		if (parentQuoteBundleWidget == null)
			throw new NullPointerException();
		this.parentQuoteBundleWidget = parentQuoteBundleWidget;
	}

	public final B getParentBundleWidget() {
		return parentQuoteBundleWidget;
	}

	public final Widget getDragHandle() {
		return header.dragHandle;
	}

	@Override
	public final Quote getModel() {
		return quote;
	}

	@Override
	public void setModel(Quote quote) {
		if (quote == null || quote.getDocument() == null)
			throw new IllegalArgumentException();
		this.quote = quote;

		QuoteInfo quoteInfo = new QuoteInfo(quote);
		header.setQuoteTitle(quoteInfo.getTitle());
		header.setSubTitle(quoteInfo.getSubTitle());
		quoteBlock.setQuotedText(quoteInfo.getQuote());
	}

	public final HandlerRegistration addQuoteHandler(IQuoteHandler handler) {
		return addHandler(handler, QuoteEvent.TYPE);
	}

	public void onResize(ResizeEvent resize) {
		this.header.onResize(resize);
	}

	protected String getXTitle() {
		return "Remove quote?";
	}

	protected boolean allowXClick() {
		return true;
	}

	protected void handleXClick() {
		// orphan the quote (move it to un-assigned bundle)
		parentQuoteBundleWidget.removeQuote(quote, true, true);
	}
}

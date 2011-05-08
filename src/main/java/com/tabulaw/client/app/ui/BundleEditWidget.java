/**
 * Copyright (C) Tabulaw, Inc. 2009-2010 All Rights Reserved
 * @author jpk
 * @since Feb 28, 2010
 */
package com.tabulaw.client.app.ui;

import java.util.List;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasResizeHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PushButton;
import com.tabulaw.client.app.Poc;
import com.tabulaw.client.app.Resources;
import com.tabulaw.client.app.model.ClientModelCache;
import com.tabulaw.client.app.model.ServerPersistApi;
import com.tabulaw.client.model.ModelChangeEvent;
import com.tabulaw.client.model.ModelChangeEvent.ModelChangeOp;
import com.tabulaw.client.ui.Notifier;
import com.tabulaw.model.Quote;
import com.tabulaw.model.QuoteBundle;
import com.tabulaw.model.UserState;

/**
 * Supports editing capability for a quote bundle.
 * @author jpk
 */
public class BundleEditWidget extends AbstractBundleWidget<BundleEditWidget, QuoteEditWidget, BundleEditWidget.EditHeader> {

	public static final String DOCX_MIME_TYPE = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
	public static final String RTF_MIME_TYPE = "text/rtf";
	public static final String DOC_MIME_TYPE = "application/msword";

	static class Styles {

		public static final String BUTTONS = "qbbuttons";

		public static final String DELETE = "delete";

		public static final String CURRENT = "current";

		public static final String X = "x";

		public static final String EMAIL = "email";

		// public static final String CONVERT = "convert"; //For the icon to
		// convert to .doc, etc. Elsewhere msword()

		public static final String QB_CURRENT = "qbcurrent";
	} // Styles

	/**
	 * Quote bundle header widget with edit buttons.
	 * @author jpk
	 */
	static class EditHeader extends EditableBundleHeader {

		private final Image delete, current, notCurrent, close;

		private final PushButton email;
		private final PushButton emailInProgress;
		private final ShareLabel  shareLabel; 

		private class DownloadBundleCommand implements Command {

			private String mimeType;

			public DownloadBundleCommand(String mimeType) {
				this.mimeType = mimeType;
			}

			@Override
			public void execute() {
				// setLocation(String.format(URL_TEMPLATE, mimeType, id));
				if(bundle != null) {
					setLocation("quotebundledownload?mimeType=" + mimeType + "&bundleid=" + bundle.getId());
				}
			}

			private final native void setLocation(String url) /*-{
				$wnd.location.href = url;
			}-*/;
		}

		/**
		 * Constructor
		 * @param orphan is this header for an orphaned quote container instance?
		 */
		public EditHeader() {
			super();
			HTML spacer = new HTML("&nbsp;", true);
			buttons.add(spacer);
			buttons.setCellWidth(spacer, "100%");

			delete = getDeleteImage();

			buttons.add(delete);
			
			notCurrent = getNonCurrentImage();
			current = getCurrentImage();

			buttons.add(current);
			buttons.add(notCurrent);


			buttons.add(getDownloadMenuBar());

			email =
					new PushButton(new Image(Resources.INSTANCE.IconEnvelope()), new Image(
							Resources.INSTANCE.IconEnvelopePressed()));
			email.setTitle("Email");
			email.setStyleName(Styles.EMAIL);

			emailInProgress = new PushButton(new Image(Resources.INSTANCE.AjaxLoader()));
			emailInProgress.setVisible(false);
			emailInProgress.setTitle("Email");
			emailInProgress.setStyleName(Styles.EMAIL);

			buttons.add(email);
			buttons.add(emailInProgress);

			close = new Image(Resources.INSTANCE.XButton());
			close.setTitle("Close");
			close.setStyleName(Styles.X);
			buttons.add(close);
			shareLabel = new ShareLabel();	

			buttons.add(shareLabel);

		}

		@Override
		public void setModel(QuoteBundle bundle) {
			super.setModel(bundle);
			modelStateCheck();
		}

		/**
		 * bundle header level model state check method.
		 */
		private void modelStateCheck() {
			checkCurrentQuoteBundle();
			checkAllQuoteBundle();
			shareLabel.setBundle(bundle);
		}
		private void checkCurrentQuoteBundle() {
			QuoteBundle cqb = ClientModelCache.get().getCurrentQuoteBundle();
			boolean isCurrent = cqb != null && cqb.equals(bundle);
			// close.setVisible(!isCurrent);
			
			if(isCurrent) {
				setLabelText("Current Bundle");
				addStyleName(Styles.QB_CURRENT);
			}
			else {
				setLabelText("Quote Bundle");
				removeStyleName(Styles.QB_CURRENT);
			}
			
			if(current != null) current.setVisible(isCurrent);
			if(notCurrent != null) notCurrent.setVisible(!isCurrent);
		}
		private void checkAllQuoteBundle() {
			QuoteBundle aqb = ClientModelCache.get().getAllQuoteBundle();
			boolean isAll = aqb != null && aqb.equals(bundle);

			if (isAll) {
				current.setVisible(false);
				notCurrent.setVisible(false);
			}
			delete.setVisible(!isAll);
			shareLabel.setVisible(!isAll);
			pName.setEditable(!isAll);
			pDesc.setEditable(!isAll);
			
			if(isAll) {
				addStyleName("orphaned");
			} else {
				removeStyleName("orphaned");
			}
			
			
			
		}
		private Image getDeleteImage() {
			Image image = new Image(Resources.INSTANCE.trash());
			image.setTitle("Remove Quote Bundle...");
			image.setStyleName(Styles.DELETE);
			image.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					if(Window.confirm("Remove '" + bundle.descriptor() + "'?")) {

						// client side (moved quote to orphaned container
						// bundle then remove bundle)
						List<Quote> quotes = bundle.getQuotes();
						ClientModelCache.get().remove(bundle.getModelKey(), EditHeader.this);

						// server side (move quotes to orphaned quotes
						// container)
						ServerPersistApi.get().deleteBundle(bundle.getId(), false);
					}
				}
			});
			return image;
		}
		private Image getCurrentImage() {
			Image image = new Image(Resources.INSTANCE.Star());
			image.setTitle("Current Quote Bundle");
			image.setStyleName(Styles.CURRENT);
			image.setVisible(false);
			return image;
		}
		private Image getNonCurrentImage() {
			Image image = new Image(Resources.INSTANCE.StarPressed());
			image.setTitle("Set as current");
			image.setStyleName(Styles.CURRENT);
			image.setVisible(false);
			image.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					UserState userState = ClientModelCache.get().getUserState();
					if(userState.setCurrentQuoteBundleId(bundle.getId())) {
						ServerPersistApi.get().saveUserState(new Command(){

							@Override
							public void execute() {
								Notifier.get().info("Current Quote Bundle set.");
								// we need to globally notify all views of the
								// current quote bundle
								// change and we do it by firing a model change
								// event
								Poc.fireModelChangeEvent(new ModelChangeEvent(EditHeader.this, ModelChangeOp.UPDATED, bundle, null));
							}
							
						});
					}
				}
			});
			return (image);
		}
		private MenuBar getDownloadMenuBar() {
			MenuBar downloadMenuTop = new MenuBar();

			MenuBar downloadMenu = new MenuBar(true);

			downloadMenuTop.addItem("<img src='poc/images/word-16.gif'/>", true, downloadMenu);

			MenuItem fireRtf = new MenuItem("rtf format", new DownloadBundleCommand(RTF_MIME_TYPE));
			MenuItem fireDocx = new MenuItem("docx format", new DownloadBundleCommand(DOCX_MIME_TYPE));
			MenuItem fireDoc = new MenuItem("doc format", new DownloadBundleCommand(DOC_MIME_TYPE));

			downloadMenu.addItem(fireRtf);
			downloadMenu.addItem(fireDocx);
			downloadMenu.addItem(fireDoc);
			
			downloadMenuTop.setStyleName(null);
			
			return downloadMenuTop;
			
		}

	} // EditHeader

	private final HasResizeHandlers resizeHandlerManager;
	private HandlerRegistration searchHandlerRegistration;
	protected final FlowPanel panel = new FlowPanel();

	/**
	 * Constructor
	 * @param dragController optional
	 * @param orphanedQuoteContainer
	 */
	public BundleEditWidget(PickupDragController dragController, HasResizeHandlers resizeHandlerManager) {
		super();
		this.header = new EditHeader();
		panel.setStyleName("qbundle");
		panel.add(header);
		panel.add(quotePanel);
		initWidget(panel);

		this.resizeHandlerManager = resizeHandlerManager;

		setDragController(dragController);
	}

	public void setCloseHandler(ClickHandler closeHandler) {
		header.close.addClickHandler(closeHandler);
	}

	public void setEmailHandler(ClickHandler emailHandler) {
		header.email.addClickHandler(emailHandler);
	}

	public void setEmailInProgress(boolean inprogress) {
		header.email.setVisible(!inprogress);
		header.emailInProgress.setVisible(inprogress);
	}

	@Override
	public QuoteEditWidget removeQuote(Quote mQuote, boolean removeFromModel, boolean persist) {
		QuoteEditWidget w = super.removeQuote(mQuote, removeFromModel, persist);
		if(w != null) dropAreaCheck();
		return w;
	}

	@Override
	protected QuoteEditWidget getNewQuoteWidget(Quote mQuote) {
		QuoteEditWidget w = new QuoteEditWidget(this, mQuote);

		if(resizeHandlerManager != null) {
			resizeHandlerManager.addResizeHandler(w);
		}
		return w;
	}

	@Override
	public QuoteEditWidget addQuote(Quote mQuote, boolean persist, boolean addToThisBundleModel) {
		QuoteEditWidget w = super.addQuote(mQuote, persist, addToThisBundleModel);
		dropAreaCheck();
		return w;
	}

	public void registerSearchHandler() {
		searchHandlerRegistration = Poc.getPortal().addSearchHandler(this);
	}

	public void unRegisterSearchHandler() {
		searchHandlerRegistration.removeHandler();
	}

	private void dropAreaCheck() {
		// maintain a drop area
		if(quotePanel.getWidgetCount() == 0) {
			quotePanel.getElement().getStyle().setHeight(50, Unit.PX);
		}
		else {
			quotePanel.getElement().getStyle().clearHeight();
		}
	}

	/**
	 * Sets relevant state based on the current quote bundle and the current
	 * bundle model.
	 */
	private void modelStateCheck() {
		header.modelStateCheck();
		QuoteBundle cqb = ClientModelCache.get().getCurrentQuoteBundle();
		boolean isCurrent = cqb != null && cqb.equals(bundle);
		if(isCurrent) {
			// i.e. quotes-current
			quotePanel.addStyleDependentName("current");
		}
		else {
			quotePanel.removeStyleDependentName("current");
		}
		// only show the goto highlight link for the current quote bundle
		for(QuoteEditWidget qw : getQuoteWidgets()) {
			qw.showQuoteLinkButton(isCurrent);
		}
	}

	@Override
	protected void onLoad() {
		super.onLoad();
		modelStateCheck();
		dropAreaCheck();
		makeModelChangeAware();
	}

	@Override
	protected void onUnload() {
		super.onUnload();
		unmakeModelChangeAware();
	}

	@Override
	public void onModelChangeEvent(ModelChangeEvent event) {
		super.onModelChangeEvent(event);
		modelStateCheck();
	}
}

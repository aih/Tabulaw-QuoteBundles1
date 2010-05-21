/**
 * The Logic Lab
 * @author jpk
 * @since Feb 14, 2010
 */
package com.tabulaw.client.app.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.DragEndEvent;
import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.allen_sauer.gwt.dnd.client.drop.FlowPanelDropController;
import com.allen_sauer.gwt.dnd.client.drop.HorizontalPanelDropController;
import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.tabulaw.client.app.model.ClientModelCache;
import com.tabulaw.client.app.model.ServerPersistApi;
import com.tabulaw.client.app.ui.BundleListingWidget.BOption;
import com.tabulaw.client.model.ModelChangeEvent;
import com.tabulaw.client.ui.AbstractModelChangeAwareWidget;
import com.tabulaw.client.ui.LoggingDragHandler;
import com.tabulaw.client.ui.Notifier;
import com.tabulaw.common.model.EntityType;
import com.tabulaw.common.model.IEntity;
import com.tabulaw.common.model.ModelKey;
import com.tabulaw.common.model.Quote;
import com.tabulaw.common.model.QuoteBundle;

/**
 * Manages the editing of quote bundles via drag and drop.
 * @author jpk
 */
public class BundlesManageWidget extends AbstractModelChangeAwareWidget {

	static class Styles {

		/**
		 * Style applied to the horizontal panel containing the quote bundle
		 * widgets.
		 */
		public static final String COLUMNS = "columns";

		/**
		 * Absolute panel style bounding the dragging area.
		 */
		public static final String BOUNDARY_AREA = "boundaryArea";
	} // Styles

	static final int NUM_COLUMNS = 3;

	static enum QbColStyle {
		one,
		two,
		three;

		public static QbColStyle resolveFromNumBundles(int numBundles) {
			switch(numBundles) {
				case 1:
					return one;
				case 2:
					return two;
				default:
				case 3:
					return three;
			}
		}
	}

	class QuoteBundleDragHandler extends LoggingDragHandler {

		public QuoteBundleDragHandler() {
			super("QuoteBundle");
		}
	} // QuoteBundleDragHandler

	/**
	 * Drag handler for dragging quote widgets.
	 * @author jpk
	 */
	class QuoteDragHandler extends LoggingDragHandler {

		public QuoteDragHandler() {
			super("Quote");
		}

		@Override
		public void onPreviewDragEnd(DragEndEvent event) throws VetoDragException {
			super.onPreviewDragEnd(event);

			// move the underying model now
			DragContext context = event.getContext();

			QuoteEditWidget sourceQuoteWidget = (QuoteEditWidget) context.draggable;
			BundleEditWidget sourceBundleWidget = sourceQuoteWidget.getParentBundleWidget();

			// identify the target quote bundle widget (recipient of draggable)
			final BundleEditWidget targetBundleWidget;
			try {
				targetBundleWidget =
						(BundleEditWidget) context.finalDropController.getDropTarget().getParent().getParent();
			}
			// CRITICAL: we must catch a Throwable as opposed to a
			// NullPointerException in order
			// for this to work in webmode!!!
			catch(Throwable t) {
				// presume the drop target is the same as the source!
				// return;
				throw new VetoDragException();
			}
			if(targetBundleWidget == sourceBundleWidget) {
				// return;
				throw new VetoDragException();
			}

			Quote sourceQuote = sourceQuoteWidget.getModel();

			String dscQuote = sourceQuote.getDocument().getTitle();
			String dscBundle = targetBundleWidget.getModel().getName();

			// does the quote already exist in the target bundle?
			if(targetBundleWidget.hasQuote(sourceQuote)) {
				String msg = "'" + dscQuote + "' is already in Quote Bundle: " + dscBundle;
				Notifier.get().warn(msg);
				throw new VetoDragException();
			}

			// are we dragging from orphaned quotes container?
			if(sourceBundleWidget.isOrphanedQuoteContainer()) {
				
				QuoteBundle targetBundle = targetBundleWidget.getModel();
				
				// move the source quote to target bundle
				if(!sourceBundleWidget.getModel().removeQuote(sourceQuote))
					throw new IllegalStateException();
				targetBundle.addQuote(sourceQuote);
				
				//if(!ClientModelCache.get().getOrphanedQuoteContainer().removeQuote(sourceQuote))
					//throw new IllegalStateException();
				
				// client persist target bundle w/ notification
				ClientModelCache.get().persist(targetBundle, targetBundleWidget);
				
				// server-side persist
				ServerPersistApi.get().unorphanQuote(sourceQuote.getId(), targetBundle.getId());
				
				return;
			}

			// clone the dragged quote widget setting its id to a new one
			Quote mQuoteClone = (Quote) sourceQuote.clone();
			mQuoteClone.setId(ClientModelCache.get().getNextId(EntityType.QUOTE.name()));
			mQuoteClone.setVersion(-1); // CRITICAL
			// add and persist
			targetBundleWidget.addQuote(mQuoteClone, true, true);
			// deny since we are copying
			throw new VetoDragException();
			// String msg = "'" + dscQuote + "' copied to Quote Bundle: " + dscBundle;
			// Notifier.get().info(msg);
		}
	}

	/**
	 * The life-cycle of this widget is managed by this class!
	 */
	private final BundleListingWidget qbListingWidget;

	/**
	 * Contains the quote bundle columns.
	 */
	private final AbsolutePanel boundaryPanel = new AbsolutePanel();

	/**
	 * Each column corres. to a quote bundle
	 */
	private final HorizontalPanel columns = new HorizontalPanel();

	// quote bundle widget dragging (main viewing area to nav col)
	private final PickupDragController bundleController;
	private final QuoteBundleDragHandler bundleHandler;
	private final HorizontalPanelDropController bundleDropController;

	// quote widget dragging (from one bundle widget to another bundle widget in
	// main viewing area only)
	private final PickupDragController quoteController;
	private final QuoteDragHandler quoteHandler;

	private final HashMap<BundleEditWidget, FlowPanelDropController> qbDropBindings =
			new HashMap<BundleEditWidget, FlowPanelDropController>();

	/**
	 * Constructor
	 */
	public BundlesManageWidget() {
		super();

		boundaryPanel.addStyleName(Styles.BOUNDARY_AREA);
		initWidget(boundaryPanel);

		qbListingWidget = new BundleListingWidget();

		// initialize quote bundle dragging
		bundleController = new PickupDragController(boundaryPanel, false);
		bundleController.setBehaviorMultipleSelection(false);
		bundleController.setBehaviorDragStartSensitivity(3);
		bundleHandler = new QuoteBundleDragHandler();
		bundleController.addDragHandler(bundleHandler);
		bundleDropController = new HorizontalPanelDropController(columns);
		bundleController.registerDropController(bundleDropController);

		// initialize quote dragging
		quoteController = new PickupDragController(boundaryPanel, false);
		quoteController.setBehaviorMultipleSelection(false);
		quoteController.setBehaviorDragStartSensitivity(3);
		quoteHandler = new QuoteDragHandler();
		quoteController.addDragHandler(quoteHandler);

		// initialize horizontal panel to hold colums of quote bundle widgets
		columns.addStyleName(Styles.COLUMNS);
		columns.setSpacing(4);
		boundaryPanel.add(columns);
	}

	/**
	 * Pins a quote bundle currently listed in the nav col onto the main viewing
	 * area.
	 * @param bundleId The id of the bundle to pin
	 */
	void pinQuoteBundle(String bundleId) {
		// see if option is in bundle list widget
		BOption[] unpinnedOptions = qbListingWidget.getOptionsPanel().getOptions();
		for(BOption option : unpinnedOptions) {
			if(option.bundleId.equals(bundleId)) {
				pinQuoteBundle(option);
			}
		}
	}

	/**
	 * Pins the bundle option to the main viewing area and assumes it is no longer
	 * listed in the qb listing widget.
	 * @param option
	 */
	void pinQuoteBundle(BOption option) {
		option.removeFromParent();
		String qbId = option.getBundleId();
		// String qbName = option.getBundleName();

		// replace just dropped option with quote bundle widget
		ModelKey key = new ModelKey(EntityType.QUOTE_BUNDLE.name(), qbId);
		QuoteBundle bundle = (QuoteBundle) ClientModelCache.get().get(key);

		insertQuoteBundleColumn(bundle, 0);
	}

	/**
	 * Removes the quote bundle widget from the main viewing area placing a
	 * corres. entry back in the quote bundle listing widget.
	 * @param quoteBundleWidget The quote bundle widget to close
	 */
	void unpinQuoteBundle(BundleEditWidget quoteBundleWidget) {
		// remove from main viewing area
		removeQuoteBundleColumn(quoteBundleWidget.getModel());

		// add to nav col
		QuoteBundle bundle = quoteBundleWidget.getModel();
		addBundleOption(bundle);
	}

	/**
	 * @return The <em>managed</em> quote bundle listing widget.
	 */
	public BundleListingWidget getQuoteBundleListingWidget() {
		return qbListingWidget;
	}

	@SuppressWarnings("unchecked")
	public void refresh() {
		clearBundleOptions();
		clearQuoteBundleColumns();

		// populate
		List<QuoteBundle> mbundles = (List<QuoteBundle>) ClientModelCache.get().getAll(EntityType.QUOTE_BUNDLE);
		if(mbundles != null) {
			for(int i = 0; i < mbundles.size(); i++) {
				QuoteBundle qb = mbundles.get(i);
				boolean isOrphanedBundle = ClientModelCache.get().getOrphanedQuoteContainer().equals(qb);
				if(!isOrphanedBundle && i < NUM_COLUMNS) {
					insertQuoteBundleColumn(qb, columns.getWidgetCount());
				}
				else {
					addBundleOption(qb);
				}
			}
		}
	}

	private void clearBundleOptions() {
		/*
		BOption[] currentOptions = qbListingWidget.getOptionsPanel().getOptions();
		for(BOption option : currentOptions) {
			bundleOptionController.makeNotDraggable(option);
		}
		*/
		qbListingWidget.getOptionsPanel().clearOptions();
	}

	private void clearQuoteBundleColumns() {
		ArrayList<BundleEditWidget> qbwlist = new ArrayList<BundleEditWidget>();
		for(int i = 0; i < columns.getWidgetCount(); i++) {
			BundleEditWidget qbw = (BundleEditWidget) columns.getWidget(i);
			qbwlist.add(qbw);
		}
		for(BundleEditWidget qbw : qbwlist) {
			removeQuoteBundleColumn(qbw.getModel());
		}
	}

	/**
	 * Adds a bundle option to the bundle listing only not affecting the quote
	 * bundle columns in the main viewing area.
	 * @param bundle
	 * @return new added option
	 */
	private BOption addBundleOption(QuoteBundle bundle) {
		BOption option = new BOption(bundle.getId(), bundle.getName());
		option.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				pinQuoteBundle((BOption) event.getSource());
			}
		});
		qbListingWidget.getOptionsPanel().addOption(option);
		// bundleOptionController.makeDraggable(option);
		return option;
	}

	private BOption removeBundleOption(QuoteBundle qb) {
		for(BOption option : qbListingWidget.getOptionsPanel().getOptions()) {
			if(option.bundleId.equals(qb.getId())) {
				qbListingWidget.getOptionsPanel().removeOption(option);
				return option;
			}
		}
		return null;
	}

	/**
	 * Inserts a quote bundle to the main viewing area only at the given column
	 * index. The bundle option listing is un-affected.
	 * @param bundle
	 */
	private void insertQuoteBundleColumn(final QuoteBundle bundle, int index) {

		// determine number of existing qb widgets
		int numBundlesBeforeInsert = 0;
		int ilast = -1;
		for(int i = 0; i < columns.getWidgetCount(); i++) {
			Widget w = columns.getWidget(i);
			if(w instanceof BundleEditWidget) {
				numBundlesBeforeInsert++;
				ilast = i;
			}
		}

		// "demote" the last quote bundle in the main viewing area
		if(numBundlesBeforeInsert >= NUM_COLUMNS) {
			BundleEditWidget last = (BundleEditWidget) columns.getWidget(ilast);
			unpinQuoteBundle(last);
		}

		Log.debug("Inserting quote bundle col widget for: " + bundle);
		final BundleEditWidget qbw = new BundleEditWidget(quoteController);
		qbw.setModel(bundle);
		qbw.setCloseHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				unpinQuoteBundle(qbw);
			}
		});
		columns.insert(qbw, index);

		int numBundles = numBundlesBeforeInsert + 1;
		QbColStyle colStyleToApply = QbColStyle.resolveFromNumBundles(numBundles);
		for(int i = 0; i < columns.getWidgetCount(); i++) {
			Widget w = columns.getWidget(i);

			if(w instanceof BundleEditWidget) {
				BundleEditWidget iqbw = (BundleEditWidget) w;

				// [re-]apply num cols based style to each bundle widget
				for(QbColStyle colStyle : QbColStyle.values()) {
					iqbw.removeStyleName(colStyle.name());
				}
				iqbw.addStyleName(colStyleToApply.name());

				// make quote bundle widget draggable
				if(numBundles == 2) {
					bundleController.makeDraggable(iqbw, iqbw.header.getDraggable());
				}
			}
		}

		if(numBundles > 2) {
			// only make added qb widget draggable and assume existing one are already
			bundleController.makeDraggable(qbw, qbw.header.getDraggable());
		}

		// initialize a quote drop controller for this quote bundle widget
		FlowPanelDropController quoteDropController = new FlowPanelDropController(qbw.quotePanel);
		quoteController.registerDropController(quoteDropController);
		qbDropBindings.put(qbw, quoteDropController);

		// make quote widgets draggable
		List<QuoteEditWidget> quoteWidgets = qbw.getQuoteWidgets();
		for(QuoteEditWidget qw : quoteWidgets) {
			quoteController.makeDraggable(qw, qw.getDragHandle());
		}
	}

	private boolean removeQuoteBundleColumn(QuoteBundle bundle) {
		BundleEditWidget removedQbw = null;
		int numBundles = 0;

		// identify the quote bundle widget
		String rmId = bundle.getId();

		for(int i = 0; i < columns.getWidgetCount(); i++) {
			Widget w = columns.getWidget(i);
			if(w instanceof BundleEditWidget) {
				BundleEditWidget qbw = (BundleEditWidget) w;
				if(qbw.getModel().getId().equals(rmId)) {

					// un-make quote widgets draggable
					List<QuoteEditWidget> quoteWidgets = qbw.getQuoteWidgets();
					for(QuoteEditWidget qw : quoteWidgets) {
						quoteController.makeNotDraggable(qw);
					}

					removedQbw = qbw;
				}
				else {
					numBundles++;
				}
			}
		}

		if(removedQbw != null) {
			if(numBundles >= 1) bundleController.makeNotDraggable(removedQbw);
			FlowPanelDropController quoteDropController = qbDropBindings.remove(removedQbw);
			quoteController.unregisterDropController(quoteDropController);
			columns.remove(removedQbw);

			QbColStyle colStyleToApply = QbColStyle.resolveFromNumBundles(numBundles);
			for(int i = 0; i < columns.getWidgetCount(); i++) {
				Widget w = columns.getWidget(i);
				if(w instanceof BundleEditWidget) {
					BundleEditWidget iqbw = (BundleEditWidget) w;

					// [re-]apply num cols based style to each bundle widget
					for(QbColStyle colStyle : QbColStyle.values()) {
						iqbw.removeStyleName(colStyle.name());
					}
					iqbw.addStyleName(colStyleToApply.name());

					// remove dragging if necessary
					if(numBundles < 2) bundleController.makeNotDraggable(iqbw);
				}
			}
		}

		return removedQbw != null;
	}

	@Override
	public void onModelChangeEvent(ModelChangeEvent event) {
		super.onModelChangeEvent(event);
		IEntity m = event.getModel();
		EntityType et = EntityType.fromString(event.getModelKey().getEntityType());
		if(et == EntityType.QUOTE_BUNDLE) {
			QuoteBundle qb = (QuoteBundle) m;
			switch(event.getChangeOp()) {
				case UPDATED: {
					String id = m.getId();
					// look for the quote bundle in the pinned quote bundle widgets
					for(int i = 0; i < columns.getWidgetCount(); i++) {
						BundleEditWidget qbw = (BundleEditWidget) columns.getWidget(i);
						QuoteBundle qbm = qbw.getModel();
						if(qbm.getId().equals(id)) {
							qbw.sync(qb);
						}
					}
					break;
				}
				case ADDED:
					insertQuoteBundleColumn(qb, 0);
					break;
				case DELETED:
					if(removeBundleOption(qb) == null) removeQuoteBundleColumn(qb);
					break;
			}
		}
	}
}

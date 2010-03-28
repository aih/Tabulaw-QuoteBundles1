/**
 * The Logic Lab
 * @author jpk
 * @since Feb 14, 2010
 */
package com.tll.tabulaw.client.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.DragEndEvent;
import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.allen_sauer.gwt.dnd.client.drop.HorizontalPanelDropController;
import com.allen_sauer.gwt.dnd.client.drop.VerticalPanelDropController;
import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.tll.client.model.ModelChangeEvent;
import com.tll.client.model.ModelChangeEvent.ModelChangeOp;
import com.tll.common.model.CopyCriteria;
import com.tll.common.model.Model;
import com.tll.common.model.ModelKey;
import com.tll.tabulaw.client.model.PocModelCache;
import com.tll.tabulaw.client.ui.QuoteBundleListingWidget.BOption;
import com.tll.tabulaw.common.model.PocEntityType;

/**
 * Manages the editing of quote bundles via drag and drop.
 * @author jpk
 */
public class QuoteBundlesManageWidget extends AbstractModelChangeAwareWidget {

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

	class BundleOptionDragHandler extends LoggingDragHandler {

		public BundleOptionDragHandler() {
			super("Quote Bundle Option");
		}

		@Override
		public void onDragEnd(DragEndEvent event) {
			super.onDragEnd(event);

			// "demote" the last quote bundle in the main viewing area to the bundle
			// listing
			int numBundles = columns.getWidgetCount();
			if(numBundles >= NUM_COLUMNS) {
				// need to iterate since drag and drop adds place holder widgets
				int ilast = -1;
				for(int i = 0; i < columns.getWidgetCount(); i++) {
					Widget w = columns.getWidget(i);
					if(w instanceof QuoteBundleEditWidget) {
						ilast = i;
					}
				}
				QuoteBundleEditWidget last = (QuoteBundleEditWidget) columns.getWidget(ilast);
				unpinQuoteBundle(last);
			}

			// convert the draggable to a QuoteBundleWidget
			DragContext context = event.getContext();
			pinQuoteBundle((BOption) context.draggable);
		}
	} // BundleOptionDragHandler

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

			QuoteEditWidget draggedQuoteWidget = (QuoteEditWidget) context.draggable;
			QuoteBundleEditWidget sourceQuoteBundleWidget =
					(QuoteBundleEditWidget) draggedQuoteWidget.getParentQuoteBundleWidget();

			// identify the target quote bundle widget (recipient of draggable)
			QuoteBundleEditWidget targetQuoteBundleWidget;
			try {
				targetQuoteBundleWidget = (QuoteBundleEditWidget) context.finalDropController.getDropTarget().getParent();
			}
			catch(NullPointerException e) {
				// presume the drop target is the same as the source!
				return;
			}
			if(targetQuoteBundleWidget == sourceQuoteBundleWidget) {
				return;
			}

			Model draggedQuoteModel = draggedQuoteWidget.getModel();
			// ModelKey draggedQuoteModelKey = draggedQuoteModel.getKey();

			String dscQuote = draggedQuoteModel.asString("document.title");
			String dscBundle = targetQuoteBundleWidget.getModel().getName();

			// does the quote already exist in the target bundle?
			if(targetQuoteBundleWidget.hasQuote(draggedQuoteModel)) {
				String msg = "'" + dscQuote + "' is already in Quote Bundle: " + dscBundle;
				Notifier.get().warn(msg);
				throw new VetoDragException();
			}

			// clone the dragged quote widget
			Model mQuoteClone = draggedQuoteModel.copy(CopyCriteria.keepReferences());
			mQuoteClone.setId(PocModelCache.get().getNextId(PocEntityType.QUOTE));
			targetQuoteBundleWidget.addQuote(mQuoteClone, true);

			//String msg = "'" + dscQuote + "' copied to Quote Bundle: " + dscBundle;
			//Notifier.get().info(msg);

			// deny since we are copying
			throw new VetoDragException();
		}
	}

	/**
	 * The life-cycle of this widget is managed by this class!
	 */
	private final QuoteBundleListingWidget qbListingWidget;

	/**
	 * Contains the quote bundle columns.
	 */
	private final AbsolutePanel boundaryPanel = new AbsolutePanel();

	/**
	 * Each column corres. to a quote bundle
	 */
	private final HorizontalPanel columns = new HorizontalPanel();

	// bundle option (nav col) dragging (nav col to main view area)
	private final PickupDragController bundleOptionController;
	private final BundleOptionDragHandler bundleOptionHandler;
	private final HorizontalPanelDropController bundleOptionDropController;

	// quote bundle widget dragging (main viewing area to nav col)
	private final PickupDragController quoteBundleController;
	private final QuoteBundleDragHandler quoteBundleHandler;
	private final HorizontalPanelDropController quoteBundleDropController;

	// quote widget dragging (from one bundle widget to another bundle widget in
	// main viewing area only)
	private final PickupDragController quoteController;
	private final QuoteDragHandler quoteHandler;

	private final HashMap<QuoteBundleEditWidget, VerticalPanelDropController> qbDropBindings =
			new HashMap<QuoteBundleEditWidget, VerticalPanelDropController>();

	/**
	 * Constructor
	 */
	public QuoteBundlesManageWidget() {
		super();

		boundaryPanel.addStyleName(Styles.BOUNDARY_AREA);
		initWidget(boundaryPanel);

		qbListingWidget = new QuoteBundleListingWidget();

		// init bundle option dragging
		bundleOptionController = new PickupDragController(RootPanel.get(), false);
		bundleOptionController.setBehaviorMultipleSelection(false);
		bundleOptionHandler = new BundleOptionDragHandler();
		bundleOptionController.addDragHandler(bundleOptionHandler);
		bundleOptionDropController = new HorizontalPanelDropController(columns);
		bundleOptionController.registerDropController(bundleOptionDropController);

		// initialize quote bundle dragging
		quoteBundleController = new PickupDragController(boundaryPanel, false);
		quoteBundleController.setBehaviorMultipleSelection(false);
		quoteBundleHandler = new QuoteBundleDragHandler();
		quoteBundleController.addDragHandler(quoteBundleHandler);
		quoteBundleDropController = new HorizontalPanelDropController(columns);
		quoteBundleController.registerDropController(quoteBundleDropController);

		// initialize quote dragging
		quoteController = new PickupDragController(boundaryPanel, false);
		quoteController.setBehaviorMultipleSelection(false);
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
		String qbName = option.getBundleName();

		// replace just dropped option with quote bundle widget
		Model mQuoteBundle = PocModelCache.get().get(new ModelKey(PocEntityType.QUOTE_BUNDLE, qbId, qbName));

		insertQuoteBundleColumn(mQuoteBundle, 0);
	}

	/**
	 * Removes the quote bundle widget from the main viewing area placing a
	 * corres. entry back in the quote bundle listing widget.
	 * @param quoteBundleWidget The quote bundle widget to close
	 */
	void unpinQuoteBundle(QuoteBundleEditWidget quoteBundleWidget) {
		// unregister quote drop controller for this quote bundle widget
		VerticalPanelDropController quoteDropController = qbDropBindings.remove(quoteBundleWidget);
		quoteController.unregisterDropController(quoteDropController);

		// remove just added quote bundle widget and replace with bundle option to
		// options panel
		quoteBundleWidget.removeFromParent();
		Model mQuoteBundle = quoteBundleWidget.getModel();
		addQuoteBundleOption(mQuoteBundle);

		//String msg = quoteBundleWidget.getModel().descriptor() + " closed.";
		//Notifier.get().info(msg);
	}

	/**
	 * @return The <em>managed</em> quote bundle listing widget.
	 */
	public QuoteBundleListingWidget getQuoteBundleListingWidget() {
		return qbListingWidget;
	}

	public void refresh() {
		clearBundleOptions();
		clearQuoteBundleColumns();

		// populate
		List<Model> mbundles = PocModelCache.get().getAll(PocEntityType.QUOTE_BUNDLE);
		if(mbundles != null) {
			for(int i = 0; i < mbundles.size(); i++) {
				if(i < NUM_COLUMNS) {
					insertQuoteBundleColumn(mbundles.get(i), columns.getWidgetCount());
				}
				else {
					addQuoteBundleOption(mbundles.get(i));
				}
			}
		}
	}

	private void clearBundleOptions() {
		BOption[] currentOptions = qbListingWidget.getOptionsPanel().getOptions();
		for(BOption option : currentOptions) {
			bundleOptionController.makeNotDraggable(option);
		}
		qbListingWidget.getOptionsPanel().clearOptions();
	}

	private void clearQuoteBundleColumns() {
		ArrayList<QuoteBundleEditWidget> qbwlist = new ArrayList<QuoteBundleEditWidget>();
		for(int i = 0; i < columns.getWidgetCount(); i++) {
			QuoteBundleEditWidget qbw = (QuoteBundleEditWidget) columns.getWidget(i);
			qbwlist.add(qbw);
		}
		for(QuoteBundleEditWidget qbw : qbwlist) {
			removeQuoteBundleColumn(qbw.getModel());
		}
	}

	/**
	 * Adds a bundle option to the bundle listing only not affecting the quote
	 * bundle columns in the main viewing area.
	 * @param mQuoteBundle
	 * @return new added option
	 */
	private BOption addQuoteBundleOption(Model mQuoteBundle) {
		BOption option = new BOption(mQuoteBundle.getId(), mQuoteBundle.getName());
		qbListingWidget.getOptionsPanel().addOption(option);
		bundleOptionController.makeDraggable(option);
		return option;
	}

	/**
	 * Inserts a quote bundle to the main viewing area only at the given column index. The bundle option
	 * listing is un-affected.
	 * @param mQuoteBundle
	 */
	private void insertQuoteBundleColumn(final Model mQuoteBundle, int index) {
		Log.debug("Inserting quote bundle col widget for: " + mQuoteBundle);
		final QuoteBundleEditWidget qbw = new QuoteBundleEditWidget(quoteController);
		qbw.setModel(mQuoteBundle);
		qbw.setCloseHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				unpinQuoteBundle(qbw);
			}
		});
		columns.insert(qbw, index);

		// make quote bundle widget draggable to bundle options
		quoteBundleController.makeDraggable(qbw, qbw.header.getHtmlHeader());

		// initialize a quote drop controller for this quote bundle widget
		VerticalPanelDropController quoteDropController = new VerticalPanelDropController(qbw.quotePanel);
		quoteController.registerDropController(quoteDropController);
		qbDropBindings.put(qbw, quoteDropController);

		// add a quote widget for each contained quote in the bundle
		AbstractQuoteWidget[] quoteWidgets = qbw.getQuoteWidgets();
		for(int i = 0; i < quoteWidgets.length; i++) {
			// make quote widget draggable to other quote bundle widgets
			AbstractQuoteWidget qw = quoteWidgets[i];
			quoteController.makeDraggable(qw, qw.getDragHandle());
		}
	}

	private void removeQuoteBundleColumn(Model mQuoteBundle) {
		// identify the quote bundle widget
		ModelKey rmKey = mQuoteBundle.getKey();
		for(int i = 0; i < columns.getWidgetCount(); i++) {
			QuoteBundleEditWidget qbw = (QuoteBundleEditWidget) columns.getWidget(i);
			if(qbw.getModel().getKey().equals(rmKey)) {
				VerticalPanelDropController quoteDropController = qbDropBindings.remove(qbw);
				quoteController.unregisterDropController(quoteDropController);
				// TODO makeNotDraggable the child quotes too ???
				columns.remove(qbw);
				return;
			}
		}
	}

	@Override
	public void onModelChangeEvent(ModelChangeEvent event) {
		super.onModelChangeEvent(event);
		Model m = event.getModel();
		if(m.getKey().getEntityType() == PocEntityType.QUOTE_BUNDLE) {
			if(event.getChangeOp() == ModelChangeOp.UPDATED) {
				ModelKey key = m.getKey();
				// look for the quote bundle in the pinned quote bundle widgets
				for(int i = 0; i < columns.getWidgetCount(); i++) {
					QuoteBundleEditWidget qbw = (QuoteBundleEditWidget) columns.getWidget(i);
					Model qbm = qbw.getModel();
					if(qbm.getKey().equals(key)) {
						qbw.sync(m);
					}
				}
			}
			if(event.getChangeOp() == ModelChangeOp.ADDED) {
				Model mQuoteBundle = event.getModel();
				// add and pin directly
				pinQuoteBundle(addQuoteBundleOption(mQuoteBundle));
				Notifier.get().info("'" + mQuoteBundle.descriptor() + "' added.");
			}
		}
	}
}

/**
 * The Logic Lab
 * @author jpk
 * @since Feb 12, 2010
 */
package com.tll.tabulaw.client.ui.nav;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TabBar;
import com.tll.client.model.ModelChangeEvent;
import com.tll.client.model.ModelChangeEvent.ModelChangeOp;
import com.tll.client.mvc.ViewManager;
import com.tll.client.mvc.view.ShowViewRequest;
import com.tll.client.mvc.view.ViewKey;
import com.tll.common.model.Model;
import com.tll.common.model.ModelKey;
import com.tll.tabulaw.client.Poc;
import com.tll.tabulaw.client.ui.AbstractModelChangeAwareWidget;
import com.tll.tabulaw.client.view.DocumentView;
import com.tll.tabulaw.common.model.PocEntityType;

/**
 * The top nav row.
 * @author jpk
 */
public class NavRowPanel extends AbstractNavPanel {

	static class Styles {

		/**
		 * Style applied to the top most panel.
		 */
		public static final String NAV_ROW = "navRow";

		/**
		 * Style applied to the contained table element.
		 */
		public static final String NAV_ROW_TBL = "navRowTbl";

		/**
		 * Style applied to the main views tab bar widget.
		 */
		public static final String MAIN_VIEWS = "mainViews";

		/**
		 * Style applied to the open docs tab bar widget.
		 */
		public static final String OPEN_DOCS = "openDocs";

		/**
		 * Style applied to the current quote bundle widget.
		 */
		public static final String CRNT_QB = "crntqb";
	} // Styles

	/**
	 * Widget that updates its content from current quote bundle model change
	 * events.
	 * @author jpk
	 */
	public static class CurrentQuoteBundleDisplayWidget extends AbstractModelChangeAwareWidget {

		private final HTML html = new HTML();
		private ModelKey crntQbKey;

		public CurrentQuoteBundleDisplayWidget() {
			super();
			initWidget(html);
		}

		private void update() {
			Model cqb = Poc.getCurrentQuoteBundle();
			if(cqb != null) {
				ModelKey key = cqb.getKey();
				if(!key.equals(crntQbKey)) {
					this.crntQbKey = key;
					html.setHTML("<p><span class=\"echo\">Current Quote Bundle:</span>" + key.getName() + "</p>");
				}
			}
		}

		@Override
		public void onModelChangeEvent(ModelChangeEvent event) {
			super.onModelChangeEvent(event);
			update();
		}

	} // CurrentQuoteBundleDisplayWidget

	private static void showView(ArrayList<? extends AbstractNavButton> list, int index) {
		ViewManager.get().dispatch(new ShowViewRequest(list.get(index).getViewInitializer()));
	}

	private final ArrayList<AbstractNavButton> mainViewButtons = new ArrayList<AbstractNavButton>();

	private final ArrayList<DocumentViewNavButton> openDocNavButtons = new ArrayList<DocumentViewNavButton>();

	private final TabBar mainViewTabs = new TabBar();

	private final TabBar openDocTabs = new TabBar();

	private final CurrentQuoteBundleDisplayWidget crntQuoteBudleWidget = new CurrentQuoteBundleDisplayWidget();

	private final HorizontalPanel hp = new HorizontalPanel();

	private final FlowPanel panel = new FlowPanel();

	private boolean handlingViewChange;

	/**
	 * Constructor
	 */
	public NavRowPanel() {
		super();

		DocumentsNavButton nbDocListing = new DocumentsNavButton();
		QuoteBundlesNavButton nbQuoteBundles = new QuoteBundlesNavButton();

		mainViewButtons.add(nbDocListing);
		mainViewButtons.add(nbQuoteBundles);

		mainViewTabs.addStyleName(Styles.MAIN_VIEWS);
		mainViewTabs.addTab(nbDocListing);
		mainViewTabs.addTab(nbQuoteBundles);

		openDocTabs.addStyleName(Styles.OPEN_DOCS);

		crntQuoteBudleWidget.setStyleName(Styles.CRNT_QB);

		hp.setStyleName(Styles.NAV_ROW_TBL);
		hp.add(mainViewTabs);
		hp.add(openDocTabs);

		panel.setStyleName(Styles.NAV_ROW);
		panel.add(crntQuoteBudleWidget);
		panel.add(hp);

		mainViewTabs.addSelectionHandler(new SelectionHandler<Integer>() {

			@Override
			public void onSelection(SelectionEvent<Integer> event) {
				if(!handlingViewChange) {
					// TODO don't act if this is the current view ??
					showView(mainViewButtons, event.getSelectedItem().intValue());
				}
			}
		});
		openDocTabs.addSelectionHandler(new SelectionHandler<Integer>() {

			@Override
			public void onSelection(SelectionEvent<Integer> event) {
				if(!handlingViewChange) {
					showView(openDocNavButtons, event.getSelectedItem().intValue());
				}
			}
		});

		initWidget(panel);
	}

	@Override
	protected void handleViewChange() {
		handlingViewChange = true;
		int i = 0;
		ViewKey crntViewKey = ViewManager.get().getCurrentViewKey();

		if(crntViewKey.getViewClass() == DocumentView.klas) {
			int index = getTabIndexFromViewKey(crntViewKey, false);
			if(index == -1) {
				// create a doc nav button and tab
				DocumentView dview = (DocumentView) ViewManager.get().resolveView(crntViewKey);
				ModelKey docKey = dview.getDocKey();
				DocumentViewNavButton dnb = new DocumentViewNavButton(docKey);
				openDocNavButtons.add(dnb);
				openDocTabs.addTab(dnb);
				index = openDocTabs.getTabCount() - 1;
			}
			openDocTabs.selectTab(index);
			// unselect main view tabs
			mainViewTabs.selectTab(-1);
		}
		else {
			for(AbstractNavButton navBtn : mainViewButtons) {
				// mainViewTabs.setTabEnabled(i, true);
				ViewKey aViewKey = navBtn.getViewInitializer().getViewKey();
				if(crntViewKey.equals(aViewKey)) {
					mainViewTabs.selectTab(i);
					// mainViewTabs.setTabEnabled(i, false);
					break;
				}
				i++;
			}
			// unselect open docs tabs
			openDocTabs.selectTab(-1);
		}
		handlingViewChange = false;
	}

	private int getTabIndexFromViewKey(ViewKey viewKey, boolean mainView) {
		List<? extends AbstractNavButton> navBtnList = mainView ? mainViewButtons : openDocNavButtons;
		for(int i = 0; i < navBtnList.size(); i++) {
			ViewKey vk = navBtnList.get(i).getViewInitializer().getViewKey();
			if(vk.equals(viewKey)) return i;
		}
		return -1;
	}

	public CurrentQuoteBundleDisplayWidget getCrntQuoteBudleWidget() {
		return crntQuoteBudleWidget;
	}

	@Override
	public void onModelChangeEvent(ModelChangeEvent event) {
		super.onModelChangeEvent(event);
		if(event.getChangeOp() == ModelChangeOp.DELETED && event.getModelKey().getEntityType() == PocEntityType.DOCUMENT) {
			// remove open doc tab
			boolean found = false;
			int i = 0;
			for(DocumentViewNavButton b : openDocNavButtons) {
				if(b.getDocKey().equals(event.getModelKey())) {
					found = true;
					break;
				}
				i++;
			}
			if(found) {
				openDocTabs.removeTab(i);
				openDocNavButtons.remove(i);
			}
		}
		else {
			crntQuoteBudleWidget.onModelChangeEvent(event);
		}
	}
}

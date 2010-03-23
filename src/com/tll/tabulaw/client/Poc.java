package com.tll.tabulaw.client;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.RootPanel;
import com.tll.client.mvc.ViewManager;
import com.tll.client.mvc.view.ShowViewRequest;
import com.tll.client.mvc.view.StaticViewInitializer;
import com.tll.client.mvc.view.ViewClass;
import com.tll.common.model.Model;
import com.tll.tabulaw.client.model.PocModelStore;
import com.tll.tabulaw.client.ui.Notifier;
import com.tll.tabulaw.client.ui.Portal;
import com.tll.tabulaw.client.ui.nav.NavColPanel;
import com.tll.tabulaw.client.ui.nav.NavRowPanel;
import com.tll.tabulaw.client.view.DocumentSearchView;
import com.tll.tabulaw.client.view.DocumentView;
import com.tll.tabulaw.client.view.DocumentsView;
import com.tll.tabulaw.client.view.QuoteBundlesView;
import com.tll.tabulaw.common.model.PocEntityType;

/**
 * Poc
 * @author jpk
 */
public class Poc implements EntryPoint {

	/**
	 * Use this token to initialize GWT history tracking.
	 */
	public static final String INITIAL_HISTORY_TOKEN = "";
	
	public static NavRowPanel getNavRow() {
		return (NavRowPanel) RootPanel.get("navRow").getWidget(0);
	}

	public static NavColPanel getNavCol() {
		return (NavColPanel) RootPanel.get("navCol").getWidget(0);
	}

	public static Portal getPortal() {
		return (Portal) RootPanel.get("portal").getWidget(0);
	}

	public void onModuleLoad() {
		Log.setUncaughtExceptionHandler();
		History.newItem(INITIAL_HISTORY_TOKEN);

		// declare the available views
		populateViewClasses();

		// build out the core structure
		build();

		DeferredCommand.addCommand(new Command() {

			@SuppressWarnings("synthetic-access")
			public void execute() {
				ViewManager.get().dispatch(new ShowViewRequest(new StaticViewInitializer(DocumentSearchView.klas)));
			}
		});
	}

	private void build() {
		Log.debug("Building..");

		// add the nav row panel
		NavRowPanel navRowPanel = new NavRowPanel();
		RootPanel.get("navRow").add(navRowPanel);

		// add the nav col panel
		NavColPanel navColPanel = new NavColPanel();
		RootPanel.get("navCol").add(navColPanel);

		// add the portal
		Portal portal = new Portal();
		RootPanel.get("portal").add(portal);

		// default set the current quote bundle
		Model firstQuoteBundle = PocModelStore.get().getAll(PocEntityType.QUOTE_BUNDLE).get(0);
		PocModelStore.get().setCurrentQuoteBundle(firstQuoteBundle, null);

		ViewManager.initialize(portal, 10);

		// create handler for displaying nav row/col content which is view specific
		ViewManager.get().addViewChangeHandler(navColPanel);
		ViewManager.get().addViewChangeHandler(navRowPanel);
		
		// initialize the ui msg notifier
		Notifier.init(navColPanel);
		
		// default set the current quote bundle
		navRowPanel.getCrntQuoteBudleWidget().update();

		Log.debug("Building complete.");
	}

	private void populateViewClasses() {
		ViewClass.addClass(DocumentSearchView.klas);
		ViewClass.addClass(DocumentsView.klas);
		ViewClass.addClass(DocumentView.klas);
		ViewClass.addClass(QuoteBundlesView.klas);
	}
}

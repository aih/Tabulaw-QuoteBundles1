package com.tll.tabulaw.client;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;
import com.tll.client.model.ModelChangeEvent;
import com.tll.client.mvc.ViewManager;
import com.tll.client.mvc.view.ShowViewRequest;
import com.tll.client.mvc.view.StaticViewInitializer;
import com.tll.client.mvc.view.ViewClass;
import com.tll.client.ui.IUserSessionHandler;
import com.tll.client.ui.LoginDialog;
import com.tll.client.ui.UserSessionEvent;
import com.tll.common.model.CopyCriteria;
import com.tll.common.model.Model;
import com.tll.tabulaw.client.ui.Notifier;
import com.tll.tabulaw.client.ui.Portal;
import com.tll.tabulaw.client.ui.nav.NavColPanel;
import com.tll.tabulaw.client.ui.nav.NavRowPanel;
import com.tll.tabulaw.client.view.DocumentView;
import com.tll.tabulaw.client.view.DocumentsView;
import com.tll.tabulaw.client.view.QuoteBundlesView;
import com.tll.tabulaw.common.data.rpc.IDocService;
import com.tll.tabulaw.common.data.rpc.IDocServiceAsync;
import com.tll.tabulaw.common.data.rpc.IUserContextService;
import com.tll.tabulaw.common.data.rpc.IUserContextServiceAsync;
import com.tll.tabulaw.common.data.rpc.UserContextPayload;

/**
 * Poc
 * @author jpk
 */
public class Poc implements EntryPoint {

	/**
	 * The sole doc service in the app.
	 */
	private static final IDocServiceAsync docService;
	
	private static final IUserContextServiceAsync userContextService;
	
	/**
	 * The sole current quote bundle.
	 */
	private static Model currentQuoteBundle;
	
	public static IDocServiceAsync getDocService() {
		return docService;
	}
	
	/**
	 * @return The current quote bundle ref.
	 */
	public static Model getCurrentQuoteBundle() {
		return currentQuoteBundle == null ? null : currentQuoteBundle.copy(CopyCriteria.keepReferences());
	}

	/**
	 * Sets the current quote bundle ref.
	 * <p>
	 * Fires a {@link ModelChangeEvent} when successful.
	 * @param mQuoteBundle non-null
	 * @return <code>true</code> if the current quote bundle ref was actually
	 *         updated and a model change event was fired.
	 */
	public static boolean setCurrentQuoteBundle(Model mQuoteBundle) {
		if(mQuoteBundle == null) throw new NullPointerException();
		if(currentQuoteBundle == null || !currentQuoteBundle.getKey().equals(mQuoteBundle)) {
			currentQuoteBundle = mQuoteBundle;
			getNavRow().getCrntQuoteBudleWidget().update();
			return true;
		}
		return false;
	}
	
	static {
		docService = (IDocServiceAsync) GWT.create(IDocService.class);
		userContextService = (IUserContextServiceAsync) GWT.create(IUserContextService.class);
	}

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

	private LoginDialog loginDialog;
	
	public void onModuleLoad() {
		Log.setUncaughtExceptionHandler();
		History.newItem(INITIAL_HISTORY_TOKEN);

		// declare the available views
		populateViewClasses();

		// build out the core structure
		build();

		userContextService.getUserContext(new AsyncCallback<UserContextPayload>() {
			
			@Override
			public void onSuccess(UserContextPayload result) {
				if(result.getUser() == null) {
					// not logged in
					if(loginDialog == null) {
						loginDialog = new LoginDialog("/login");
						loginDialog.setGlassEnabled(true);
						loginDialog.addUserSessionHandler(new IUserSessionHandler() {
							
							@Override
							public void onUserSessionEvent(UserSessionEvent event) {
								if(event.isStart()) {
									
									loginDialog.hide();
									loginDialog = null;
									
									DeferredCommand.addCommand(new Command() {

										@SuppressWarnings("synthetic-access")
										public void execute() {
											ViewManager.get().dispatch(new ShowViewRequest(new StaticViewInitializer(DocumentsView.klas)));
										}
									});
								}
							}
						});
					}
					loginDialog.center();
				}
				else {
					if(loginDialog != null) {
						loginDialog.hide();
						loginDialog = null;
					}
					ViewManager.get().dispatch(new ShowViewRequest(new StaticViewInitializer(DocumentsView.klas)));
				}
			}
			
			@Override
			public void onFailure(Throwable caught) {
			}
		});
		
		// TODO temp bypass logins
		//ViewManager.get().dispatch(new ShowViewRequest(new StaticViewInitializer(DocumentsView.klas)));
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

		ViewManager.initialize(portal, 10);

		// create handler for displaying nav row/col content which is view specific
		ViewManager.get().addViewChangeHandler(navColPanel);
		ViewManager.get().addViewChangeHandler(navRowPanel);
		
		// pre-load quote bundles view so it recieves model change events staying in sync!
		ViewManager.get().loadView(new StaticViewInitializer(QuoteBundlesView.klas));
		
		// initialize the ui msg notifier
		Notifier.init(navColPanel);
		
		Log.debug("Building complete.");
	}

	private void populateViewClasses() {
		ViewClass.addClass(DocumentsView.klas);
		ViewClass.addClass(DocumentView.klas);
		ViewClass.addClass(QuoteBundlesView.klas);
	}
}

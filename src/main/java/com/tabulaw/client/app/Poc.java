package com.tabulaw.client.app;

import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import com.tabulaw.client.app.model.ClientModelCache;
import com.tabulaw.client.app.ui.LoginTopPanel;
import com.tabulaw.client.app.ui.Portal;
import com.tabulaw.client.app.ui.nav.NavColPanel;
import com.tabulaw.client.app.ui.nav.NavRowPanel;
import com.tabulaw.client.app.ui.nav.NavTabsPanel;
import com.tabulaw.client.app.view.BundlesView;
import com.tabulaw.client.app.view.DocView;
import com.tabulaw.client.app.view.DocsView;
import com.tabulaw.client.app.view.UsersView;
import com.tabulaw.client.model.ModelChangeEvent;
import com.tabulaw.client.model.ModelChangeEvent.ModelChangeOp;
import com.tabulaw.client.ui.Notifier;
import com.tabulaw.client.ui.Position;
import com.tabulaw.client.ui.login.IUserSessionHandler;
import com.tabulaw.client.ui.login.UserSessionEvent;
import com.tabulaw.client.ui.msg.GlobalMsgPanel;
import com.tabulaw.client.view.ShowViewRequest;
import com.tabulaw.client.view.StaticViewInitializer;
import com.tabulaw.client.view.ViewClass;
import com.tabulaw.client.view.ViewManager;
import com.tabulaw.common.data.rpc.IGoogleDocsService;
import com.tabulaw.common.data.rpc.IGoogleDocsServiceAsync;
import com.tabulaw.common.data.rpc.IRemoteDocService;
import com.tabulaw.common.data.rpc.IRemoteDocServiceAsync;
import com.tabulaw.common.data.rpc.IUserAdminService;
import com.tabulaw.common.data.rpc.IUserAdminServiceAsync;
import com.tabulaw.common.data.rpc.IUserContextService;
import com.tabulaw.common.data.rpc.IUserContextServiceAsync;
import com.tabulaw.common.data.rpc.IUserCredentialsService;
import com.tabulaw.common.data.rpc.IUserCredentialsServiceAsync;
import com.tabulaw.common.data.rpc.IUserDataService;
import com.tabulaw.common.data.rpc.IUserDataServiceAsync;
import com.tabulaw.common.data.rpc.UserContextPayload;
import com.tabulaw.model.QuoteBundle;
import com.tabulaw.model.User;
import com.tabulaw.model.UserState;

/**
 * Poc
 * @author jpk
 */
public class Poc implements EntryPoint, IUserSessionHandler {

	private static IUserAdminServiceAsync userAdminService;

	private static final IUserContextServiceAsync userContextService;

	private static IUserCredentialsServiceAsync userCredentialsService;

	private static final IUserDataServiceAsync userDataService;
	
	private static final IRemoteDocServiceAsync docService;
	
	private static final IGoogleDocsServiceAsync googleDocsService;

	/**
	 * Use this token to initialize GWT history tracking.
	 */
	public static final String INITIAL_HISTORY_TOKEN = "";

	private static final GlobalMsgPanel msgPanel;

	static {
		userContextService = (IUserContextServiceAsync) GWT.create(IUserContextService.class);
		userDataService = (IUserDataServiceAsync) GWT.create(IUserDataService.class);
		docService = (IRemoteDocServiceAsync) GWT.create(IRemoteDocService.class);
		googleDocsService = (IGoogleDocsServiceAsync) GWT.create(IGoogleDocsService.class);

		msgPanel = new GlobalMsgPanel();
	}

	/**
	 * @return The user admin service.
	 */
	public static IUserAdminServiceAsync getUserAdminService() {
		if(userAdminService == null) {
			userAdminService = (IUserAdminServiceAsync) GWT.create(IUserAdminService.class);
		}
		return userAdminService;
	}

	/**
	 * @return The user context service.
	 */
	public static IUserContextServiceAsync getUserContextService() {
		return userContextService;
	}

	/**
	 * @return The user register service.
	 */
	public static IUserCredentialsServiceAsync getUserRegisterService() {
		if(userCredentialsService == null) {
			userCredentialsService = (IUserCredentialsServiceAsync) GWT.create(IUserCredentialsService.class);
		}
		return userCredentialsService;
	}

	/**
	 * @return The user data service.
	 */
	public static IUserDataServiceAsync getUserDataService() {
		return userDataService;
	}
	
	/**
	 * @return The doc service.
	 */
	public static IRemoteDocServiceAsync getDocService() {
		return docService;
	}

	/**
	 * @return The sole global msg panel in the app. This is intended to be used
	 *         freely by those who need it meaning it may be detached and attached
	 *         in different parts of the DOM.
	 */
	public static GlobalMsgPanel unparkGlobalMsgPanel() {
		msgPanel.removeFromParent();
		return msgPanel;
	}

	public static void parkGlobalMsgPanel() {
		RootPanel mainCol = RootPanel.get("mainCol");
		if(mainCol.getWidgetCount() == 2) {
			mainCol.insert(msgPanel, 1);
		}
	}

	/**
	 * Manual firing of a model change event throughout the client app.
	 * @param mce model change event to fire
	 */
	public static void fireModelChangeEvent(ModelChangeEvent mce) {
		getPortal().fireEvent(mce);
	}

	public static NavRowPanel getNavRow() {
		return (NavRowPanel) RootPanel.get("navRow").getWidget(0);
	}

	public static NavColPanel getNavCol() {
		return (NavColPanel) RootPanel.get("navCol").getWidget(0);
	}

	public static NavTabsPanel getNavTabs() {
		return (NavTabsPanel) RootPanel.get("navTabs").getWidget(0);
	}

	public static Portal getPortal() {
		return (Portal) RootPanel.get("portal").getWidget(0);
	}

	private LoginTopPanel loginPanel;

	private void getUserContext() {
		// we need to make sure we have a clean slate
		assert ClientModelCache.get().totalSize() == 0;

		userContextService.getClientUserContext(new AsyncCallback<UserContextPayload>() {

			@Override
			public void onSuccess(final UserContextPayload result) {
				User liu = result.getUser();
				if(liu == null) {
					// not logged in
					showLoginPanel();
					/*-
					GWT.runAsync(new RunAsyncCallback() {
						@Override
						public void onSuccess() {
							showLoginPanel();
						}
						@Override
						public void onFailure(Throwable reason) {
							showLoginPanel();
						}
					});*/
				} else {
					showApplication(result);
					/*-
					GWT.runAsync(new RunAsyncCallback() {
						@Override
						public void onSuccess() {
							showApplication(result);
						}
						@Override
						public void onFailure(Throwable reason) {
							showLoginPanel();
						}
					});*/
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				// assume not logged in
				// (this will happen when an AccessDeniedException is thrown for this
				// RPC call)
				showLoginPanel();
			}
		});
	}

	private void showApplication(UserContextPayload result){
		User liu = result.getUser();
		
		hideLoginPanel();

		// attach the global msg panel in its native place
		parkGlobalMsgPanel();

		// cache initial batch of next ids
		ClientModelCache.get().setNextIdBatch(result.getNextIds());

		// cache user (i.e. the user context) and notify
		ClientModelCache.get().persist(liu, null);
		fireModelChangeEvent(new ModelChangeEvent(null, ModelChangeOp.LOADED, liu, null));
		
		// set the orphan bundle id
		ClientModelCache.get().setOrphanedQuoteBundleId(result.getOrphanQuoteContainerId());

		// load up user bundles
		List<QuoteBundle> userBundles = result.getBundles();

		// we need to individually add the contained quotes as well but don't
		// throw model changes events for quotes
		for(QuoteBundle qb : userBundles) {
			ClientModelCache.get().persistAll(qb.getQuotes());
		}

		// cache bundles (w/ no notification)
		ClientModelCache.get().persistAll(userBundles);

		// load bundles view (this will pull all just stored bundles from cache)
		ViewManager.get().loadView(new StaticViewInitializer(BundlesView.klas));

		// cache user state
		UserState userState = result.getUserState();
		if(userState == null) {
			Log.debug("Creating new UserState instance");
			userState = new UserState(liu.getId());
		}
		ClientModelCache.get().persist(userState, getPortal());

		// show doc listing view by default
		ViewManager.get().dispatch(new ShowViewRequest(new StaticViewInitializer(DocsView.klas)));
	}

	private void showLoginPanel() {		
		Widget htmlLogin = RootPanel.get("login");
		if(htmlLogin != null){
			htmlLogin.setVisible(true);
		}
		
		if(loginPanel == null) {
			loginPanel = new LoginTopPanel();
			loginPanel.addUserSessionHandler(new IUserSessionHandler() {
				@Override
				public void onUserSessionEvent(UserSessionEvent event) {
					if(event.isStart()) {
						hideLoginPanel();
						getUserContext();
					}
				}
			});
		}
		getNavRow().setVisible(false);
		getNavCol().setVisible(false);
		getNavTabs().setVisible(false);
		getPortal().setVisible(false);
		RootPanel.get("portal").add(loginPanel);
	}

	private void hideLoginPanel() {
		Widget htmlLogin = RootPanel.get("login");
		if(htmlLogin != null){
			htmlLogin.setVisible(false);
		}
		if(loginPanel != null) {
			loginPanel.removeFromParent();
			loginPanel = null;
		}
		getPortal().setVisible(true);
		getNavRow().setVisible(true);
		getNavTabs().setVisible(true);
		getNavCol().setVisible(true);
	}

	@Override
	public void onModuleLoad() {
		Log.setUncaughtExceptionHandler();
		History.newItem(INITIAL_HISTORY_TOKEN);

		//new Timer(){@Override public void run() {
		DeferredCommand.addCommand(new Command() {
			@Override
			public void execute() {
				populateViewClasses();
				build();
				getUserContext();
			}
		});
		//}	}.schedule(3000);

		// TODO temp bypass logins
		// ViewManager.get().dispatch(new ShowViewRequest(new
		// StaticViewInitializer(DocsView.klas)));
	}

	@Override
	public void onUserSessionEvent(UserSessionEvent event) {
		if(!event.isStart()) {
			clear();
			showLoginPanel();
		}
	}

	private void clear() {
		ViewManager.get().clear();
		getNavRow().clear();
		getNavTabs().clear();
		getNavCol().clear();
		ClientModelCache.get().clear();
	}

	private void build() {
		Log.debug("Building..");

		// add the portal
		Portal portal = new Portal();
		portal.setVisible(false);
		RootPanel.get("portal").add(portal);

		ClientModelCache.init(portal);

		// add the nav row panel
		NavRowPanel navRowPanel = new NavRowPanel(this);
		navRowPanel.setVisible(false);
		RootPanel.get("navRow").add(navRowPanel);

		// add the nav row panel
		NavTabsPanel navTabsPanel = new NavTabsPanel(this);
		navTabsPanel.setVisible(false);
		RootPanel.get("navTabs").add(navTabsPanel);

		// add the nav col panel
		NavColPanel navColPanel = new NavColPanel();
		navColPanel.setVisible(false);
		RootPanel.get("navCol").add(navColPanel);

		ViewManager.initialize(portal.getPanel(), 10);

		// create handler for displaying nav row/col content which is view specific
		ViewManager.get().addViewChangeHandler(navColPanel);
		ViewManager.get().addViewChangeHandler(navRowPanel);

		hideStaticLoginPanel();
		
		// initialize the ui msg notifier
		Notifier.init(navColPanel, Position.TOP, -20, 0);

		Log.debug("Building complete.");
	}

	private void hideStaticLoginPanel() {
		Element e = DOM.getElementById("staticLoginPanel");
		if(e != null) {
			UIObject.setVisible(e, false);
		}
	}
	
	private void populateViewClasses() {
		ViewClass.addClass(DocsView.klas);
		ViewClass.addClass(DocView.klas);
		ViewClass.addClass(BundlesView.klas);
		ViewClass.addClass(UsersView.klas);
	}

	public static IGoogleDocsServiceAsync getGoogledocsService() {
		return googleDocsService;
	}
}

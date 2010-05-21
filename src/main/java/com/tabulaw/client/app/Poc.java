package com.tabulaw.client.app;

import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;
import com.tabulaw.client.app.model.ClientModelCache;
import com.tabulaw.client.app.ui.LoginTopPanel;
import com.tabulaw.client.app.ui.Portal;
import com.tabulaw.client.app.ui.nav.NavColPanel;
import com.tabulaw.client.app.ui.nav.NavRowPanel;
import com.tabulaw.client.app.ui.view.BundlesView;
import com.tabulaw.client.app.ui.view.DocView;
import com.tabulaw.client.app.ui.view.DocsView;
import com.tabulaw.client.app.ui.view.UsersView;
import com.tabulaw.client.model.ModelChangeEvent;
import com.tabulaw.client.model.ModelChangeEvent.ModelChangeOp;
import com.tabulaw.client.mvc.ViewManager;
import com.tabulaw.client.mvc.view.ShowViewRequest;
import com.tabulaw.client.mvc.view.StaticViewInitializer;
import com.tabulaw.client.mvc.view.ViewClass;
import com.tabulaw.client.ui.Notifier;
import com.tabulaw.client.ui.Position;
import com.tabulaw.client.ui.login.IUserSessionHandler;
import com.tabulaw.client.ui.login.UserSessionEvent;
import com.tabulaw.client.ui.msg.GlobalMsgPanel;
import com.tabulaw.common.data.rpc.IDocService;
import com.tabulaw.common.data.rpc.IDocServiceAsync;
import com.tabulaw.common.data.rpc.IUserAdminService;
import com.tabulaw.common.data.rpc.IUserAdminServiceAsync;
import com.tabulaw.common.data.rpc.IUserContextService;
import com.tabulaw.common.data.rpc.IUserContextServiceAsync;
import com.tabulaw.common.data.rpc.IUserCredentialsService;
import com.tabulaw.common.data.rpc.IUserCredentialsServiceAsync;
import com.tabulaw.common.data.rpc.UserContextPayload;
import com.tabulaw.common.model.Quote;
import com.tabulaw.common.model.QuoteBundle;
import com.tabulaw.common.model.User;
import com.tabulaw.common.model.UserState;

/**
 * Poc
 * @author jpk
 */
public class Poc implements EntryPoint, IUserSessionHandler {

	private static IUserAdminServiceAsync userAdminService;

	private static final IUserContextServiceAsync userContextService;

	private static IUserCredentialsServiceAsync userCredentialsService;

	private static final IDocServiceAsync docService;

	/**
	 * Use this token to initialize GWT history tracking.
	 */
	public static final String INITIAL_HISTORY_TOKEN = "";

	private static final GlobalMsgPanel msgPanel;

	static {
		userContextService = (IUserContextServiceAsync) GWT.create(IUserContextService.class);
		docService = (IDocServiceAsync) GWT.create(IDocService.class);

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
	 * @return The doc service.
	 */
	public static IDocServiceAsync getDocService() {
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

	public static Portal getPortal() {
		return (Portal) RootPanel.get("portal").getWidget(0);
	}

	private LoginTopPanel loginPanel;

	private void getUserContext() {
		// we need to make sure we have a clean slate
		assert ClientModelCache.get().totalSize() == 0;

		userContextService.getClientUserContext(new AsyncCallback<UserContextPayload>() {

			@Override
			public void onSuccess(UserContextPayload result) {
				User liu = result.getUser();
				if(liu == null) {
					// not logged in
					showLoginPanel();
				}
				else {
					hideLoginPanel();

					// attach the global msg panel in its native place
					parkGlobalMsgPanel();

					// cache initial batch of next ids
					ClientModelCache.get().setNextIdBatch(result.getNextIds());

					// cache user (i.e. the user context) and notify
					ClientModelCache.get().persist(liu, null);
					getPortal().fireEvent(new ModelChangeEvent(null, ModelChangeOp.LOADED, liu, null));

					// load up user bundles
					List<QuoteBundle> userBundles = result.getBundles();

					// we need to individually add the contained quotes as well but don't
					// throw model changes events for quotes
					for(QuoteBundle qb : userBundles) {
						ClientModelCache.get().persistAll(qb.getQuotes());
					}

					// store orphaned quotes (w/ no notification)
					List<Quote> orphanedQuotes = result.getOrphanedQuotes();
					ClientModelCache.get().persistAll(orphanedQuotes);
					ClientModelCache.get().getOrphanedQuoteContainer().addQuotes(orphanedQuotes);

					// cache bundles w/ no notification
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

	private void showLoginPanel() {
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
		getPortal().setVisible(false);
		RootPanel.get("portal").add(loginPanel);
	}

	private void hideLoginPanel() {
		if(loginPanel != null) {
			loginPanel.removeFromParent();
			loginPanel = null;
		}
		getPortal().setVisible(true);
		getNavRow().setVisible(true);
		getNavCol().setVisible(true);
	}

	public void onModuleLoad() {
		Log.setUncaughtExceptionHandler();
		History.newItem(INITIAL_HISTORY_TOKEN);

		DeferredCommand.addCommand(new Command() {

			public void execute() {
				populateViewClasses();
				build();
				getUserContext();
			}
		});

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

		// add the nav col panel
		NavColPanel navColPanel = new NavColPanel();
		navColPanel.setVisible(false);
		RootPanel.get("navCol").add(navColPanel);

		ViewManager.initialize(portal.getPanel(), 10);

		// create handler for displaying nav row/col content which is view specific
		ViewManager.get().addViewChangeHandler(navColPanel);
		ViewManager.get().addViewChangeHandler(navRowPanel);

		// initialize the ui msg notifier
		Notifier.init(navColPanel, Position.TOP, -20, 0);

		Log.debug("Building complete.");
	}

	private void populateViewClasses() {
		ViewClass.addClass(DocsView.klas);
		ViewClass.addClass(DocView.klas);
		ViewClass.addClass(BundlesView.klas);
		ViewClass.addClass(UsersView.klas);
	}
}

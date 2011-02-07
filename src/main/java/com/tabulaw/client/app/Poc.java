package com.tabulaw.client.app;

import java.util.List;
import java.util.UUID;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.tabulaw.client.app.model.ClientModelCache;
import com.tabulaw.client.app.ui.LoginTopPanel;
import com.tabulaw.client.app.ui.Portal;
import com.tabulaw.client.app.ui.nav.CurrentQuoteBundleDisplayWidget;
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
 * 
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
		if (userAdminService == null) {
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
		if (userCredentialsService == null) {
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
	 *         freely by those who need it meaning it may be detached and
	 *         attached in different parts of the DOM.
	 */
	public static GlobalMsgPanel unparkGlobalMsgPanel() {
		msgPanel.removeFromParent();
		return msgPanel;
	}

	public static void parkGlobalMsgPanel() {
		mainColPanel.insert(msgPanel, 1);
	}

	/**
	 * Manual firing of a model change event throughout the client app.
	 * 
	 * @param mce
	 *            model change event to fire
	 */
	public static void fireModelChangeEvent(ModelChangeEvent mce) {
		getPortal().fireEvent(mce);
	}

	public static NavRowPanel getNavRow() {
		return navRow;
	}

	public static NavColPanel getNavCol() {
		return navCol;
	}

	public static NavTabsPanel getNavTabs() {
		return navTabs;
	}

	public static Portal getPortal() {
		return portal;
	}
	public static CurrentQuoteBundleDisplayWidget getCrntQuoteBudleWidget() {
		return crntQuoteBudleWidget;
	}

	private LoginTopPanel loginPanel;

	private void getUserContext() {
		// we need to make sure we have a clean slate
		assert ClientModelCache.get().totalSize() == 0;

		userContextService.getClientUserContext(new AsyncCallback<UserContextPayload>() {

			@Override
			public void onSuccess(UserContextPayload result) {
				User liu = result.getUser();

				if (liu == null) {
					// not logged in
					showLoginPanel();
				} else {
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

					// we need to individually add the contained quotes as well
					// but don't
					// throw model changes events for quotes
					for (QuoteBundle qb : userBundles) {
						ClientModelCache.get().persistAll(qb.getQuotes());
					}

					// cache bundles (w/ no notification)
					ClientModelCache.get().persistAll(userBundles);

					// load bundles view (this will pull all just stored bundles
					// from cache)
					ViewManager.get().loadView(new StaticViewInitializer(BundlesView.klas));

					// cache user state
					UserState userState = result.getUserState();
					if (userState == null) {
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
				// (this will happen when an AccessDeniedException is thrown for
				// this
				// RPC call)
				showLoginPanel();
			}
		});
	}

	private void showLoginPanel() {
		if (loginPanel == null) {
			loginPanel = new LoginTopPanel();
			loginPanel.addUserSessionHandler(new IUserSessionHandler() {

				@Override
				public void onUserSessionEvent(UserSessionEvent event) {
					if (event.isStart()) {
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
//		RootPanel.get("portal").add(loginPanel);
	}

	private void hideLoginPanel() {
		if (loginPanel != null) {
			loginPanel.removeFromParent();
			loginPanel = null;
		}
		getPortal().setVisible(true);
		getNavRow().setVisible(true);
		getNavTabs().setVisible(true);
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
		if (!event.isStart()) {
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

	private static Portal portal = new Portal(); 
	private static NavColPanel navCol = new NavColPanel();
	private static NavRowPanel navRow;
	private static NavTabsPanel navTabs;
	private static FlowPanel mainColPanel = new FlowPanel();
	private static CurrentQuoteBundleDisplayWidget crntQuoteBudleWidget = new CurrentQuoteBundleDisplayWidget();
	
	private void build() {

		Log.debug("Building..");

		ClientModelCache.init(portal);

		ViewManager.initialize(portal.getPanel(), 10);
		portal.setVisible(false);
		
		DockLayoutPanel container = new DockLayoutPanel(Unit.PX);
		container.getElement().setId("container");
		
		navTabs = new NavTabsPanel(this) ;

		FlowPanel navColPanel = new FlowPanel();
		
		navColPanel.setWidth("148px");
		
		navColPanel.getElement().setId("navCol");
		
		
		HTML img1 = new HTML("<img title='Beta' class='beta' src='images/beta_banner_left.png' alt='Tabulaw Beta'>");
		HTML img2 = new HTML("<img title='Tabulaw' class='logo' src='images/tabulaw_logo.png' alt='Tabulaw'>");

		navColPanel.add(img1);
		navColPanel.add(img2);
		navColPanel.add(navTabs);
		navColPanel.add(navCol);

		navRow = new NavRowPanel(this);
		portal.setVisible(false);

		crntQuoteBudleWidget.setStyleName("crntqb");
		crntQuoteBudleWidget.makeModelChangeAware();
		
		mainColPanel.add(crntQuoteBudleWidget);
		mainColPanel.add(portal);

		HTML footer = new HTML("<div id='footer'><p class='footer'>&copy; 2010 Tabulaw, Inc.</p></div>");
		container.addWest(navColPanel,180);
		container.addNorth(navRow,44);
		container.addSouth(footer,30);
		
		container.add(mainColPanel);
		container.getElement().setId("container");
		
		RootLayoutPanel root = RootLayoutPanel.get();
		
		root.add(container);

		// create handler for displaying nav row/col content which is view
		// specific
		ViewManager.get().addViewChangeHandler(navCol);
		ViewManager.get().addViewChangeHandler(navRow);
		ViewManager.get().addViewChangeHandler(navTabs);
		
		

		// initialize the ui msg notifier
		Notifier.init(navCol, Position.TOP, -20, 0);

		Log.debug("Building complete.");
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

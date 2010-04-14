package com.tll.tabulaw.client;

import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;
import com.tll.client.model.ModelChangeEvent;
import com.tll.client.model.ModelChangeEvent.ModelChangeOp;
import com.tll.client.mvc.ViewManager;
import com.tll.client.mvc.view.ShowViewRequest;
import com.tll.client.mvc.view.StaticViewInitializer;
import com.tll.client.mvc.view.ViewClass;
import com.tll.client.ui.IUserSessionHandler;
import com.tll.client.ui.LoginPanel;
import com.tll.client.ui.UserSessionEvent;
import com.tll.common.model.CopyCriteria;
import com.tll.common.model.Model;
import com.tll.tabulaw.client.model.PocModelCache;
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
import com.tll.tabulaw.common.data.rpc.IUserDataService;
import com.tll.tabulaw.common.data.rpc.IUserDataServiceAsync;
import com.tll.tabulaw.common.data.rpc.UserContextPayload;

/**
 * Poc
 * @author jpk
 */
public class Poc implements EntryPoint, IUserSessionHandler {

	private static final IUserContextServiceAsync userContextService;
	
	private static final IUserDataServiceAsync userDataService;

	private static final IDocServiceAsync docService;
	
	/**
	 * Use this token to initialize GWT history tracking.
	 */
	public static final String INITIAL_HISTORY_TOKEN = "";
	
	/**
	 * The sole current quote bundle.
	 */
	private static Model currentQuoteBundle;
	
	static {
		docService = (IDocServiceAsync) GWT.create(IDocService.class);
		userContextService = (IUserContextServiceAsync) GWT.create(IUserContextService.class);
		userDataService = (IUserDataServiceAsync) GWT.create(IUserDataService.class);
	}

	/**
	 * @return The user context service.
	 */
	public static IUserContextServiceAsync getUserContextService() {
		return userContextService;
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
	
	public static NavRowPanel getNavRow() {
		return (NavRowPanel) RootPanel.get("navRow").getWidget(0);
	}

	public static NavColPanel getNavCol() {
		return (NavColPanel) RootPanel.get("navCol").getWidget(0);
	}

	public static Portal getPortal() {
		return (Portal) RootPanel.get("portal").getWidget(0);
	}
	
	private LoginPanel loginPanel;
	
	private void getUserContext() {
		userContextService.getUserContext(new AsyncCallback<UserContextPayload>() {
			
			@Override
			public void onSuccess(UserContextPayload result) {
				Model liu = result.getUser();
				if(liu == null) {
					// not logged in
					showLoginPanel();
				}
				else {
					hideLoginPanel();
					
					// ensure quote bundles view so it recieves model change events staying in sync!
					ViewManager.get().loadView(new StaticViewInitializer(QuoteBundlesView.klas));
					
					// cache user (i.e. the user context) and notify
					PocModelCache.get().persist(liu, null);
					getPortal().fireEvent(new ModelChangeEvent(ModelChangeOp.LOADED, liu, null));

					// load up user bundles
					List<Model> userBundles = result.getBundles();
					PocModelCache.get().persistAll(userBundles, getPortal());
					
					// show doc listing view by default
					ViewManager.get().dispatch(new ShowViewRequest(new StaticViewInitializer(DocumentsView.klas)));
				}
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// assume not logged in 
				// (this will happen when an AccessDeniedException is thrown for this RPC call)
				showLoginPanel();
			}
		});
	}
	
	private void showLoginPanel() {
		if(loginPanel == null) {
			loginPanel = new LoginPanel("/login/");
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
		//ViewManager.get().dispatch(new ShowViewRequest(new StaticViewInitializer(DocumentsView.klas)));
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
		PocModelCache.get().clear();
		currentQuoteBundle = null;
	}

	private void build() {
		Log.debug("Building..");

		// add the nav row panel
		NavRowPanel navRowPanel = new NavRowPanel(this);
		navRowPanel.setVisible(false);
		RootPanel.get("navRow").add(navRowPanel);

		// add the nav col panel
		NavColPanel navColPanel = new NavColPanel();
		navColPanel.setVisible(false);
		RootPanel.get("navCol").add(navColPanel);

		// add the portal
		Portal portal = new Portal();
		RootPanel.get("portal").add(portal);

		ViewManager.initialize(portal.getPanel(), 10);

		// create handler for displaying nav row/col content which is view specific
		ViewManager.get().addViewChangeHandler(navColPanel);
		ViewManager.get().addViewChangeHandler(navRowPanel);
		
		// pre-load quote bundles view so it recieves model change events staying in sync!
		//ViewManager.get().loadView(new StaticViewInitializer(QuoteBundlesView.klas));
		
		// initialize the ui msg notifier
		Notifier.init(navColPanel);
		
		// hide until user context gotten
		portal.setVisible(false);
		getNavRow().setVisible(false);
		getNavCol().setVisible(false);

		Log.debug("Building complete.");
	}

	private void populateViewClasses() {
		ViewClass.addClass(DocumentsView.klas);
		ViewClass.addClass(DocumentView.klas);
		ViewClass.addClass(QuoteBundlesView.klas);
	}
}

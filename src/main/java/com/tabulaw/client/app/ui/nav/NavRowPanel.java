/**
 * The Logic Lab
 * @author jpk
 * @since Feb 12, 2010
 */
package com.tabulaw.client.app.ui.nav;

import java.util.ArrayList;
import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TabBar;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitHandler;
import com.tabulaw.client.app.model.ClientModelCache;
import com.tabulaw.client.app.ui.view.DocView;
import com.tabulaw.client.model.IModelChangeHandler;
import com.tabulaw.client.model.ModelChangeEvent;
import com.tabulaw.client.model.ModelChangeEvent.ModelChangeOp;
import com.tabulaw.client.mvc.ViewManager;
import com.tabulaw.client.mvc.view.IViewInitializerProvider;
import com.tabulaw.client.mvc.view.ShowViewRequest;
import com.tabulaw.client.mvc.view.UnloadViewRequest;
import com.tabulaw.client.mvc.view.ViewKey;
import com.tabulaw.client.ui.SimpleHyperLink;
import com.tabulaw.client.ui.login.IUserSessionHandler;
import com.tabulaw.client.ui.login.UserSessionEvent;
import com.tabulaw.common.model.DocKey;
import com.tabulaw.common.model.EntityType;
import com.tabulaw.common.model.IEntity;
import com.tabulaw.common.model.QuoteBundle;
import com.tabulaw.common.model.User;

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
		 * Style applied to the div containing the horizontal panel containing the
		 * tabs.
		 */
		public static final String NAV_ROW_TABS = "navRowTabs";

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
	public static class CurrentQuoteBundleDisplayWidget extends Composite implements IModelChangeHandler {

		private final HTML html = new HTML();
		private String crntQbId;

		public CurrentQuoteBundleDisplayWidget() {
			super();
			initWidget(html);
		}

		public void update() {
			QuoteBundle cqb = ClientModelCache.get().getCurrentQuoteBundle();
			if(cqb != null) {
				String id = cqb.getId();
				if(!id.equals(crntQbId)) {
					this.crntQbId = id;
					html.setHTML("<p><span class=\"echo\">Current Quote Bundle:</span>" + cqb.getName() + "</p>");
					html.setVisible(true);
				}
			}
			else {
				html.setVisible(false);
			}
		}

		@Override
		public void onModelChangeEvent(ModelChangeEvent event) {
			update();
		}

		public void clear() {
			html.setHTML("");
			crntQbId = null;
		}

	} // CurrentQuoteBundleDisplayWidget

	static class LoggedInUserWidget extends Composite implements IModelChangeHandler {

		static class Styles {

			public static final String LGD_IN_USR = "lgdInUsr";
			public static final String FORM_CONTENTS = "frmContents";
			public static final String WELCOME_TEXT = "welcomeText";
			public static final String LOGOUT = "logout";
		}

		final Label welcomeText;
		final SimpleHyperLink lnkLogOut;
		final FlowPanel pnl;
		final Hidden hiddenCurrentBundleId;
		final FormPanel frmLogout;

		// wraps the form and is the top-most widget
		final SimplePanel wrapper;

		public LoggedInUserWidget() {
			super();

			pnl = new FlowPanel();
			pnl.setStyleName(Styles.FORM_CONTENTS);

			welcomeText = new Label();
			welcomeText.setStyleName(Styles.WELCOME_TEXT);
			lnkLogOut = new SimpleHyperLink("Log Out", new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {

					// save the user state to the server
					ClientModelCache.get().saveUserState(new Command() {

						@Override
						public void execute() {
							frmLogout.submit();
						}
					});
				}
			});
			lnkLogOut.setStyleName(Styles.LOGOUT);

			hiddenCurrentBundleId = new Hidden("currentBundleId");

			frmLogout = new FormPanel();
			frmLogout.setMethod(FormPanel.METHOD_POST);
			frmLogout.setAction(GWT.getModuleBaseURL() + "logout");

			pnl.add(hiddenCurrentBundleId);
			pnl.add(welcomeText);
			pnl.add(lnkLogOut);
			frmLogout.add(pnl);

			wrapper = new SimplePanel();
			wrapper.setStyleName(Styles.LGD_IN_USR);
			wrapper.setWidget(frmLogout);

			initWidget(wrapper);
		}

		public void populate(User mUser) {
			welcomeText.setText("Welcome " + mUser.getName() + '!');
		}

		public void clear() {
			welcomeText.setText("");
		}

		@Override
		public void onModelChangeEvent(ModelChangeEvent event) {
			if(event.getChangeOp() == ModelChangeOp.LOADED) {
				IEntity m = event.getModel();
				if(EntityType.fromString(m.getEntityType()) == EntityType.USER) {
					populate((User) m);
				}
			}
		}
	}

	private static void showView(ArrayList<? extends IViewInitializerProvider> list, int index) {
		ViewManager.get().dispatch(new ShowViewRequest(list.get(index).getViewInitializer()));
	}

	private static final int maxNumOpenDocs = 6;

	private final ArrayList<IViewInitializerProvider> mainViewButtons = new ArrayList<IViewInitializerProvider>();

	private final ArrayList<DocViewNavButton> openDocNavButtons = new ArrayList<DocViewNavButton>();

	private final TabBar mainViewTabs = new TabBar();

	private final TabBar openDocTabs = new TabBar();

	private final CurrentQuoteBundleDisplayWidget crntQuoteBudleWidget = new CurrentQuoteBundleDisplayWidget();

	private final LoggedInUserWidget liuWidget = new LoggedInUserWidget();

	private final HorizontalPanel hp = new HorizontalPanel();
	private final SimplePanel hpWrapper = new SimplePanel();

	private final FlowPanel panel = new FlowPanel();

	private boolean handlingViewChange;

	/**
	 * Constructor
	 * @param userSessionHandler
	 */
	public NavRowPanel(final IUserSessionHandler userSessionHandler) {
		super();

		DocsNavButton nbDocListing = new DocsNavButton();
		QuoteBundlesNavButton nbQuoteBundles = new QuoteBundlesNavButton();

		mainViewButtons.add(nbDocListing);
		mainViewButtons.add(nbQuoteBundles);

		mainViewTabs.addStyleName(Styles.MAIN_VIEWS);
		mainViewTabs.addTab(nbDocListing);
		mainViewTabs.addTab(nbQuoteBundles);

		openDocTabs.addStyleName(Styles.OPEN_DOCS);

		crntQuoteBudleWidget.setStyleName(Styles.CRNT_QB);

		liuWidget.frmLogout.addSubmitHandler(new SubmitHandler() {

			@Override
			public void onSubmit(SubmitEvent event) {
				// clear out user state and fire user session end event w/o waiting for
				// server response
				ClientModelCache.get().removeAll(EntityType.USER, liuWidget);
				userSessionHandler.onUserSessionEvent(new UserSessionEvent(false));
			}
		});

		hp.setStyleName(Styles.NAV_ROW_TBL);
		hp.add(mainViewTabs);
		hp.add(openDocTabs);

		hpWrapper.setStyleName(Styles.NAV_ROW_TABS);
		hpWrapper.setWidget(hp);

		panel.setStyleName(Styles.NAV_ROW);

		panel.add(liuWidget);
		panel.add(hpWrapper);
		panel.add(crntQuoteBudleWidget);

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

	/**
	 * Clears out the nav row resetting its state to that of page refresh state.
	 */
	public void clear() {
		while(openDocTabs.getTabCount() > 0)
			openDocTabs.removeTab(0);
		openDocNavButtons.clear();

		// remove tail admin users nav button if present
		if(mainViewButtons.size() == 3) {
			mainViewButtons.remove(2);
			mainViewTabs.removeTab(2);
		}

		crntQuoteBudleWidget.clear();
		liuWidget.clear();
	}

	@Override
	protected void handleViewLoad(ViewKey key) {
		handlingViewChange = true;
		int i = 0;
		ViewKey crntViewKey = ViewManager.get().getCurrentViewKey();

		if(crntViewKey.getViewClass() == DocView.klas) {
			int index = getTabIndexFromViewKey(crntViewKey, false);
			if(index == -1) {
				// create a doc nav button and tab
				DocView dview = (DocView) ViewManager.get().resolveView(crntViewKey);
				DocKey docKey = dview.getDocKey();
				DocViewNavButton dnb = new DocViewNavButton(docKey);
				openDocNavButtons.add(0, dnb);
				openDocTabs.insertTab(dnb, 0);
				index = 0;
			}
			openDocTabs.selectTab(index);
			// unselect main view tabs
			mainViewTabs.selectTab(-1);

			// unload oldest view if at capacity
			if(openDocNavButtons.size() > maxNumOpenDocs) {
				openDocTabs.setVisible(false);
				ViewKey tounload = openDocNavButtons.get(openDocNavButtons.size() - 1).getViewInitializer().getViewKey();
				ViewManager.get().dispatch(new UnloadViewRequest(tounload, true, false));
			}
		}
		else {
			for(IViewInitializerProvider navBtn : mainViewButtons) {
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

	@Override
	protected void handleViewUnload(ViewKey key) {
		if(key.getViewClass() == DocView.klas) {
			int index = getTabIndexFromViewKey(key, false);
			if(index >= 0) {
				Log.debug("Removing old doc view: " + key);
				openDocNavButtons.remove(index);
				openDocTabs.removeTab(index);
				openDocTabs.setVisible(true);
			}
		}
	}

	private int getTabIndexFromViewKey(ViewKey viewKey, boolean mainView) {
		List<? extends IViewInitializerProvider> navBtnList = mainView ? mainViewButtons : openDocNavButtons;
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

		// user loaded
		if(event.getChangeOp() == ModelChangeOp.LOADED
				&& EntityType.USER.name().equals(event.getModelKey().getEntityType())) {
			
			User liu = (User) event.getModel();
			if(liu.inRole(User.Role.ADMINISTRATOR)) {
				UsersNavButton nbUsers = new UsersNavButton();
				mainViewButtons.add(nbUsers);
				mainViewTabs.addTab(nbUsers);
			}
		}

		// document deleted
		else if(event.getChangeOp() == ModelChangeOp.DELETED
				&& EntityType.DOCUMENT.name().equals(event.getModelKey().getEntityType())) {
			// remove open doc tab
			boolean found = false;
			int i = 0;
			for(DocViewNavButton b : openDocNavButtons) {
				if(b.getModelKey().equals(event.getModelKey())) {
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
		
		crntQuoteBudleWidget.onModelChangeEvent(event);
		liuWidget.onModelChangeEvent(event);
	}
}

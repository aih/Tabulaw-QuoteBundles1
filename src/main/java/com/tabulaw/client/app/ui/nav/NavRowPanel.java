/**
 * Copyright (C) Tabulaw, Inc. 2009-2010 All Rights Reserved
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
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TabBar;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.tabulaw.client.app.model.ClientModelCache;
import com.tabulaw.client.app.model.ServerPersistApi;
import com.tabulaw.client.app.ui.UserPasswordSetDialog;
import com.tabulaw.client.app.view.DocView;
import com.tabulaw.client.model.IModelChangeHandler;
import com.tabulaw.client.model.ModelChangeEvent;
import com.tabulaw.client.model.ModelChangeEvent.ModelChangeOp;
import com.tabulaw.client.ui.SimpleHyperLink;
import com.tabulaw.client.ui.login.IUserSessionHandler;
import com.tabulaw.client.view.IViewInitializerProvider;
import com.tabulaw.client.view.ShowViewRequest;
import com.tabulaw.client.view.UnloadViewRequest;
import com.tabulaw.client.view.ViewKey;
import com.tabulaw.client.view.ViewManager;
import com.tabulaw.model.DocKey;
import com.tabulaw.model.EntityType;
import com.tabulaw.model.IEntity;
import com.tabulaw.model.User;

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

	} // Styles

	static class LoggedInUserWidget extends Composite implements IModelChangeHandler {

		static class Styles {

			public static final String LGD_IN_USR = "lgdInUsr";
			public static final String FORM_CONTENTS = "frmContents";
			public static final String WELCOME_TEXT = "welcomeText";
			public static final String LOGOUT = "logout";
			public static final String RESET_PSWD = "rpswd";
		}

		final Label welcomeText;
		final SimpleHyperLink lnkLogOut, lnkResetPswd;
		final FlowPanel pnl;
		final Hidden hiddenCurrentBundleId;
		final FormPanel frmLogout;
		
		// TODO go MVP style as this is a clone of what is in UserEditPanel
		// we need to pull out interactions with the model from the widgets 
		// and make it purely event driven (the views and widgets are supposed to be dumb)!
		final UserPasswordSetDialog dlgResetPassword = new UserPasswordSetDialog();

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
					ServerPersistApi.get().saveUserState(new Command() {

						@Override
						public void execute() {
							frmLogout.submit();
						}
					});
				}
			});
			lnkLogOut.setStyleName(Styles.LOGOUT);
			lnkLogOut.setTitle("Log out from Tabulaw");
			
			lnkResetPswd = new SimpleHyperLink("Reset Password", new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					event.getNativeEvent().stopPropagation();
					User user = ClientModelCache.get().getUser();
					dlgResetPassword.set(user.getId(), user.getName(), user.getEmailAddress());
					dlgResetPassword.showRelativeTo((UIObject) event.getSource());
				}
			});
			lnkResetPswd.setStyleName(Styles.RESET_PSWD);
			lnkResetPswd.setTitle("Reset your login password");

			hiddenCurrentBundleId = new Hidden("currentBundleId");

			frmLogout = new FormPanel();
			frmLogout.setMethod(FormPanel.METHOD_POST);
			frmLogout.setAction(GWT.getModuleBaseURL() + "logout");
			
			pnl.add(hiddenCurrentBundleId);
			pnl.add(welcomeText);
			pnl.add(lnkResetPswd);
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

	private final ArrayList<DocViewNavButton> openDocNavButtons = new ArrayList<DocViewNavButton>();

	private final TabBar openDocTabs = new TabBar();

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

		openDocTabs.addStyleName(Styles.OPEN_DOCS);


		liuWidget.frmLogout.addSubmitCompleteHandler(new SubmitCompleteHandler() {
			@Override
			public void onSubmitComplete(SubmitCompleteEvent event) {
				// clear out user state and fire user session end event w/o waiting for
				// server response
				ClientModelCache.get().removeAll(EntityType.USER, liuWidget);
				//userSessionHandler.onUserSessionEvent(new UserSessionEvent(false));
				Window.Location.reload();
			}
		});

		hp.setStyleName(Styles.NAV_ROW_TBL);
		hp.add(openDocTabs);

		hpWrapper.setStyleName(Styles.NAV_ROW_TABS);
		hpWrapper.setWidget(hp);

		panel.setStyleName(Styles.NAV_ROW);

		panel.add(liuWidget);
		panel.add(hpWrapper);

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

		liuWidget.clear();
	}

	@Override
	protected void handleViewLoad(ViewKey key) {
		handlingViewChange = true;

		ViewKey crntViewKey = ViewManager.get().getCurrentViewKey();

		if(crntViewKey.getViewClass() == DocView.klas) {

			DocView newDocView = (DocView) ViewManager.get().resolveView(key);
			newDocView .doActivate();
			
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
			//mainViewTabs.selectTab(-1);

			// unload oldest view if at capacity
			if(openDocNavButtons.size() > maxNumOpenDocs) {
				openDocTabs.setVisible(false);
				ViewKey tounload = openDocNavButtons.get(openDocNavButtons.size() - 1).getViewInitializer().getViewKey();
				ViewManager.get().dispatch(new UnloadViewRequest(tounload, true, false));
			}
		}
		else {
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
		if(!mainView){
		List<? extends IViewInitializerProvider> navBtnList = openDocNavButtons;
		for(int i = 0; i < navBtnList.size(); i++) {
			ViewKey vk = navBtnList.get(i).getViewInitializer().getViewKey();
			if(vk.equals(viewKey)) return i;
		}}
		return -1;
	}


	@Override
	public void onModelChangeEvent(ModelChangeEvent event) {
		super.onModelChangeEvent(event);

		// document deleted
		if(event.getChangeOp() == ModelChangeOp.DELETED
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
		
		liuWidget.onModelChangeEvent(event);
	}
}

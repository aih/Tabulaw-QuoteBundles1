package com.tabulaw.client.app.ui.nav;

import java.util.ArrayList;

import org.cobogw.gwt.user.client.ui.VerticalTabBar;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.tabulaw.client.app.view.IPocView;
import com.tabulaw.client.model.ModelChangeEvent;
import com.tabulaw.client.model.ModelChangeEvent.ModelChangeOp;
import com.tabulaw.client.ui.login.IUserSessionHandler;
import com.tabulaw.client.view.IViewInitializerProvider;
import com.tabulaw.client.view.ShowViewRequest;
import com.tabulaw.client.view.ViewKey;
import com.tabulaw.client.view.ViewManager;
import com.tabulaw.model.EntityType;
import com.tabulaw.model.User;

public class NavTabsPanel extends AbstractNavPanel {

	public static class Style {
		public final static String NAV_TABS_PANEL = "navTabsPanel";
	}

	private final ArrayList<IViewInitializerProvider> mainViewButtons = new ArrayList<IViewInitializerProvider>();

	private final VerticalTabBar mainViewTabs = new VerticalTabBar();
	private final FlowPanel searchPanel = new FlowPanel();
	private final FlowPanel panel = new FlowPanel();

	private boolean handlingViewChange;

	public NavTabsPanel(final IUserSessionHandler userSessionHandler) {
		searchPanel.addStyleName("vpanel search-panel");
		panel.add(searchPanel);
		panel.add(mainViewTabs);
		
		initWidget(panel);
		addStyleName(Style.NAV_TABS_PANEL);

		DocsNavButton nbDocListing = new DocsNavButton();
		QuoteBundlesNavButton nbQuoteBundles = new QuoteBundlesNavButton();

		mainViewButtons.add(nbDocListing);
		mainViewButtons.add(nbQuoteBundles);

		mainViewTabs.addTab(nbDocListing);
		mainViewTabs.addTab(nbQuoteBundles);

		mainViewTabs.addSelectionHandler(new SelectionHandler<Integer>() {
			@Override
			public void onSelection(SelectionEvent<Integer> event) {
				if (!handlingViewChange) {
					showView(mainViewButtons, event.getSelectedItem()
							.intValue());
				}
			}
		});
	}

	public void clear() {
		// remove tail admin users nav button if present
		if (mainViewButtons.size() == 3) {
			mainViewButtons.remove(2);
			mainViewTabs.removeTab(2);
		}
	}

	@Override
	public void onModelChangeEvent(ModelChangeEvent event) {
		super.onModelChangeEvent(event);
		// user loaded
		if (event.getChangeOp() == ModelChangeOp.LOADED
				&& EntityType.USER.name().equals(
						event.getModelKey().getEntityType())) {
			User liu = (User) event.getModel();
			if (liu.inRole(User.Role.ADMINISTRATOR)) {
				UsersNavButton nbUsers = new UsersNavButton();
				mainViewButtons.add(nbUsers);
				mainViewTabs.addTab(nbUsers);
			}
		}
	}

	@Override
	protected void handleViewLoad(ViewKey key) {
		changeSearchWidget(key); 
	}

	@Override
	protected void handleViewUnload(ViewKey key) {
		handlingViewChange = false;
	}
	private void changeSearchWidget(ViewKey key) {
		//clear
		if (searchPanel.getWidgetCount()>0) {
			searchPanel.remove(0);
		}
		IPocView<?> view = (IPocView<?>) ViewManager.get().resolveView(key);
		if (view != null) {
			Widget searchWidget = view.getSearchWidget();
			if (searchWidget != null) {
				searchPanel.add(searchWidget);
			}
		}
	}

	private static void showView(
			ArrayList<? extends IViewInitializerProvider> list, int index) {
		ViewManager.get().dispatch(
				new ShowViewRequest(list.get(index).getViewInitializer()));
	}
}

package com.tabulaw.client.app.ui.nav;

import java.util.ArrayList;

import org.cobogw.gwt.user.client.ui.VerticalTabBar;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.tabulaw.client.app.view.DocView;
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

	private final ArrayList<IViewInitializerProvider> mainViewButtons = new ArrayList<IViewInitializerProvider>();
	// private final TabBar mainViewTabs = new TabBar();
	private final VerticalTabBar mainViewTabs = new VerticalTabBar();

	private boolean handlingViewChange;

	public NavTabsPanel(final IUserSessionHandler userSessionHandler) {
		super();

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
		initWidget(mainViewTabs);
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
		handlingViewChange = true;
		ViewKey crntViewKey = ViewManager.get().getCurrentViewKey();
		int i = 0;
		if (crntViewKey.getViewClass() != DocView.klas) {
			for (IViewInitializerProvider navBtn : mainViewButtons) {
				mainViewTabs.setTabEnabled(i, true);
				ViewKey aViewKey = navBtn.getViewInitializer().getViewKey();
				if (crntViewKey.equals(aViewKey)) {
					mainViewTabs.selectTab(i);
					mainViewTabs.setTabEnabled(i, false);
					break;
				}
				i++;
			}
		}
	}

	@Override
	protected void handleViewUnload(ViewKey key) {
		handlingViewChange = false;
	}

	private static void showView(
			ArrayList<? extends IViewInitializerProvider> list, int index) {
		ViewManager.get().dispatch(
				new ShowViewRequest(list.get(index).getViewInitializer()));
	}
}

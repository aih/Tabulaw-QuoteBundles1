package com.tabulaw.client.app.ui;

import java.util.List;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;
import com.tabulaw.client.app.Poc;
import com.tabulaw.client.app.model.ClientModelCache;
import com.tabulaw.client.model.IModelChangeHandler;
import com.tabulaw.client.model.ModelChangeEvent;
import com.tabulaw.client.model.ModelChangeEvent.ModelChangeOp;
import com.tabulaw.client.ui.Notifier;
import com.tabulaw.model.EntityType;
import com.tabulaw.model.QuoteBundle;

public class BundleQuotesMenuBar extends MenuBar {

	private final static BundleQuotesMenuBar MENU_BAR = new BundleQuotesMenuBar();

	public BundleQuotesMenuBar() {
		super(true);
		update();
		Poc.getPortal().addModelChangeHandler(new IModelChangeHandler() {
			@Override
			public void onModelChangeEvent(ModelChangeEvent event) {
				update();
			}
		});
	}

	@SuppressWarnings("unchecked")
	public void update() {
		clearItems();
		List<QuoteBundle> qbs = (List<QuoteBundle>) ClientModelCache.get()
				.getAll(EntityType.QUOTE_BUNDLE);
		for (final QuoteBundle qb : qbs) {
			addItem(qb.getName(), new Command() {
				@Override
				public void execute() {
					if (ClientModelCache.get().getUserState()
							.setCurrentQuoteBundleId(qb.getId())) {
						Notifier.get().info("Current Quote Bundle set.");
						Poc.fireModelChangeEvent(new ModelChangeEvent(
								BundleQuotesMenuBar.this,
								ModelChangeOp.UPDATED, qb, null));
					}
				}
			});
		}
	}

	public static MenuBar getBundleQuotesMenuBar() {
		return MENU_BAR;
	}
}
 
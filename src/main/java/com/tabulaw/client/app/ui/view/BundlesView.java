/**
 * The Logic Lab
 * @author jpk
 * @since Feb 11, 2010
 */
package com.tabulaw.client.app.ui.view;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.tabulaw.client.app.Resources;
import com.tabulaw.client.app.ui.AddBundleDialog;
import com.tabulaw.client.app.ui.BundlesManageWidget;
import com.tabulaw.client.ui.ImageButton;
import com.tabulaw.client.view.StaticViewInitializer;
import com.tabulaw.client.view.ViewClass;

/**
 * A view for managing existing doc bundles.
 * @author jpk
 */
public class BundlesView extends AbstractPocView<StaticViewInitializer> {
	
	public static final Class klas = new Class();

	public static final class Class extends PocViewClass {

		@Override
		public String getName() {
			return "quoteBundle";
		}

		@Override
		public BundlesView newView() {
			return new BundlesView();
		}
	}
	
	static class NewQuoteBundleButton extends ImageButton {
		
		private AddBundleDialog dialog;
		
		public NewQuoteBundleButton() {
			super(Resources.INSTANCE.plus(), "New Quote Bundle");
			addStyleDependentName("quoteBundle");
			setTitle("Create a Quote Bundle...");
			addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					if(dialog == null) {
						dialog = new AddBundleDialog();
						dialog.setGlassEnabled(true);
					}
					dialog.showRelativeTo(NewQuoteBundleButton.this);
				}
			});
		}
	}
	
	private final Widget[] navColWidgets;
	
	private final BundlesManageWidget qbPanel;
	
	/**
	 * Constructor
	 */
	public BundlesView() {
		super();
		qbPanel = new BundlesManageWidget();
		
		HTML viewName = new HTML(getLongViewName());
		viewName.setStyleName("viewTitle");
		
		navColWidgets = new Widget[] {
			viewName,
			qbPanel.getQuoteBundleListingWidget(),
			new NewQuoteBundleButton(),
		};
		
		addWidget(qbPanel);
	}

	@Override
	public ViewClass getViewClass() {
		return klas;
	}

	@Override
	public String getLongViewName() {
		return "Quote Bundles";
	}

	@Override
	public Widget[] getNavColWidgets() {
		return navColWidgets;
	}

	@Override
	protected void doInitialization(StaticViewInitializer initializer) {
		qbPanel.makeModelChangeAware();
	}

	@Override
	protected void doDestroy() {
		super.doDestroy();
		qbPanel.unmakeModelChangeAware();
	}

	@Override
	protected void doRefresh() {
		qbPanel.refresh();
	}
}

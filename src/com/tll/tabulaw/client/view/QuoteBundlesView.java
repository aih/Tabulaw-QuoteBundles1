/**
 * The Logic Lab
 * @author jpk
 * @since Feb 11, 2010
 */
package com.tll.tabulaw.client.view;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.tll.client.model.ModelChangeEvent;
import com.tll.client.mvc.view.IViewInitializer;
import com.tll.client.mvc.view.StaticViewInitializer;
import com.tll.client.mvc.view.ViewClass;
import com.tll.tabulaw.client.ui.AddQuoteBundleDialog;
import com.tll.tabulaw.client.ui.QuoteBundlesManageWidget;
import com.tll.tabulaw.client.ui.nav.AbstractNavButton;

/**
 * A view for managing existing doc bundles.
 * @author jpk
 */
public class QuoteBundlesView extends AbstractPocView<StaticViewInitializer> {
	
	public static final Class klas = new Class();

	public static final class Class extends PocViewClass {

		@Override
		public String getName() {
			return "quoteBundle";
		}

		@Override
		public QuoteBundlesView newView() {
			return new QuoteBundlesView();
		}
	}
	
	class NewQuoteBundleButton extends AbstractNavButton {
		
		AddQuoteBundleDialog dialog;
		
		public NewQuoteBundleButton() {
			super("New Quote Bundle", Styles.PLUS);
			dialog = new AddQuoteBundleDialog();
			dialog.setGlassEnabled(true);
			setClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					dialog.showRelativeTo(NewQuoteBundleButton.this);
				}
			});
		}

		@Override
		protected String getTitleText(String buttonText) {
			return "Create a Quote Bundle...";
		}

		@Override
		protected IViewInitializer getViewInitializer() {
			throw new UnsupportedOperationException();
		}
	}
	
	private final Widget[] navColWidgets;
	
	private final QuoteBundlesManageWidget movePanel;
	
	/**
	 * Constructor
	 */
	public QuoteBundlesView() {
		super();
		movePanel = new QuoteBundlesManageWidget();
		
		HTML viewName = new HTML(getLongViewName());
		viewName.setStyleName("viewTitle");
		
		navColWidgets = new Widget[] {
			viewName,
			movePanel.getQuoteBundleListingWidget(),
			new NewQuoteBundleButton(),
		};
		
		addWidget(movePanel);
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
		// default add the first two quote bundles to the move panel
	}

	@Override
	protected void doRefresh() {
		movePanel.refresh();
	}

	@Override
	protected void handleModelChange(ModelChangeEvent event) {
		movePanel.onModelChangeEvent(event);
	}
}

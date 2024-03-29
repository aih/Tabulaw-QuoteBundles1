/**
 * Copyright (C) Tabulaw, Inc. 2009-2010 All Rights Reserved
 * @author jpk
 * @since Feb 11, 2010
 */
package com.tabulaw.client.app.view;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.tabulaw.client.app.Resources;
import com.tabulaw.client.app.ui.AddBundleDialog;
import com.tabulaw.client.app.ui.BundlesManageWidget;
import com.tabulaw.client.app.ui.SearchWidget;
import com.tabulaw.client.ui.EmbedableDockLayoutPanel;
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
			return "bundles";
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
		
		
		navColWidgets = new Widget[] {buildBundleListLayout()};
		
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
	public Widget getSearchWidget() {
		return new SearchWidget();
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
	private Widget buildBundleListLayout(){
		HTML viewName = new HTML(getLongViewName());
		viewName.setStyleName("viewTitle");

		DockLayoutPanel navColWidget = new EmbedableDockLayoutPanel(Unit.PX, 300);
		FlowPanel north =  new FlowPanel();
		north.add(viewName);
		north.add(new NewQuoteBundleButton());
		navColWidget.addNorth(north, 70);
		navColWidget.add(qbPanel.getQuoteBundleListingWidget());
		return navColWidget;
	}
}

/**
 * Copyright (C) Tabulaw, Inc. 2009-2010 All Rights Reserved
 * @author jpk
 * @since Feb 12, 2010
 */
package com.tabulaw.client.app.ui;

import com.google.gwt.user.client.ui.Composite;
import com.tabulaw.client.ui.VerticalPanelWithSpacer;
import com.tabulaw.client.ui.option.Option;

/**
 * Displays quote bundles vertically by title.
 * @author jpk
 */
public final class BundleListingWidget extends Composite {

	public static class Styles {

		/**
		 * The id of the {@link BundleListingWidget} widget in the dom.
		 */
		public static final String ID = "qbListing";
	}

	public static class BOption extends Option {

		final String bundleId;

		public BOption(String bundleId, String bundleName) {
			super(bundleName);
			this.bundleId = bundleId;
			setTitle("Click or drag to edit");
		}

		public String getBundleId() {
			return bundleId;
		}

		public String getBundleName() {
			return getText();
		}
	} // BOption

	static class BundleOptionsPanel extends VerticalPanelWithSpacer {
		
		public void addOption(BOption option) {
			add(option);
		}
		
		public void removeOption(BOption option) {
			remove(option);
		}
		
		public BOption[] getOptions() {
			BOption[] arr = new BOption[getWidgetCount()];
			for(int i = 0; i < arr.length; i++) {
				arr[i] = (BOption) getWidget(i);
			}
			return arr;
		}
		
		public void clearOptions() {
			clear();
		}
	} // BundleOptionsPanel

	private final BundleOptionsPanel bundleOptionsPanel;

	/**
	 * Constructor
	 */
	public BundleListingWidget() {
		super();
		bundleOptionsPanel = new BundleOptionsPanel();
		bundleOptionsPanel.getElement().setId(BundleListingWidget.Styles.ID);
		initWidget(bundleOptionsPanel);
	}
	
	public BundleOptionsPanel getOptionsPanel() {
		return bundleOptionsPanel;
	}
}

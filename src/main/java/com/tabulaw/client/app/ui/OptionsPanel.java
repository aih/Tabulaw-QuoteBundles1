/**
 * The Logic Lab
 * @author jopaki
 * @since Aug 31, 2010
 */
package com.tabulaw.client.app.ui;

import com.tabulaw.client.ui.VerticalPanelWithSpacer;
import com.tabulaw.client.ui.option.Option;

/**
 * Lists {@link Option} widgets in a vertical panel that supports drag and drop
 * ops via the base class: {@link VerticalPanelWithSpacer}
 * @author jopaki
 */
public class OptionsPanel extends VerticalPanelWithSpacer {

	/**
	 * Constructor
	 */
	public OptionsPanel() {
		super();
	}

	public Option[] getOptions() {
		Option[] arr = new Option[getWidgetCount()];
		for(int i = 0; i < arr.length; i++) {
			arr[i] = (Option) getWidget(i);
		}
		return arr;
	}
}
/**
 * The Logic Lab
 * @author jpk
 * @since Oct 17, 2009
 */
package com.tabulaw.client.ui.impl;

import com.google.gwt.user.client.Window;
import com.tabulaw.client.ui.GlassPanel;


/**
 * GlassPanelImplStandard
 * @author jpk
 */
public class GlassPanelImplStandard extends GlassPanelImpl {
	@Override
	public void matchDocumentSize(GlassPanel glassPanel, boolean dueToResize) {
		final int clientWidth = Window.getClientWidth();
		final int clientHeight = Window.getClientHeight();

		final int scrollWidth = getWindowScrollWidth();
		final int scrollHeight = getWindowScrollHeight();

		final int width = Math.max(clientWidth, scrollWidth);
		final int height = Math.max(clientHeight, scrollHeight);

		glassPanel.setPixelSize(width - 1, height - 1);
	}
}

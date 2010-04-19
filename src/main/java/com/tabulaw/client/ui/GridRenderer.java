package com.tabulaw.client.ui;

import java.util.Collection;

import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

/**
 * GridRenderer
 * @author jpk
 */
public class GridRenderer implements IWidgetRenderer {

	private final int numCols;
	private final String panelStyle;

	/**
	 * Constructor
	 * @param numCols The number of columns. If <code>-1</code>, the number of
	 *        columns is unbounded and all cells are rendered horizontally in one
	 *        row.
	 * @param panelStyle Optional style applied to the rendered grid.
	 */
	public GridRenderer(int numCols, String panelStyle) {
		super();
		this.numCols = numCols;
		this.panelStyle = panelStyle;
	}

	public Panel render(Collection<? extends Widget> wclc) {
		if(numCols == -1) {
			// unbounded columns
			final HorizontalPanel hp = new HorizontalPanel();
			if(panelStyle != null) hp.setStyleName(panelStyle);
			for(final Widget rb : wclc) {
				hp.add(rb);
			}
			return hp;
		}

		// grid
		final int numRows = (int) Math.ceil((double) wclc.size() / (double) numCols);
		final Grid grid = new Grid(numRows, numCols);
		if(panelStyle != null) grid.setStyleName(panelStyle);
		int row, col;
		row = col = 0;
		for(final Widget rb : wclc) {
			if(col == numCols) {
				// new row
				row++;
				col = 0;
			}
			grid.setWidget(row, col++, rb);
		}
		return grid;
	}
}
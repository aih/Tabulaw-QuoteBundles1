/**
 * The Logic Lab
 * @author jpk
 * Dec 15, 2007
 */
package com.tabulaw.client.ui;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Focusable;

/**
 * FocusCommand
 * @author jpk
 */
public class FocusCommand implements Command {

	protected final Focusable w;
	protected final boolean focus;

	/**
	 * Constructor
	 * @param w
	 * @param focus
	 */
	public FocusCommand(Focusable w, boolean focus) {
		super();
		this.w = w;
		this.focus = focus;
	}

	public void execute() {
		w.setFocus(focus);
	}

}

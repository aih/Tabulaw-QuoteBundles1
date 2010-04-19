/**
 * The Logic Lab
 * @author jpk
 * @since Oct 17, 2009
 */
package com.tabulaw.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.tabulaw.client.ui.impl.GlassPanelImpl;

/**
 * GlassPanel - Clone of Fred Sauer's GlassPanel with upfitting for GWT 2.0
 * <h3>CSS Style Rules</h3>
 * <ul>
 * <li>.tll-GlassPanel { the glass panel itself }</li>
 * </ul>
 * @author jpk
 */
public class GlassPanel extends Composite implements NativePreviewHandler {

	/**
	 * A FocusPanel which automatically focuses itself when attached (in order to
	 * blur any currently focused widget) and then removes itself.
	 */
	private static class FocusPanelImpl extends FocusPanel {

		public FocusPanelImpl() {
			addBlurHandler(new BlurHandler() {

				@Override
				public void onBlur(BlurEvent event) {
					FocusPanelImpl.this.removeFromParent();
				}

			});
		}

		@Override
		protected void onLoad() {
			super.onLoad();
			/**
			 * Removed DeferredCommand if/when GWT issue 1849 is implemented
			 * http://code.google.com/p/google-web-toolkit/issues/detail?id=1849
			 */
			DeferredCommand.addCommand(new Command() {

				public void execute() {
					setFocus(true);
				}
			});
		}
	}

	static final GlassPanelImpl impl = GWT.create(GlassPanelImpl.class);

	/**
	 * Creates an {@link AbsolutePanel} that overlays the given widget and is
	 * added to the {@link RootPanel}.
	 * @param w The target widget
	 * @return Newly created {@link AbsolutePanel} added to the {@link RootPanel}.
	 */
	public static AbsolutePanel createOverlay(Widget w) {
		final AbsolutePanel ap = new AbsolutePanel();
		ap.setPixelSize(w.getOffsetWidth(), w.getOffsetHeight());
		RootPanel.get().add(ap, w.getAbsoluteLeft(), w.getAbsoluteTop());
		return ap;
	}

	private HandlerRegistration hrResize, hrNp;

	private final boolean autoHide;

	private final SimplePanel pnl;

	/*
	private final Timer timer = new Timer() {

		@Override
		public void run() {
			impl.matchDocumentSize(GlassPanel.this, false);
		}
	};
	 */

	/**
	 * Create a glass panel widget that can be attached to an AbsolutePanel via
	 * {@link AbsolutePanel#add(com.google.gwt.user.client.ui.Widget, int, int)
	 * absolutePanel.add(glassPanel, 0, 0)}.
	 * @param autoHide <code>true</code> if the glass panel should be automatically
	 *        hidden when the user clicks on it or presses <code>ESC</code>.
	 */
	public GlassPanel(boolean autoHide) {
		this.autoHide = autoHide;
		pnl = new SimplePanel();
		initWidget(pnl);
		setStyleName("tll-GlassPanel");
	}

	@Override
	public void onPreviewNativeEvent(NativePreviewEvent event) {
		switch(event.getTypeInt()) {
		case Event.ONKEYDOWN: {
			//case Event.ONKEYPRESS: {
			if(event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ESCAPE) {
				removeFromParent();
				event.cancel();
			}
		}
		case Event.ONCLICK: {
			if(DOM.isOrHasChild(getElement(), DOM.eventGetTarget(Event.as(event.getNativeEvent())))) {
				removeFromParent();
				event.cancel();
			}
		}
		}
	}

	@Override
	public void setWidget(Widget widget) {
		pnl.setWidget(widget);
	}

	@Override
	protected void onAttach() {
		super.onAttach();
		AbsolutePanel parent;
		try {
			parent = (AbsolutePanel) getParent();
		}
		catch(final RuntimeException e) {
			throw new IllegalStateException("Parent widget must be an instance of AbsolutePanel");
		}

		if(parent == RootPanel.get()) {
			impl.matchDocumentSize(this, false);
			//timer.scheduleRepeating(100);
			hrResize = Window.addResizeHandler(new ResizeHandler() {

				@Override
				public void onResize(ResizeEvent event) {
					impl.matchDocumentSize(GlassPanel.this, true);
				}
			});
		}
		else {
			impl.matchParentSize(this, parent);
		}
		if(autoHide) {
			hrNp = Event.addNativePreviewHandler(this);
		}

		RootPanel.get().add(new FocusPanelImpl(), Window.getScrollLeft(), Window.getScrollTop());
	}

	@Override
	protected void onDetach() {
		super.onDetach();
		//timer.cancel();
		if(hrResize != null) {
			hrResize.removeHandler();
			hrResize = null;
		}
		if(hrNp != null) {
			hrNp.removeHandler();
		}
	}
}

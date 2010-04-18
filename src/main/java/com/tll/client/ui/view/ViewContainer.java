/**
 * The Logic Lab
 * @author jpk Mar 13, 2008
 */
package com.tll.client.ui.view;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.tll.client.mvc.ViewManager;
import com.tll.client.mvc.view.IView;
import com.tll.client.mvc.view.IViewState;
import com.tll.client.mvc.view.PinPopViewRequest;
import com.tll.client.mvc.view.UnloadViewRequest;
import com.tll.client.mvc.view.ViewKey;
import com.tll.client.mvc.view.ViewOptions;
import com.tll.client.ui.DragEvent;
import com.tll.client.ui.IDragHandler;
import com.tll.client.ui.IHasDragHandlers;
import com.tll.client.ui.DragEvent.DragMode;

/**
 * ViewContainer - UI container for {@link IView} implementations.
 * @author jpk
 */
@SuppressWarnings("synthetic-access")
public final class ViewContainer extends SimplePanel implements MouseDownHandler, MouseMoveHandler,
MouseUpHandler, IHasDragHandlers, ClickHandler, NativePreviewHandler {

	/**
	 * Styles - (view.css)
	 * @author jpk
	 */
	protected static class Styles {

		/**
		 * Primary style applied to the widget that is the view container.
		 */
		public static final String VIEW_CONTAINER = "viewContainer";
		/**
		 * Secondary style for view container's in the popped state.
		 */
		public static final String POPPED = "popped";
		/**
		 * Secondary style for view container's in the pinned state.
		 */
		public static final String PINNED = "pinned";

	} // Styles

	/**
	 * The wrapped IView
	 */
	private final IView<?> view;

	private final ViewKey key;

	private final ViewToolbar toolbar;

	private final MyVerticalPanel mainLayout = new MyVerticalPanel();

	private boolean mouseIsDown, dragging;

	private int dragOffsetX, dragOffsetY;

	private HandlerRegistration hrEventPreview, hrMouseDown, hrMouseMove, hrMouseUp;

	private final com.google.gwt.user.client.Element dragTarget;

	/**
	 * MyVerticalPanel - Simple extension of VerticalPanel to get at td and tr
	 * table elements for blyhme's sake!
	 * @author jpk
	 */
	private static final class MyVerticalPanel extends VerticalPanel {

		Element getTd(Widget w) {
			if(w.getParent() != this) {
				return null;
			}
			return w.getElement().getParentElement();
		}

		Element getWidgetTr(Widget w) {
			final Element td = getTd(w);
			return td == null ? null : td.getParentElement();
		}
	}

	/**
	 * Constructor
	 * @param view The view def
	 * @param options The view display options
	 * @param key The view key
	 */
	public ViewContainer(IView<?> view, ViewOptions options, ViewKey key) {
		super();
		if(view == null || key == null) throw new IllegalArgumentException("Null view and/or view key");
		this.view = view;
		this.key = key;
		toolbar = new ViewToolbar(view.getLongViewName(), options, this);
		dragTarget = toolbar.viewTitle.getElement();
		mainLayout.add(toolbar);
		mainLayout.add(view.getViewWidget());
		mainLayout.setStylePrimaryName(Styles.VIEW_CONTAINER);
		setWidget(mainLayout);
	}

	@Override
	public void onPreviewNativeEvent(NativePreviewEvent event) {
		// NOTE: we should only be in the popped state for previewing events
		// assert isPopped() == true; (commented out for performance - but asserts
		// ok)

		final int type = event.getTypeInt();
		final NativeEvent ne = event.getNativeEvent();
		final Element target = Element.as(ne.getEventTarget());
		final boolean eventTargetsPopup = getElement().isOrHasChild(target);

		switch(type) {
			case Event.ONMOUSEDOWN:
				// We need to preventDefault() on mouseDown events (outside of the
				// DialogBox content) to keep text from being selected when it
				// is dragged.
				if(target.isOrHasChild(dragTarget)) {
					// Log.debug("ViewContainer.onPreviewNativeEvent() - preventing default..");
					ne.preventDefault();
				}
			case Event.ONMOUSEUP:
			case Event.ONMOUSEMOVE:
			case Event.ONCLICK:
			case Event.ONDBLCLICK: {
				// Don't eat events if event capture is enabled, as this can interfere
				// with dialog dragging, for example.
				if(DOM.getCaptureElement() != null) {
					//Log.debug("ViewContainer.onPreviewNativeEvent() - we're in capture mode..");
					return;
				}
				break;
			}
		}

		// debug logging
		/*
		if(dragging) {
			String msg = "t:" + type + " tg?:" + eventTargetsPopup + " rvl:" + rval;
			StatusDisplay.log(new Msg(msg, Msg.LEVEL_INFO));
		}
		 */

		// NOTE: we dis-allow UI interaction with content NOT contained w/in this
		// view container!
		if(!eventTargetsPopup) {
			//Log.debug("ViewContainer.onPreviewNativeEvent() - cancelling event..");
			event.cancel();
		}
	}

	/**
	 * @return the view
	 */
	public IView<?> getView() {
		return view;
	}

	public ViewKey getViewKey() {
		return key;
	}

	public IViewState getViewState() {
		return new IViewState() {

			public boolean isPopped() {
				return ViewContainer.this.isPopped();
			}

			public boolean isMinimized() {
				return ViewContainer.this.isMinimized();
			}

		};
	}

	/**
	 * @return The contained view toolbar.
	 */
	public ViewToolbar getToolbar() {
		return toolbar;
	}

	@Override
	public HandlerRegistration addDragHandler(IDragHandler handler) {
		return addHandler(handler, DragEvent.TYPE);
	}

	public boolean isPopped() {
		return RootPanel.get().getElement() == getElement().getParentElement();
	}

	public boolean isMinimized() {
		return "none".equals(mainLayout.getWidgetTr(view.getViewWidget()).getStyle().getProperty("display"));
	}

	private void endDrag() {
		if(mouseIsDown) {
			Log.debug("ending dragging..");
			DOM.releaseCapture(dragTarget);
			mouseIsDown = dragging = false;
			dragOffsetX = dragOffsetY = -1;
		}
	}

	public void onMouseDown(MouseDownEvent event) {
		if(isPopped()) {
			endDrag();
			DOM.setCapture(dragTarget);
			Log.debug("set drag target: " + dragTarget.getInnerText());

			event.stopPropagation();
			mouseIsDown = true;
			dragOffsetX = event.getClientX() - getAbsoluteLeft();
			dragOffsetY = event.getClientY() - getAbsoluteTop();
			//Log.debug("onMouseDown() - dragOffsetX:" + dragOffsetX + ",dragOffsetY:" + dragOffsetY);
		}
	}

	public void onMouseMove(MouseMoveEvent event) {
		final int dc = getHandlerCount(DragEvent.TYPE);
		final boolean fireDrag = (dc > 0);
		if(mouseIsDown) {
			if(!dragging) {
				dragging = true;
				//Log.debug("onMouseMove() - drag start..");
				if(fireDrag) fireEvent(new DragEvent(DragMode.START, dragOffsetX, dragOffsetY));
			}

			final int x = event.getClientX(), y = event.getClientY();
			int nx = x - dragOffsetX;
			int ny = y - dragOffsetY;

			// keep the drag handle within the viewable area!
			if(nx < 0) nx = 0;
			if(ny < 0) ny = 0;
			//Log.debug("onMouseMove() - x:" + x + ",y:" + y + " | nx:" + nx + ",ny:" + ny);

			final int bx = fireDrag ? getAbsoluteLeft() : 0;
			final int by = fireDrag ? getAbsoluteTop() : 0;

			final Style es = getElement().getStyle();
			es.setPropertyPx("left", nx);
			es.setPropertyPx("top", ny);

			if(fireDrag) {
				//Log.debug("onMouseMove() deltaX:" + (nx - bx) + ",deltaY:" + (ny - by));
				fireEvent(new DragEvent(DragMode.DRAGGING, nx - bx, ny - by));
			}
		}
	}

	public void onMouseUp(MouseUpEvent event) {
		if(mouseIsDown) {
			endDrag();
			if(getHandlerCount(DragEvent.TYPE) > 0) fireEvent(new DragEvent(DragMode.END));
		}
	}

	/**
	 * Pops the view container out of the natural DOM layout making its
	 * positioning absolute and and adding it to the {@link RootPanel} using the
	 * panel's existing position.
	 * @param refWidget The reference Widget used to determine the popped
	 *        position.
	 */
	public void pop(Widget refWidget) {
		assert refWidget != null;
		pop(refWidget.getAbsoluteTop() + 15, refWidget.getAbsoluteLeft() + 15);
	}

	/**
	 * Pops the view container out of the natural DOM layout making its
	 * positioning absolute and and adding it to the {@link RootPanel}.
	 * @param top The top pixel position
	 * @param left The left pixel position
	 */
	private void pop(int top, int left) {
		if(!isPopped()) {
			assert top > 0 && left > 0;

			mainLayout.removeStyleDependentName(Styles.PINNED);
			mainLayout.addStyleDependentName(Styles.POPPED);

			final int width = getWidget().getOffsetWidth();
			// int width = getWidget().getOffsetWidth();
			// int height = getWidget().getOffsetHeight();

			final Element elm = getElement();
			elm.getStyle().setProperty("position", "absolute");
			elm.getStyle().setPropertyPx("left", left);
			elm.getStyle().setPropertyPx("top", top);
			elm.getStyle().setPropertyPx("width", width);
			elm.getStyle().setProperty("height", "");

			RootPanel.get().add(this);
			// DOM.setStyleAttribute(elm, "width", getWidget().getOffsetWidth() +
			// "px");

			if(toolbar.btnMinimize != null) {
				toolbar.btnMinimize.setDown(false);
				toolbar.show(toolbar.btnMinimize, true);
			}

			assert toolbar.btnPop != null;
			toolbar.btnPop.setDown(true);
			toolbar.btnPop.setTitle(ViewToolbar.TITLE_PIN);

			//assert hrMouseDown == hrMouseMove == hrMouseUp == null;
			hrMouseDown = toolbar.addMouseDownHandler(this);
			hrMouseMove = toolbar.addMouseMoveHandler(this);
			hrMouseUp = toolbar.addMouseUpHandler(this);

			assert hrEventPreview == null;
			hrEventPreview = Event.addNativePreviewHandler(this);
		}
	}
	
	/**
	 * Sets the state of this container to be ready for pinning to a Panel.
	 */
	public void makePinReady() {
		if(hrEventPreview != null) {
			hrEventPreview.removeHandler();
			hrEventPreview = null;
		}

		if(hrMouseDown != null) {
			hrMouseDown.removeHandler();
			hrMouseMove.removeHandler();
			hrMouseUp.removeHandler();
			hrMouseDown = hrMouseMove = hrMouseUp = null;
		}

		final Element elm = getElement();
		elm.getStyle().setProperty("position", "");
		elm.getStyle().setProperty("left", "");
		elm.getStyle().setProperty("top", "");
		elm.getStyle().setProperty("width", "");
		elm.getStyle().setProperty("height", "");

		mainLayout.removeStyleDependentName(Styles.POPPED);
		mainLayout.addStyleDependentName(Styles.PINNED);

		maximize();
		if(toolbar.btnMinimize != null) {
			toolbar.show(toolbar.btnMinimize, false);
		}

		if(toolbar.btnPop != null) {
			toolbar.btnPop.setDown(false);
			toolbar.btnPop.setTitle(ViewToolbar.TITLE_POP);
		}
	}

	/**
	 * Minimizes the view
	 */
	public void minimize() {
		if(!isMinimized()) {
			mainLayout.getWidgetTr(view.getViewWidget()).getStyle().setProperty("display", "none");
			toolbar.btnMinimize.setTitle(ViewToolbar.TITLE_MAXIMIZE);
		}
	}

	/**
	 * Maximizes the view
	 */
	public void maximize() {
		if(isMinimized()) {
			mainLayout.getWidgetTr(view.getViewWidget()).getStyle().setProperty("display", "");
			toolbar.btnMinimize.setTitle(ViewToolbar.TITLE_MINIMIZE);
		}
	}

	/**
	 * Removes this instance from its parent and clears its state.
	 */
	public void close() {
		removeFromParent();
		if(hrEventPreview != null) {
			hrEventPreview.removeHandler();
			hrEventPreview = null;
		}
	}

	public void onClick(ClickEvent event) {
		final Object sender = event.getSource();

		// pop the view
		if(sender == toolbar.btnPop) {
			final boolean popped = isPopped();
			ViewManager.get().dispatch(new PinPopViewRequest(key, !popped));
		}

		// close the view
		else if(sender == toolbar.btnClose) {
			ViewManager.get().dispatch(new UnloadViewRequest(key, true, false));
		}

		// minimize/mazimize the view
		else if(sender == toolbar.btnMinimize) {
			if(toolbar.btnMinimize.isDown()) {
				minimize();
			}
			else {
				maximize();
			}
		}

		// refresh the view
		else if(sender == toolbar.btnRefresh) {
			view.refresh();
		}
	}

	@Override
	public String toString() {
		return "ViewContainer [" + view.toString() + "]";
	}
}

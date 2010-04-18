/**
 * The Logic Lab
 * @author jpk Dec 6, 2007
 */
package com.tll.client.ui.option;

import java.util.HashMap;
import java.util.HashSet;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * OptionsPanel - Panel containing a vertical list of options that are
 * selectable via mouse and keyboard.
 * @param <O> option type
 * @author jpk
 */
public class OptionsPanel<O extends Option> extends FocusPanel implements KeyDownHandler, MouseDownHandler, MouseOverHandler,
MouseOutHandler, IHasOptionHandlers {

	/**
	 * Styles - (options.css)
	 * @author jpk
	 */
	protected static class Styles {

		public static final String OPTIONS = "options";

		public static final String ACTIVE = "active";
	}

	/**
	 * MRegs
	 * @author jpk
	 */
	static class MRegs {

		final HandlerRegistration down, out, over;

		public MRegs(HandlerRegistration down, HandlerRegistration out, HandlerRegistration over) {
			super();
			this.down = down;
			this.out = out;
			this.over = over;
		}
	} // MRegs

	protected final HashMap<O, MRegs> options = new HashMap<O, MRegs>();
	protected final VerticalPanel vp = new VerticalPanel();
	protected int crntIndx = -1;

	/**
	 * Constructor
	 */
	public OptionsPanel() {
		super();
		setWidget(vp);
		addKeyDownHandler(this);
		setStyleName(Styles.OPTIONS);
	}

	@Override
	public HandlerRegistration addOptionHandler(IOptionHandler handler) {
		return addHandler(handler, OptionEvent.TYPE);
	}

	/**
	 * Adds a single Option
	 * @param option The Option to add
	 */
	protected void addOption(O option) {
		final MRegs mreg =
			new MRegs(option.addMouseDownHandler(this), option.addMouseOutHandler(this), option.addMouseOverHandler(this));
		options.put(option, mreg);
		vp.add(option);
	}
	
	/**
	 * Removes a single option
	 * @param option the option to remove
	 */
	protected void removeOption(O option) {
		vp.remove(option);
		MRegs m = options.get(option);
		m.down.removeHandler();
		m.out.removeHandler();
		m.over.removeHandler();
		options.remove(option);
	}

	protected final void clearOptions() {
		crntIndx = -1;
		HashSet<O> tormv = new HashSet<O>(options.keySet());
		for(final O option : tormv) {
			removeOption(option);
		}
	}

	/**
	 * Sets the {@link Option}s in this panel given an array of them. Clears any
	 * existing options before the new ones are added.
	 * @param options Array of {@link Option}s to be set
	 */
	public final void setOptions(O[] options) {
		clearOptions();
		if(options != null) {
			for(final O element2 : options) {
				addOption(element2);
			}
		}
	}

	protected void clearCurrentOption() {
		if(crntIndx != -1) {
			vp.getWidget(crntIndx).getElement().getParentElement().setClassName("");
			crntIndx = -1;
		}
	}

	protected void setCurrentOption(int index, boolean fireCurrentOptionChanged) {
		if(crntIndx != -1 && crntIndx == index) {
			return;
		}
		else if(index > options.size() - 1) {
			index = 0;
		}
		else if(index < 0) {
			index = options.size() - 1;
		}

		// unset current
		if(crntIndx != -1) {
			vp.getWidget(crntIndx).getElement().getParentElement().setClassName("");
		}

		// set new current
		final Option crntOption = (Option) vp.getWidget(index);
		crntOption.getElement().getParentElement().setClassName(Styles.ACTIVE);
		this.crntIndx = index;

		if(fireCurrentOptionChanged) {
			fireEvent(new OptionEvent(OptionEvent.EventType.CHANGED, crntOption.getText()));
		}
	}

	public void onKeyDown(KeyDownEvent event) {
		switch(event.getNativeKeyCode()) {
			case KeyCodes.KEY_UP:
				setCurrentOption(crntIndx - 1, true);
				break;
			case KeyCodes.KEY_DOWN:
				setCurrentOption(crntIndx + 1, true);
				break;
			case KeyCodes.KEY_ENTER:
				if(crntIndx >= 0) {
					fireEvent(new OptionEvent(OptionEvent.EventType.SELECTED, ((Option) vp.getWidget(crntIndx)).getText()));
				}
				break;
		}
	}

	public void onMouseDown(MouseDownEvent event) {
		final int index = vp.getWidgetIndex((Option) event.getSource());
		if(index >= 0) {
			setCurrentOption(index, false);
			fireEvent(new OptionEvent(OptionEvent.EventType.SELECTED, ((Option) event.getSource()).getText()));
		}
	}

	public void onMouseOver(MouseOverEvent event) {
		final int index = vp.getWidgetIndex((Option) event.getSource());
		if(index >= 0) {
			setCurrentOption(index, false);
			fireEvent(new OptionEvent(OptionEvent.EventType.CHANGED, ((Option) event.getSource()).getText()));
		}
	}

	@Override
	public void onMouseOut(MouseOutEvent event) {
		final int index = vp.getWidgetIndex((Option) event.getSource());
		if(index >= 0) {
			clearCurrentOption();
		}
	}

	@Override
	protected void onLoad() {
		super.onLoad();
		clearCurrentOption();
	}

}

/**
 * The Logic Lab
 * @author jpk
 * Nov 5, 2007
 */
package com.tabulaw.client.ui.field;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.ListBox;

/**
 * MultiSelectField
 * @param <V> the data element value type
 * @author jpk
 */
public final class MultiSelectField<V> extends AbstractCollectionDataField<V> {

	/**
	 * Impl
	 * @author jpk
	 */
	final class Impl extends ListBox implements IEditable<Collection<V>>, ChangeHandler {

		/**
		 * Constructor
		 * @param name
		 */
		public Impl(String name) {
			super(true);
			setName(name);
			addStyleName(Styles.TBOX);
			addChangeHandler(this);
		}

		@Override
		public void onChange(ChangeEvent event) {
			ValueChangeEvent.fire(this, getValue());
		}

		@Override
		public Collection<V> getValue() {
			final ArrayList<V> sel = new ArrayList<V>();
			for(int i = 0; i < super.getItemCount(); i++) {
				if(super.isItemSelected(i)) {
					sel.add(getDataValue(getValue(i)));
				}
			}
			return sel;
		}

		@Override
		public void setValue(Collection<V> value, boolean fireEvents) {
			setValue(value);
			if(fireEvents) {
				ValueChangeEvent.fire(this, getValue());
			}
		}

		@Override
		public void setValue(Collection<V> value) {
			setSelectedIndex(-1);
			if(value != null) {
				for(int i = 0; i < super.getItemCount(); i++) {
					for(final V val : value) {
						final String rv = renderer.convert(val);
						if(rv != null && rv.equals(getValue(i))) {
							setItemSelected(i, true);
						}
					}
				}
			}
		}

		@Override
		public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Collection<V>> handler) {
			return addHandler(handler, ValueChangeEvent.getType());
		}
	}

	/**
	 * The list box widget.
	 */
	private final Impl lb;

	/**
	 * Constructor
	 * @param name
	 * @param propName
	 * @param labelText
	 * @param helpText
	 * @param data
	 */
	MultiSelectField(String name, String propName, String labelText, String helpText, Map<V, String> data) {
		super(name, propName, labelText, helpText);
		lb = new Impl(name);
		lb.addValueChangeHandler(this);
		lb.addFocusHandler(this);
		lb.addBlurHandler(this);
		setData(data);
	}

	/**
	 * Sets the options.
	 * @param data The options to set
	 */
	@Override
	public void setData(Map<V, String> data) {
		super.setData(data);
		lb.clear();
		if(data != null) {
			for(final Map.Entry<V, String> e : data.entrySet()) {
				lb.addItem(e.getValue());
			}
		}
		lb.setSelectedIndex(-1);
	}

	@Override
	public void addDataItem(String name, V value) {
		super.addDataItem(name, value);
		lb.addItem(name);
	}

	@Override
	public void removeDataItem(V value) {
		super.removeDataItem(value);
		for(int i = 0; i < lb.getItemCount(); i++) {
			if(lb.getValue(i).equals(getToken(value))) {
				lb.removeItem(i);
				return;
			}
		}
	}

	@Override
	public IEditable<Collection<V>> getEditable() {
		return lb;
	}

	public int getItemCount() {
		return lb.getItemCount();
	}

	public boolean isItemSelected(int index) {
		return lb.isItemSelected(index);
	}

	public String getItemText(int index) {
		return lb.getItemText(index);
	}

	public void setItemText(int index, String text) {
		lb.setItemText(index, text);
	}

	public int getVisibleItemCount() {
		return lb.getVisibleItemCount();
	}

	public void setVisibleItemCount(final int visibleItems) {
		lb.setVisibleItemCount(visibleItems);
	}

	public int getSelectedIndex() {
		return lb.getSelectedIndex();
	}

	@Override
	public String doGetText() {
		// comma delimit
		final StringBuilder sb = new StringBuilder();
		for(int i = 0; i < lb.getItemCount(); i++) {
			if(lb.isItemSelected(i)) {
				sb.append(',');
				sb.append(lb.getValue(i));
			}
		}
		return sb.length() == 0 ? "" : sb.substring(1);
	}

	public void setText(String text) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setEnabled(boolean enabled) {
		lb.setEnabled(enabled);
		super.setEnabled(enabled);
	}
}

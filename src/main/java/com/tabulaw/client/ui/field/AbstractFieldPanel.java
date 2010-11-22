/**
 * Copyright (C) Tabulaw, Inc. 2009-2010 All Rights Reserved
 * @author jpk
 * @since Mar 13, 2010
 */
package com.tabulaw.client.ui.field;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.tabulaw.client.validate.IErrorHandler;
import com.tabulaw.client.validate.IHasErrorHandler;


/**
 * Base class for all concrete field panels.
 * @author jpk
 */
public abstract class AbstractFieldPanel extends Composite implements IHasFieldGroup, IHasErrorHandler {

	/**
	 * Styles (field.css)
	 * @author jpk
	 */
	public static final class Styles {

		/**
		 * Style indicating a field panel.
		 */
		public static final String FIELD_PANEL = "fpnl";
	}

	/**
	 * The actual panel.
	 */
	protected final FlowPanel panel = new FlowPanel();
	
	/**
	 * The field group.
	 */
	protected FieldGroup group;

	/**
	 * The sole error handler instance for this binding.
	 */
	protected IErrorHandler errorHandler;

	private boolean drawn;

	/**
	 * Constructor
	 */
	public AbstractFieldPanel() {
		super();
		panel.setStyleName(Styles.FIELD_PANEL);
		initWidget(panel);
	}

	@Override
	public final IErrorHandler getErrorHandler() {
		return errorHandler;
	}

	@Override
	public final void setErrorHandler(IErrorHandler errorHandler) {
		this.errorHandler = errorHandler;
		if(group != null) group.setErrorHandler(errorHandler);
	}

	/**
	 * Checks the current state of this field panel ensuring the field group is ok
	 * to be generated.
	 * @return true/false
	 */
	protected boolean canGenerateFieldGroup() {
		return true;
	}

	/**
	 * Generates the root {@link FieldGroup} this panel references via
	 * {@link #getFieldGroup()}. This method is only called when this panel's
	 * field group reference is <code>null</code>. Therefore, this method may be
	 * circumvented by manually calling {@link #setFieldGroup(FieldGroup)}.
	 * @return A new {@link FieldGroup} instance.
	 */
	protected abstract FieldGroup generateFieldGroup();

	/**
	 * Generates the fields in the field group if they haven't been created yet.
	 * This guarantees a non-<code> return value.
	 * @throws IllegalStateException When the field group can't be generated based
	 *         on the current state of this panel.
	 */
	@Override
	public final FieldGroup getFieldGroup() throws IllegalStateException {
		if(group == null) {
			if(!canGenerateFieldGroup()) {
				throw new IllegalStateException();
			}
			Log.debug(this + " generating fields..");
			setFieldGroup(generateFieldGroup());
		}
		return group;
	}

	@Override
	public void setFieldGroup(FieldGroup fields) {
		if(fields == null) throw new IllegalArgumentException("Null fields");
		if(this.group == fields) return;
		this.group = fields;
		this.group.setWidget(this);
		drawn = false; // force a re-draw

		// propagate the binding's error handler and model change tracker
		if(errorHandler != null) {
			Log.debug("Propagating error handler for: " + this);
			group.setErrorHandler(errorHandler);
		}
	}

	/**
	 * Provides the field panel renderer (drawer).
	 * @return the renderer
	 */
	protected abstract IFieldRenderer<FlowPanel> getRenderer();

	/**
	 * Responsible for rendering the group in the ui. The default is to employ the
	 * provided renderer via {@link #getRenderer()}. Sub-classes may extend this
	 * method to circumvent this strategy thus avoiding the call to
	 * {@link #getRenderer()}.
	 */
	protected void draw() {
		final IFieldRenderer<FlowPanel> renderer = getRenderer();
		if(renderer != null) {
			Log.debug(this + ": rendering..");
			renderer.render(panel, getFieldGroup());
		}
	}

	@Override
	protected void onAttach() {
		Log.debug("Attaching " + this + "..");
		super.onAttach();
	}

	@Override
	protected void onLoad() {
		Log.debug("Loading " + toString() + "..");
		super.onLoad();
		if(!drawn) {
			draw();
			drawn = true;
		}
	}

	@Override
	protected void onDetach() {
		Log.debug("Detatching " + toString() + "..");
		super.onDetach();
	}

	@Override
	public String toString() {
		return "FieldPanel[" + (group == null ? "-nofields-" : group.getName()) + "]";
	}
}

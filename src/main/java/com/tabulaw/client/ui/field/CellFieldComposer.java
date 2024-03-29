/**
 * Copyright (C) Tabulaw, Inc. 2009-2010 All Rights Reserved
 * @author jpk
 * May 24, 2008
 */
package com.tabulaw.client.ui.field;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Lays out fields utilizing horizontal and vertical panels with the following
 * attributes:
 * <ol>
 * <li>Field labels are placed on top of the field
 * <li>Fields are added horizontally to the canvas
 * <li>New rows are created by calling {@link #newRow()}
 * </ol>
 * @author jpk
 */
public class CellFieldComposer extends AbstractFieldComposer implements HasAlignment {

	/**
	 * Styles - (field.css)
	 * @author jpk
	 */
	static final class Styles {

		/**
		 * Style for wrapping divs containing a field and label.
		 */
		public static final String FIELD_CONTAINER = "fldc";

		/**
		 * Style applied to each row of fields.
		 */
		public static final String FIELD_ROW = "frow";
	}

	/**
	 * The root canvas panel for this field canvas implementation.
	 */
	private VerticalPanel vp;

	private HorizontalPanel currentRow;

	private Widget last;

	/**
	 * Constructor
	 */
	public CellFieldComposer() {
		super();
	}

	@Override
	public void setCanvas(Panel canvas) {
		if(this.canvas != null && this.canvas == canvas) return;
		super.setCanvas(canvas);
		vp = new VerticalPanel();
		currentRow = null;
		last = null;
		this.canvas = canvas;
		canvas.add(vp);
	}

	public HorizontalPanel getCurrentRow() {
		if(currentRow == null) {
			currentRow = new HorizontalPanel();
			currentRow.setStyleName(Styles.FIELD_ROW);
			vp.add(currentRow);
		}
		return currentRow;
	}

	@Override
	public void add(FieldLabel fldLbl, Widget w) {
		FlowPanel fcontainer = new FlowPanel();
		fcontainer.setStyleName(Styles.FIELD_CONTAINER);
		
		// add the field's name as a style to the field container
		// this is the proper way to achieve custom placement of fields
		if(w instanceof IFieldWidget<?>) {
			fcontainer.addStyleName(((IFieldWidget<?>) w).getName());
		}

		if(fldLbl != null) {
			fcontainer.add(fldLbl);
		}
		/*
		else if(!atCurrent) {
			fp.add(new HTML("&nbsp;")); // for spacing
		}
		*/

		fcontainer.add(w);
		getCurrentRow().add(fcontainer);
		last = w;
	}

	@Override
	public void addWidget(Widget w) {
		add(null, w);
	}

	@Override
	public void addWidget(String label, Widget w) {
		add(label == null ? null : new FieldLabel(label), w);
	}

	public void addField(IFieldWidget<?> field) {
		addField(field, true);
	}

	@Override
	public void addField(IFieldWidget<?> field, boolean showLabel) {
		add(showLabel ? field.getFieldLabel() : null, field.getWidget());
		field.setFieldContainer(last.getParent());
		field.setFieldLabelContainer(last.getParent());
	}

	/**
	 * Forces a new row to be created before the next field or Widget is added.
	 * Also, the flow and alignment state is reset.
	 */
	public void newRow() {
		// this will cause a new row the next time addField is called
		currentRow = null;
	}

	/**
	 * Adds the given style name to the associated field container widget that
	 * contains it.
	 * @param w
	 * @param style
	 */
	public void addFieldContainerStyle(Widget w, String style) {
		final Widget p = w.getParent();
		if(p.getStyleName() == null || p.getStyleName().indexOf(Styles.FIELD_CONTAINER) < 0) {
			throw new IllegalArgumentException("Not a field contained widget");
		}
		p.addStyleName(style);
	}

	public HorizontalAlignmentConstant getHorizontalAlignment() {
		return getCurrentRow().getHorizontalAlignment();
	}

	public void setHorizontalAlignment(HorizontalAlignmentConstant align) {
		getCurrentRow().setHorizontalAlignment(align);
	}

	public VerticalAlignmentConstant getVerticalAlignment() {
		return getCurrentRow().getVerticalAlignment();
	}

	public void setVerticalAlignment(VerticalAlignmentConstant align) {
		getCurrentRow().setVerticalAlignment(align);
	}
}

/**
 * The Logic Lab
 * @author jpk
 * May 24, 2008
 */
package com.tll.client.ui.field;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * FlowPanelFieldComposer - Lays out fields in a flow style having the following
 * attributes:
 * <ol>
 * <li>Field labels are placed on top of the field
 * <li>Fields are added horizontally to the canvas
 * <li>New rows are created by calling {@link #newRow()}
 * </ol>
 * @author jpk
 */
public class FlowPanelFieldComposer extends AbstractFieldComposer implements HasAlignment {

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

	private boolean atCurrent;

	/**
	 * Constructor
	 */
	public FlowPanelFieldComposer() {
		super();
	}

	@Override
	public void setCanvas(Panel canvas) {
		if(this.canvas != null && this.canvas == canvas) return;
		super.setCanvas(canvas);
		vp = new VerticalPanel();
		currentRow = null;
		last = null;
		atCurrent = false;
		this.canvas = canvas;
		canvas.add(vp);
	}

	private HorizontalPanel getCurrentRow() {
		if(currentRow == null) {
			currentRow = new HorizontalPanel();
			currentRow.setStyleName(Styles.FIELD_ROW);
			vp.add(currentRow);
		}
		return currentRow;
	}

	@Override
	public void add(FieldLabel fldLbl, Widget w) {
		FlowPanel fp;
		if(!atCurrent) {
			fp = new FlowPanel();
			fp.setStyleName(Styles.FIELD_CONTAINER);
		}
		else {
			if(last == null) throw new IllegalStateException("Empty row");
			fp = (FlowPanel) last.getParent();
		}

		if(fldLbl != null) {
			fp.add(fldLbl);
		}
		else if(!atCurrent) {
			fp.add(new HTML("&nbsp;")); // for spacing
		}

		fp.add(w);
		getCurrentRow().add(fp);
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

	@Override
	public void addField(IFieldWidget<?> field) {
		add(field.getFieldLabel(), field.getWidget());
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
		reset();
	}

	/**
	 * Forces subsequently added fields/widgets to be added at the same "slot" at
	 * the last added field/widget.
	 */
	public void stopFlow() {
		atCurrent = true;
	}

	/**
	 * Re-establishes the flow so subsequently added fields/widgets will have a
	 * newly created "slot".
	 */
	public void resetFlow() {
		atCurrent = false;
	}

	/**
	 * Resets the alignment for subsequently added Widgets/fields to their initial
	 * values.
	 */
	public void resetAlignment() {
		setHorizontalAlignment(ALIGN_DEFAULT);
		setVerticalAlignment(ALIGN_TOP);
	}

	/**
	 * Resets both the flow and alignment.
	 */
	public void reset() {
		resetFlow();
		resetAlignment();
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

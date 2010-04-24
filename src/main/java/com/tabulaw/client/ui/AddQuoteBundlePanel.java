/**
 * The Logic Lab
 * @author jpk
 * @since Feb 16, 2010
 */
package com.tabulaw.client.ui;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.tabulaw.client.ui.field.AbstractFieldGroupProvider;
import com.tabulaw.client.ui.field.AbstractFieldPanel;
import com.tabulaw.client.ui.field.FieldFactory;
import com.tabulaw.client.ui.field.FieldGroup;
import com.tabulaw.client.ui.field.FlowPanelFieldComposer;
import com.tabulaw.client.ui.field.IFieldRenderer;
import com.tabulaw.client.ui.field.IFieldWidget;
import com.tabulaw.schema.PropertyMetadata;
import com.tabulaw.schema.PropertyType;


/**
 * UI handling of adding a new quote bundle.
 * @author jpk
 */
public class AddQuoteBundlePanel extends AbstractFieldPanel<FlowPanel> {
	
	static class FieldProvider extends AbstractFieldGroupProvider {

		static final PropertyMetadata qbNameMetadata = new PropertyMetadata(PropertyType.STRING, false, true, 50);
		static final PropertyMetadata qbDescMetadata = new PropertyMetadata(PropertyType.STRING, false, false, 255);
		
		@Override
		protected String getFieldGroupName() {
			return "Quote Bundle";
		}

		@Override
		protected void populateFieldGroup(FieldGroup fg) {
			IFieldWidget<?> fw;
			
			fw = FieldFactory.ftext("qbName", "name", "Name", "The name to assign to the Quote Bundle", 25);
			fw.setPropertyMetadata(qbNameMetadata);
			fg.addField(fw);
			
			fw = FieldFactory.ftextarea("qbDesc", "description", "Description", "Optional description for the Quote Bundle", 7, 15);
			fw.setPropertyMetadata(qbDescMetadata);
			fg.addField(fw);
			
			fg.validateIncrementally(false);
		}

	}

	/**
	 * Renders the add quote bundle fields.
	 * @author jpk
	 */
	static class Renderer implements IFieldRenderer<FlowPanel> {

		public void render(FlowPanel panel, FieldGroup fg) {
			final FlowPanelFieldComposer cmpsr = new FlowPanelFieldComposer();
			cmpsr.setCanvas(panel);
			cmpsr.addField(fg.getFieldWidget("qbName"));
			cmpsr.newRow();
			cmpsr.addField(fg.getFieldWidget("qbDesc"));
		}
	}
	
	private final FlowPanel panel = new FlowPanel();
	
	public AddQuoteBundlePanel() {
		super();
		initWidget(panel);
	}

	@Override
	protected FieldGroup generateFieldGroup() {
		return new FieldProvider().getFieldGroup();
	}

	@Override
	protected IFieldRenderer<FlowPanel> getRenderer() {
		return new Renderer();
	}
	
	public Focusable getFocusable() {
		return getFieldGroup().getFieldWidget("qbName"); 
	}
}

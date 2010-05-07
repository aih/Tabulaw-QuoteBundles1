/**
 * The Logic Lab
 * @author jpk
 * @since May 2, 2010
 */
package com.tabulaw.client.app.ui;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.user.client.ui.FlowPanel;
import com.tabulaw.client.app.model.EntityMetadataProvider;
import com.tabulaw.client.ui.GridRenderer;
import com.tabulaw.client.ui.field.AbstractFieldGroupProvider;
import com.tabulaw.client.ui.field.AbstractFieldPanel;
import com.tabulaw.client.ui.field.CheckboxField;
import com.tabulaw.client.ui.field.DateField;
import com.tabulaw.client.ui.field.FieldFactory;
import com.tabulaw.client.ui.field.FieldGroup;
import com.tabulaw.client.ui.field.FlowPanelFieldComposer;
import com.tabulaw.client.ui.field.IFieldRenderer;
import com.tabulaw.client.ui.field.RadioGroupField;
import com.tabulaw.client.ui.field.TextField;
import com.tabulaw.common.model.EntityType;
import com.tabulaw.common.model.User.Role;
import com.tabulaw.schema.PropertyMetadata;
import com.tabulaw.util.StringUtil;

/**
 * @author jpk
 */
public class UserFieldPanel extends AbstractFieldPanel<FlowPanel> {

	static class FieldProvider extends AbstractFieldGroupProvider {

		@Override
		protected String getFieldGroupName() {
			return "User";
		}

		@Override
		protected void populateFieldGroup(FieldGroup fg) {
			Map<String, PropertyMetadata> metamap = EntityMetadataProvider.get().getEntityMetadata(EntityType.USER);

			TextField fname = FieldFactory.ftext("userName", "name", "Name", "Name", 40);
			fname.setPropertyMetadata(metamap.get("name"));
			fg.addField(fname);

			TextField femail = FieldFactory.femail("userEmail", "emailAddress", "Email Address", "Your email address", 40);
			femail.setPropertyMetadata(metamap.get("emailAddress"));
			femail.setReadOnly(true);
			fg.addField(femail);

			CheckboxField flocked = FieldFactory.fcheckbox("userLocked", "locked", "Locked?", "User locked?");
			flocked.setPropertyMetadata(metamap.get("locked"));
			fg.addField(flocked);

			CheckboxField fenabled = FieldFactory.fcheckbox("userEnabled", "enabled", "Enabled?", "User enabled?");
			fenabled.setPropertyMetadata(metamap.get("enabled"));
			fg.addField(fenabled);

			DateField fexpires = FieldFactory.fdate("userExpires", "expires", "Expiry Date", "Date user account expires");
			fexpires.setPropertyMetadata(metamap.get("expires"));
			fg.addField(fexpires);

			HashMap<Role, String> dataMap = new HashMap<Role, String>();
			for(Role role : Role.values()) {
				dataMap.put(role, StringUtil.enumStyleToPresentation(role.name()));
			}

			GridRenderer userRolesRenderer = new GridRenderer(Role.values().length, null);
			RadioGroupField<Role> fuserRoles =
					FieldFactory.fradiogroup("userRoles", "authorities", null, "The user roles", dataMap, userRolesRenderer);
			fg.addField(fuserRoles);
		}
	}

	private final FlowPanel panel = new FlowPanel();

	/**
	 * Constructor
	 */
	public UserFieldPanel() {
		super();
		initWidget(panel);
	}

	@Override
	protected FieldGroup generateFieldGroup() {
		return new FieldProvider().getFieldGroup();
	}

	@Override
	protected IFieldRenderer<FlowPanel> getRenderer() {
		return new IFieldRenderer<FlowPanel>() {

			@Override
			public void render(FlowPanel widget, FieldGroup fg) {
				final FlowPanelFieldComposer cmpsr = new FlowPanelFieldComposer();
				cmpsr.setCanvas(widget);
				cmpsr.addField(fg.getFieldWidget("userName"));
				cmpsr.newRow();
				cmpsr.addField(fg.getFieldWidget("userEmail"));
				cmpsr.newRow();
				cmpsr.addField(fg.getFieldWidget("userEnabled"));
				cmpsr.addField(fg.getFieldWidget("userLocked"));
				cmpsr.newRow();
				cmpsr.addField(fg.getFieldWidget("userExpires"));
				cmpsr.newRow();
				cmpsr.addField(fg.getFieldWidget("userRoles"), false);
			}
		};
	}

}

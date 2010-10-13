package com.tabulaw.client.app.ui;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.tabulaw.client.app.Poc;
import com.tabulaw.client.app.model.ClientModelCache;
import com.tabulaw.client.model.ModelChangeEvent;
import com.tabulaw.client.model.ModelChangeEvent.ModelChangeOp;
import com.tabulaw.client.ui.Dialog;
import com.tabulaw.client.ui.edit.EditEvent;
import com.tabulaw.client.ui.edit.FieldGroupEditPanel;
import com.tabulaw.client.ui.edit.IEditHandler;
import com.tabulaw.client.ui.edit.EditEvent.EditOp;
import com.tabulaw.client.ui.field.AbstractFieldGroupProvider;
import com.tabulaw.client.ui.field.AbstractFieldPanel;
import com.tabulaw.client.ui.field.CellFieldComposer;
import com.tabulaw.client.ui.field.FieldFactory;
import com.tabulaw.client.ui.field.FieldGroup;
import com.tabulaw.client.ui.field.IFieldRenderer;
import com.tabulaw.client.ui.field.IFieldWidget;
import com.tabulaw.common.data.rpc.ModelListPayload;
import com.tabulaw.model.DocRef;
import com.tabulaw.model.EntityBase;
import com.tabulaw.model.QuoteBundle;
import com.tabulaw.schema.PropertyMetadata;
import com.tabulaw.schema.PropertyType;

public class UserQuoteDialog extends Dialog implements IEditHandler<FieldGroup>{

	static class FieldProvider extends AbstractFieldGroupProvider {
		static final PropertyMetadata quoteTitleMetadata = new PropertyMetadata(PropertyType.STRING, false, true, 50);
		static final PropertyMetadata quoteTextMetadata = new PropertyMetadata(PropertyType.STRING, false, false, 255);
		static final PropertyMetadata quoteReferenceMetadata = new PropertyMetadata(PropertyType.STRING, false, false, 50);

		@Override
		protected String getFieldGroupName() {
			return "Quote Bundle";
		}

		@Override
		protected void populateFieldGroup(FieldGroup fg) {
			IFieldWidget<?> fw;

			fw = FieldFactory.ftext("quoteTitle", "title", "Title", "The name to assign to the Quote", 25);
			fw.setPropertyMetadata(quoteTitleMetadata);
			fg.addField(fw);

			fw =FieldFactory.ftextarea("quoteText", "quotetext", "Enter text below that you want to save", "Enter text below that you want to save",12, 80);
			fw.setPropertyMetadata(quoteTextMetadata );
			fg.addField(fw);

			fw = FieldFactory.ftext("quoteReference", "quotereference", "Reference", "The reference to source of the quote", 40);
			fw.setPropertyMetadata(quoteReferenceMetadata);
			fw.setEnabled(false);
			fg.addField(fw);

			fw = FieldFactory.ftext("quoteReferenceUrl", "quotereferenceurl", "Reference URL", "The reference url", 40);
			fw.setPropertyMetadata(quoteReferenceMetadata);
			fw.setEnabled(false);
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
			final CellFieldComposer cmpsr = new CellFieldComposer();
			cmpsr.setCanvas(panel);
			cmpsr.addField(fg.getFieldWidget("quoteTitle"));
			cmpsr.newRow();
			cmpsr.addField(fg.getFieldWidget("quoteText"));
			cmpsr.newRow();
			cmpsr.addField(fg.getFieldWidget("quoteReference"));
			cmpsr.newRow();
			cmpsr.addField(fg.getFieldWidget("quoteReferenceUrl"));
		}
	}
	
	

	private class UserQuotePanel extends AbstractFieldPanel {
		@Override
		protected FieldGroup generateFieldGroup() {
			return new FieldProvider().getFieldGroup();
		}

		@Override
		protected IFieldRenderer<FlowPanel> getRenderer() {
			return new Renderer();
		}

		public Focusable getFocusable() {
			return getFieldGroup().getFieldWidget("quoteText");
		}
		
	}

	private final UserQuotePanel fieldPanel = new UserQuotePanel();

	private final FieldGroupEditPanel editPanel = new FieldGroupEditPanel("Save", null, "Cancel", null, fieldPanel);

	/**
	 * Constructor
	 */
	public UserQuoteDialog() {

		super(null, false);
		setText("Add Quote");
		setAnimationEnabled(true);

		// set error hander for edit panel
		// editPanel.setErrorHandler(ErrorHandlerBuilder.build(false, true,
		// null), true);

		editPanel.addEditHandler(this);

		this.setWidth("330px");
		this.add(editPanel);
		
	}
	@Override
	public void onEdit(EditEvent<FieldGroup> event) {
		// persist the quote bundle
		if(event.getOp() == EditOp.SAVE) {
			FieldGroup fieldGroup = event.getContent();

			if(ClientModelCache.get().getUserState().getCurrentQuoteBundleId() == null) {
				return;
			} 
			String docTitle = (String) fieldGroup.getFieldWidget("quoteTitle").getValue();
			String quoteText=(String) fieldGroup.getFieldWidget("quoteText").getValue();
			String bundleId = ClientModelCache.get().getUserState().getCurrentQuoteBundleId();
			String userId = ClientModelCache.get().getUserState().getUserId();
			
			Poc.getUserDataService().addUserQuote(userId, docTitle, quoteText, bundleId,  new AsyncCallback<ModelListPayload<EntityBase>>(){

				@Override
				public void onFailure(Throwable arg0) {
					hide();
				}

				@Override
				public void onSuccess(ModelListPayload<EntityBase> arg0) {
					QuoteBundle quoteBundle = (QuoteBundle) arg0.getModelList().get(0);
					DocRef doc = (DocRef) arg0.getModelList().get(1);

					ClientModelCache.get().persist(doc, UserQuoteDialog.this);
					ClientModelCache.get().persist(quoteBundle, UserQuoteDialog.this);
					
					hide();
				}}
			);
			
			
		}
		else if(event.getOp() == EditOp.CANCEL) {
			hide();
		}
	}
	

}

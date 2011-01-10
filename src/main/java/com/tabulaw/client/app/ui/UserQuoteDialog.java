package com.tabulaw.client.app.ui;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.tabulaw.client.app.Poc;
import com.tabulaw.client.app.model.ClientModelCache;
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
import com.tabulaw.client.ui.field.SelectField;
import com.tabulaw.client.validate.ErrorHandlerBuilder;
import com.tabulaw.common.data.rpc.ModelListPayload;
import com.tabulaw.model.CaseReference;
import com.tabulaw.model.DocRef;
import com.tabulaw.model.EntityBase;
import com.tabulaw.model.QuoteBundle;
import com.tabulaw.model.Reference;
import com.tabulaw.model.RegulationReference;
import com.tabulaw.model.StatuteReference;
import com.tabulaw.schema.PropertyMetadata;
import com.tabulaw.schema.PropertyType;

public class UserQuoteDialog extends Dialog implements IEditHandler<FieldGroup>{
	static enum QuoteReferenceType {
		NONE, COURT, STATUTE, REGULATION
	}	
	
	static class FieldProvider extends AbstractFieldGroupProvider {
		static final PropertyMetadata quoteTitleMetadata = new PropertyMetadata(PropertyType.STRING, false, true, 50);
		static final PropertyMetadata quoteTextMetadata = new PropertyMetadata(PropertyType.STRING, false, true, 255);
		
		static final PropertyMetadata statuteTitle = new PropertyMetadata(PropertyType.STRING, false, true, 50);
		static final PropertyMetadata statuteReporter = new PropertyMetadata(PropertyType.STRING, false, true, 50);
		static final PropertyMetadata statuteSection = new PropertyMetadata(PropertyType.INT, false, true, 10);
		static final PropertyMetadata statuteSubSection = new PropertyMetadata(PropertyType.STRING, false, false, 10);
		static final PropertyMetadata statuteSubSubSection = new PropertyMetadata(PropertyType.STRING, false, false, 10);
		static final PropertyMetadata statuteYear = new PropertyMetadata(PropertyType.INT, false, false, 10);
		
		static final PropertyMetadata regulationTitle = new PropertyMetadata(PropertyType.STRING, false, true, 50);
		static final PropertyMetadata regulationReporter = new PropertyMetadata(PropertyType.STRING, false, true, 50);
		static final PropertyMetadata regulationSection = new PropertyMetadata(PropertyType.INT, false, true, 10);
		static final PropertyMetadata regulationSubSection = new PropertyMetadata(PropertyType.STRING, false, false, 10);
		static final PropertyMetadata regulationSubSubSection = new PropertyMetadata(PropertyType.STRING, false, false, 10);
		static final PropertyMetadata regulationYear = new PropertyMetadata(PropertyType.INT, false, false, 10);
		
		static final PropertyMetadata courtCourt = new PropertyMetadata(PropertyType.STRING, false, false, 50);
		static final PropertyMetadata courtDocLoc = new PropertyMetadata(PropertyType.STRING, false, false, 50);
		static final PropertyMetadata courtParties = new PropertyMetadata(PropertyType.STRING, false, false, 50);
		static final PropertyMetadata courtReftoken = new PropertyMetadata(PropertyType.STRING, false, false, 50);
		static final PropertyMetadata courtYear = new PropertyMetadata(PropertyType.INT, false, false, 10);
		
		
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
			
			Map<QuoteReferenceType, String> types = new LinkedHashMap<QuoteReferenceType, String>();
			types.put(QuoteReferenceType.NONE, "Without reference");
			types.put(QuoteReferenceType.COURT, "Court");
			types.put(QuoteReferenceType.STATUTE, "Statute");
			types.put(QuoteReferenceType.REGULATION, "Regulation");
			SelectField<QuoteReferenceType> select = FieldFactory.fselect("quoteReferenceType", "quotereferencetype", "Reference Type", "Reference Type", null);
			select.setData(types, false);
			select.setValue(QuoteReferenceType.NONE);
			fg.addField(select);
			
			fw = FieldFactory.ftext("statuteTitle", "statutetitle", "Statute Title", "Statute Title", 40);
			fw.setPropertyMetadata(statuteTitle);
			fg.addField(fw);

			fw = FieldFactory.ftext("statuteReporter", "statutereporter", "Statute Reporter", "Statute Reporter", 40);
			fw.setPropertyMetadata(statuteReporter);
			fg.addField(fw);
			
			fw = FieldFactory.ftext("statuteSection", "statutesection", "Statute Section", "Statute Section", 10);
			fw.setPropertyMetadata(statuteSection);
			fg.addField(fw);
			
			fw = FieldFactory.ftext("statuteSubSection", "statutesubsection", "Statute Subsection", "Statute Subsection", 10);
			fw.setPropertyMetadata(statuteSubSection);
			fg.addField(fw);
			
			fw = FieldFactory.ftext("statuteSubSubSection", "statutesubsubsection", "Statute Sub-subsection", "Statute Sub-subsection", 10);
			fw.setPropertyMetadata(statuteSubSubSection);
			fg.addField(fw);
			
			fw = FieldFactory.ftext("statuteYear", "statuteyear", "Statute Year", "Statute Year", 10);
			fw.setPropertyMetadata(statuteYear);
			fg.addField(fw);		
			
			fw = FieldFactory.ftext("regulationTitle", "regulationtitle", "Regulation Title", "Regulation Title", 40);
			fw.setPropertyMetadata(regulationTitle);
			fg.addField(fw);

			fw = FieldFactory.ftext("regulationReporter", "regulationreporter", "Regulation Reporter", "Regulation Reporter", 40);
			fw.setPropertyMetadata(regulationReporter);
			fg.addField(fw);
			
			fw = FieldFactory.ftext("regulationSection", "regulationsection", "Regulation Section", "Regulation Section", 10);
			fw.setPropertyMetadata(regulationSection);
			fg.addField(fw);
			
			fw = FieldFactory.ftext("regulationSubSection", "regulationsubsection", "Regulation Subsection", "Regulation Subsection", 10);
			fw.setPropertyMetadata(regulationSubSection);
			fg.addField(fw);
			
			fw = FieldFactory.ftext("regulationSubSubSection", "regulationsubsubsection", "Regulation Sub-subsection", "Regulation Sub-subsection", 10);
			fw.setPropertyMetadata(regulationSubSubSection);
			fg.addField(fw);
			
			fw = FieldFactory.ftext("regulationYear", "regulationyear", "Regulation Year", "Regulation Year", 10);
			fw.setPropertyMetadata(regulationYear);
			fg.addField(fw);	
			
			fw = FieldFactory.ftext("courtCourt", "courtcourt", "Court", "Court", 40);
			fw.setPropertyMetadata(courtCourt);
			fg.addField(fw);
			
			fw = FieldFactory.ftext("courtDocLoc", "courtdocloc", "Document Location", "Document Location", 40);
			fw.setPropertyMetadata(courtDocLoc);
			fg.addField(fw);
			
			fw = FieldFactory.ftext("courtParties", "courtparties", "Parties", "Parties", 40);
			fw.setPropertyMetadata(courtParties);
			fg.addField(fw);
			
			fw = FieldFactory.ftext("courtReftoken", "courtreftoken", "Reftoken", "Reftoken", 40);
			fw.setPropertyMetadata(courtReftoken);
			fg.addField(fw);			
			
			fw = FieldFactory.ftext("courtYear", "courtyear", "Year", "Year", 10);
			fw.setPropertyMetadata(courtYear);
			fg.addField(fw);	
			
			fg.validateIncrementally(false);
		}

	}

	/**
	 * Renders the add quote bundle fields.
	 * @author jpk
	 */
	static class Renderer implements IFieldRenderer<FlowPanel> {

		private List<HorizontalPanel> statuteRows = new ArrayList<HorizontalPanel>();
		private List<HorizontalPanel> courtRows = new ArrayList<HorizontalPanel>();
		private List<HorizontalPanel> regulationRows = new ArrayList<HorizontalPanel>();
		
		private UserQuoteDialog dialog;
		
		public Renderer(UserQuoteDialog dialog) {
			this.dialog = dialog;			
		}
		
		private void setVisible(List<HorizontalPanel> list, boolean visible) {
			for (HorizontalPanel panel : list) {
				panel.setVisible(visible);
			}
		}
		
		public void render(FlowPanel panel, FieldGroup fg) {
			final CellFieldComposer cmpsr = new CellFieldComposer();
			cmpsr.setCanvas(panel);
			cmpsr.addField(fg.getFieldWidget("quoteTitle"));
			cmpsr.newRow();
			cmpsr.addField(fg.getFieldWidget("quoteText"));
			cmpsr.newRow();
			
			SelectField<QuoteReferenceType> select = (SelectField<QuoteReferenceType>) fg.getFieldWidget("quoteReferenceType");
			select.addValueChangeHandler(new ValueChangeHandler<QuoteReferenceType>() {
				
				@Override
				public void onValueChange(ValueChangeEvent<QuoteReferenceType> event) {
					QuoteReferenceType type = event.getValue();
					switch (type) {
						case NONE:
							setVisible(statuteRows, false);
							setVisible(courtRows, false);
							setVisible(regulationRows, false);
							break;
						case STATUTE:
							setVisible(statuteRows, true);
							setVisible(courtRows, false);
							setVisible(regulationRows, false);
							break;
						case COURT:
							setVisible(statuteRows, false);
							setVisible(courtRows, true);
							setVisible(regulationRows, false);
							break;
						case REGULATION:
							setVisible(statuteRows, false);
							setVisible(courtRows, false);
							setVisible(regulationRows, true);
							break;
					}
					dialog.center();
				}
			});
			cmpsr.addField(select);
			
			cmpsr.newRow();
			cmpsr.addField(fg.getFieldWidget("statuteTitle"));
			statuteRows.add(cmpsr.getCurrentRow());
			cmpsr.newRow();
			cmpsr.addField(fg.getFieldWidget("statuteReporter"));
			statuteRows.add(cmpsr.getCurrentRow());
			cmpsr.newRow();
			cmpsr.addField(fg.getFieldWidget("statuteSection"));
			statuteRows.add(cmpsr.getCurrentRow());
			cmpsr.newRow();
			cmpsr.addField(fg.getFieldWidget("statuteSubSection"));
			statuteRows.add(cmpsr.getCurrentRow());
			cmpsr.newRow();
			cmpsr.addField(fg.getFieldWidget("statuteSubSubSection"));
			statuteRows.add(cmpsr.getCurrentRow());
			cmpsr.newRow();
			cmpsr.addField(fg.getFieldWidget("statuteYear"));
			statuteRows.add(cmpsr.getCurrentRow());
			
			cmpsr.newRow();
			cmpsr.addField(fg.getFieldWidget("regulationTitle"));
			regulationRows.add(cmpsr.getCurrentRow());
			cmpsr.newRow();
			cmpsr.addField(fg.getFieldWidget("regulationReporter"));
			regulationRows.add(cmpsr.getCurrentRow());
			cmpsr.newRow();
			cmpsr.addField(fg.getFieldWidget("regulationSection"));
			regulationRows.add(cmpsr.getCurrentRow());
			cmpsr.newRow();
			cmpsr.addField(fg.getFieldWidget("regulationSubSection"));
			regulationRows.add(cmpsr.getCurrentRow());
			cmpsr.newRow();
			cmpsr.addField(fg.getFieldWidget("regulationSubSubSection"));
			regulationRows.add(cmpsr.getCurrentRow());
			cmpsr.newRow();
			cmpsr.addField(fg.getFieldWidget("regulationYear"));
			regulationRows.add(cmpsr.getCurrentRow());
			
			cmpsr.newRow();
			cmpsr.addField(fg.getFieldWidget("courtCourt"));
			courtRows.add(cmpsr.getCurrentRow());
			cmpsr.newRow();
			cmpsr.addField(fg.getFieldWidget("courtDocLoc"));
			courtRows.add(cmpsr.getCurrentRow());
			cmpsr.newRow();
			cmpsr.addField(fg.getFieldWidget("courtParties"));
			courtRows.add(cmpsr.getCurrentRow());
			cmpsr.newRow();
			cmpsr.addField(fg.getFieldWidget("courtReftoken"));
			courtRows.add(cmpsr.getCurrentRow());
			cmpsr.newRow();
			cmpsr.addField(fg.getFieldWidget("courtYear"));
			courtRows.add(cmpsr.getCurrentRow());
			
			setVisible(statuteRows, false);
			setVisible(courtRows, false);
			setVisible(regulationRows, false);
			
			cmpsr.newRow();
			HTML requiredNote= new HTML ("<div class='requiredNote'><sup>*</sup> required field</div>", true);
			cmpsr.addWidget(requiredNote);
		}
	}
	
	class UserQuotePanel extends AbstractFieldPanel {		
		
		@Override
		protected FieldGroup generateFieldGroup() {
			return new FieldProvider().getFieldGroup();
		}

		@Override
		protected IFieldRenderer<FlowPanel> getRenderer() {
			return new Renderer(UserQuoteDialog.this);
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

//		 set error hander for edit panel
		 editPanel.setErrorHandler(ErrorHandlerBuilder.build(false, true, null), true);

		editPanel.addEditHandler(this);

		this.setWidth("330px");
		this.add(editPanel);
		
	}
	
	private Reference getReference(FieldGroup fieldGroup) {
		QuoteReferenceType type = ((SelectField<QuoteReferenceType>) fieldGroup.getFieldWidget("quoteReferenceType")).getValue();
		switch (type) {
			case COURT:
				CaseReference caseRef = new CaseReference();
				caseRef.setCourt((String) fieldGroup.getFieldWidget("courtCourt").getValue());
				caseRef.setDocLoc((String) fieldGroup.getFieldWidget("courtDocLoc").getValue());
				caseRef.setParties((String) fieldGroup.getFieldWidget("courtParties").getValue());
				caseRef.setReftoken((String) fieldGroup.getFieldWidget("courtReftoken").getValue());
				caseRef.setYear(Integer.parseInt((String) fieldGroup.getFieldWidget("courtYear").getValue()));
				return caseRef;
			case STATUTE:
				StatuteReference statuteRef = new StatuteReference();
				statuteRef.setTitle((String) fieldGroup.getFieldWidget("statuteTitle").getValue());
				statuteRef.setReporter((String) fieldGroup.getFieldWidget("statuteReporter").getValue());
				statuteRef.setSection(Integer.parseInt((String) fieldGroup.getFieldWidget("statuteSection").getValue()));
				statuteRef.setSubSection((String) fieldGroup.getFieldWidget("statuteSubSection").getValue());
				statuteRef.setSubSubSection((String) fieldGroup.getFieldWidget("statuteSubSubSection").getValue());
				statuteRef.setYear(Integer.parseInt((String) fieldGroup.getFieldWidget("statuteYear").getValue()));
				return statuteRef;				
			case REGULATION:
				RegulationReference regulationRef = new RegulationReference();
				regulationRef.setTitle((String) fieldGroup.getFieldWidget("regulationTitle").getValue());
				regulationRef.setReporter((String) fieldGroup.getFieldWidget("regulationReporter").getValue());
				regulationRef.setSection(Integer.parseInt((String) fieldGroup.getFieldWidget("regulationSection").getValue()));
				regulationRef.setSubSection((String) fieldGroup.getFieldWidget("regulationSubSection").getValue());
				regulationRef.setSubSubSection((String) fieldGroup.getFieldWidget("regulationSubSubSection").getValue());
				regulationRef.setYear(Integer.parseInt((String) fieldGroup.getFieldWidget("regulationYear").getValue()));
				return regulationRef;					
		}
		return null;
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
			Reference reference = getReference(fieldGroup);
			String bundleId = ClientModelCache.get().getUserState().getCurrentQuoteBundleId();
			String userId = ClientModelCache.get().getUserState().getUserId();
			
			Poc.getUserDataService().addOrphanQuote(userId, docTitle, reference, quoteText, bundleId,  new AsyncCallback<ModelListPayload<EntityBase>>(){

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

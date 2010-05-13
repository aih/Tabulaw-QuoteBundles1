/**
 * The Logic Lab
 * @author jpk
 * @since May 2, 2010
 */
package com.tabulaw.client.app.ui;

import java.util.LinkedHashMap;
import java.util.Map;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.tabulaw.client.app.field.DocFieldProvider;
import com.tabulaw.client.app.field.DocFieldProvider.DocUseCase;
import com.tabulaw.client.ui.GridRenderer;
import com.tabulaw.client.ui.edit.FieldGroupEditPanel;
import com.tabulaw.client.ui.field.AbstractFieldPanel;
import com.tabulaw.client.ui.field.FieldFactory;
import com.tabulaw.client.ui.field.FieldGroup;
import com.tabulaw.client.ui.field.CellFieldComposer;
import com.tabulaw.client.ui.field.IFieldRenderer;
import com.tabulaw.client.ui.field.RadioGroupField;
import com.tabulaw.client.validate.ErrorHandlerBuilder;

/**
 * Captures user input for either creating a case doc or non-case doc.
 * <p>
 * A case doc really only requires a remote url to be specified whereas a
 * non-case doc requires the doc fields to be manually entered.
 * @author jpk
 */
public class DocCreateEditPanel extends FieldGroupEditPanel {

	static class FieldPanel extends AbstractFieldPanel {

		@Override
		protected FieldGroup generateFieldGroup() {
			FieldGroup fg = new FieldGroup("Create Document");

			FieldGroup fgCaseDoc = new DocFieldProvider(DocUseCase.CREATE_CASEDOC).getFieldGroup();
			fg.addField(fgCaseDoc);

			FieldGroup fgNonCaseDoc = new DocFieldProvider(DocUseCase.CREATE_NONCASE).getFieldGroup();
			fg.addField(fgNonCaseDoc);

			// add doc type selection radio button
			Map<String, String> dataMap = new LinkedHashMap<String, String>();
			dataMap.put("case", "Google Scholar Case");
			dataMap.put("noncase", "Editable");
			GridRenderer docTypeRenderer = new GridRenderer(2, null);
			RadioGroupField<String> fDocType =
					FieldFactory.fradiogroup("docType", null, "Document Type", "Select the document to create", dataMap,
							docTypeRenderer);
			fg.addField(fDocType);

			// enable/disable sub-fieldgroup based on doc type radio button selection
			fDocType.addValueChangeHandler(new ValueChangeHandler<String>() {

				@Override
				public void onValueChange(ValueChangeEvent<String> event) {
					String fval = event.getValue();
					boolean isCaseDoc = "case".equals(fval);

					FieldGroup fgCaseDoc1 = (FieldGroup) getFieldGroup().getFieldByName(DocUseCase.CREATE_CASEDOC.name());
					fgCaseDoc1.setEnabled(isCaseDoc);
					fgCaseDoc1.setRequired(isCaseDoc);
					
					FieldGroup fgNonCaseDoc1 = (FieldGroup) getFieldGroup().getFieldByName(DocUseCase.CREATE_NONCASE.name());
					fgNonCaseDoc1.setEnabled(!isCaseDoc);
					fgNonCaseDoc1.setRequired(!isCaseDoc);
				}
			});

			return fg;
		}

		@Override
		protected IFieldRenderer<FlowPanel> getRenderer() {
			return new IFieldRenderer<FlowPanel>() {

				@Override
				public void render(FlowPanel widget, FieldGroup fg) {
					final CellFieldComposer cmpsr = new CellFieldComposer();
					cmpsr.setCanvas(widget);

					cmpsr.addField(fg.getFieldWidget("docType"));
					cmpsr.newRow();

					HTML htmlNonCaseDoc = new HTML("Create a new editable document");
					htmlNonCaseDoc.setStyleName("headerNonCaseDoc");
					cmpsr.addWidget(htmlNonCaseDoc);
					cmpsr.addField(fg.getFieldWidget("docTitle"));
					cmpsr.addField(fg.getFieldWidget("docDate"));
					cmpsr.newRow();

					HTML htmlCaseDoc = new HTML("Specify the url for a Google Scholar Case Document");
					htmlCaseDoc.setStyleName("headerCaseDoc");
					cmpsr.addWidget(htmlCaseDoc);
					cmpsr.addField(fg.getFieldWidget("caseUrl"));
					cmpsr.newRow();
				}
			};
		}
	}

	/**
	 * Constructor
	 */
	public DocCreateEditPanel() {
		super("Create Document", null, null, null, new FieldPanel());
		showDeleteButton(false);
		showResetButton(false);
		showCancelButton(true);
		// set field-only error handler (no msg display)
		setErrorHandler(ErrorHandlerBuilder.build(false, true, null), false);
	}
}

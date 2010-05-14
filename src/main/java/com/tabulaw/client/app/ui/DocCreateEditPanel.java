/**
 * The Logic Lab
 * @author jpk
 * @since May 2, 2010
 */
package com.tabulaw.client.app.ui;

import java.util.Date;

import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.ui.FlowPanel;
import com.tabulaw.client.app.field.DocFieldProvider;
import com.tabulaw.client.app.field.DocFieldProvider.DocUseCase;
import com.tabulaw.client.ui.edit.FieldGroupEditPanel;
import com.tabulaw.client.ui.field.AbstractFieldPanel;
import com.tabulaw.client.ui.field.CellFieldComposer;
import com.tabulaw.client.ui.field.DateField;
import com.tabulaw.client.ui.field.FieldFactory;
import com.tabulaw.client.ui.field.FieldGroup;
import com.tabulaw.client.ui.field.IFieldRenderer;
import com.tabulaw.client.ui.field.RadioField;
import com.tabulaw.client.validate.ErrorHandlerBuilder;

/**
 * Captures user input for either creating a case doc or non-case doc.
 * <p>
 * A case doc really only requires a remote url to be specified whereas a
 * non-case doc requires the doc fields to be manually entered.
 * @author jpk
 */
public class DocCreateEditPanel extends FieldGroupEditPanel {

	class FieldPanel extends AbstractFieldPanel {

		@Override
		protected FieldGroup generateFieldGroup() {
			FieldGroup fg = new FieldGroup("Create Document");

			FieldGroup fgCaseDoc = new DocFieldProvider(DocUseCase.CREATE_CASEDOC).getFieldGroup();
			fg.addField(fgCaseDoc);

			FieldGroup fgNonCaseDoc = new DocFieldProvider(DocUseCase.CREATE_NONCASE).getFieldGroup();
			fg.addField(fgNonCaseDoc);

			// add doc type selection radio button
			RadioField fDocTypeCase =
					FieldFactory.fradio("docTypeCase", "docType", null, "Google Scholar document",
							"Select for fetch a remote Google Scholar document");
			fDocTypeCase.addStyleName("headerCaseDoc");
			fg.addField(fDocTypeCase);

			RadioField fDocTypeNonCase =
					FieldFactory.fradio("docTypeNonCase", "docType", null, "Editable document",
							"Select to create an empty non-case document.");
			fDocTypeNonCase.addStyleName("headerNonCaseDoc");
			fg.addField(fDocTypeNonCase);

			// enable/disable sub-fieldgroup based on doc type radio button selection
			fDocTypeCase.getEditable().addFocusHandler(new FocusHandler() {

				@Override
				public void onFocus(FocusEvent event) {
					setMode(true);
				}
			});
			fDocTypeNonCase.getEditable().addFocusHandler(new FocusHandler() {

				@Override
				public void onFocus(FocusEvent event) {
					setMode(false);
				}
			});

			// by default
			// fDocTypeCase.setValue(true, true);

			return fg;
		}

		@Override
		protected IFieldRenderer<FlowPanel> getRenderer() {
			return new IFieldRenderer<FlowPanel>() {

				@Override
				public void render(FlowPanel widget, FieldGroup fg) {
					final CellFieldComposer cmpsr = new CellFieldComposer();
					cmpsr.setCanvas(widget);

					cmpsr.addField(fg.getFieldWidget("docTypeNonCase"), false);
					cmpsr.newRow();

					cmpsr.addField(fg.getFieldWidget("docTitle"));
					cmpsr.addField(fg.getFieldWidget("docDate"));
					cmpsr.newRow();

					cmpsr.addField(fg.getFieldWidget("docTypeCase"), false);
					cmpsr.newRow();

					cmpsr.addField(fg.getFieldWidget("caseUrl"));
				}
			};
		}
	}

	/**
	 * Constructor
	 */
	public DocCreateEditPanel() {
		super("Create Document", null, null, null);
		setFieldPanel(new FieldPanel());
		showDeleteButton(false);
		showResetButton(false);
		showCancelButton(true);
		// set field-only error handler (no msg display)
		setErrorHandler(ErrorHandlerBuilder.build(false, true, null), false);
		// default check radio button
		((RadioField) getFieldPanel().getFieldGroup().getFieldWidget("docTypeNonCase")).setChecked(true);
		setMode(false);
	}

	public void setMode(final boolean isCaseDoc) {
		final FieldGroup fg = getFieldPanel().getFieldGroup();

		FieldGroup fgNonCaseDoc1 = (FieldGroup) fg.getFieldByName(DocUseCase.CREATE_NONCASE.name());
		// fgNonCaseDoc1.setEnabled(!isCaseDoc);
		fgNonCaseDoc1.setVisible(!isCaseDoc);
		fgNonCaseDoc1.setRequired(!isCaseDoc);

		FieldGroup fgCaseDoc1 = (FieldGroup) fg.getFieldByName(DocUseCase.CREATE_CASEDOC.name());
		// fgCaseDoc1.setEnabled(isCaseDoc);
		fgCaseDoc1.setVisible(isCaseDoc);
		fgCaseDoc1.setRequired(isCaseDoc);

		// default set doc date to now
		if(!isCaseDoc) {
			DateField fdocdate = (DateField) fg.getFieldWidget("docDate");
			if(fdocdate.getValue() == null) fdocdate.setValue(new Date());
		}

		getErrorHandler().clear();

		// set focus
		DeferredCommand.addCommand(new Command() {

			@Override
			public void execute() {
				fg.getFieldWidget(isCaseDoc ? "caseUrl" : "docTitle").setFocus(true);
			}
		});
	}
}

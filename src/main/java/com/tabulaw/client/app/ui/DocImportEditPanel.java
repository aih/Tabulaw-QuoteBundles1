package com.tabulaw.client.app.ui;

import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.tabulaw.client.ui.edit.FieldGroupEditPanel;
import com.tabulaw.client.ui.field.AbstractFieldPanel;
import com.tabulaw.client.ui.field.FieldFactory;
import com.tabulaw.client.ui.field.FieldGroup;
import com.tabulaw.client.ui.field.IFieldRenderer;
import com.tabulaw.client.ui.field.RadioField;
import com.tabulaw.client.validate.ErrorHandlerBuilder;
import com.tabulaw.common.data.GoogleDocument;

public class DocImportEditPanel extends FieldGroupEditPanel {

	public static class Style {
		public final static String DOC_IMPORTED_EDIT_PANEL = "doc-import-edit-panel";
	}

	class InProgressPanel extends AbstractFieldPanel {

		@Override
		protected FieldGroup generateFieldGroup() {
			return new FieldGroup("Import Document");
		}

		@Override
		protected IFieldRenderer<FlowPanel> getRenderer() {
			return new IFieldRenderer<FlowPanel>() {
				@Override
				public void render(FlowPanel widget, FieldGroup fg) {
					widget.add(new Label("Loading ..."));
				}
			};
		}

	}

	class FieldPanel extends AbstractFieldPanel {

		private final List<GoogleDocument> list;

		public FieldPanel(List<GoogleDocument> list) {
			this.list = list;
		}

		@Override
		protected FieldGroup generateFieldGroup() {
			FieldGroup fg = new FieldGroup("Import Document");
			for (final GoogleDocument doc : list) {
				RadioField gd = FieldFactory.fradio(doc.getResourceId(),
						"googledoc", null, doc.getTitle(),
						"Select for fetch a remote Google Scholar document");
				gd.addStyleName("headerCaseDoc");
				gd.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
					@Override
					public void onValueChange(ValueChangeEvent<Boolean> event) {
						setResourceId(doc.getResourceId());
					}
				});
			}
			return fg;
		}

		@Override
		protected IFieldRenderer<FlowPanel> getRenderer() {
			return new IFieldRenderer<FlowPanel>() {
				@Override
				public void render(FlowPanel widget, FieldGroup fg) {
					FlexTable table = new FlexTable();
					widget.add(table);
					table.setHTML(0, 1, "<b>Document</b>");
					table.setHTML(0, 2, "<b>Date</b>");
					table.setHTML(0, 3, "<b>Author</b>");
					for (int row = 0; row < list.size(); row++) {
						final GoogleDocument doc = list.get(row);
						final RadioButton rb = new RadioButton("googledocs");
						rb.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
							@Override
							public void onValueChange(
									ValueChangeEvent<Boolean> event) {
								if (event.getValue()) {
									setResourceId(doc.getResourceId());
								}
							}
						});
						ClickHandler ch = new ClickHandler() {
							@Override
							public void onClick(ClickEvent event) {
								rb.setValue(true, true);
							}
						};
						Label title = new Label(doc.getTitle());
						Label date = new Label(doc.getDate());
						Label author = new Label(doc.getAuthor());
						title.addClickHandler(ch);
						date.addClickHandler(ch);
						author.addClickHandler(ch);
						table.setWidget(row + 1, 0, rb);
						table.setWidget(row + 1, 1, title);
						table.setWidget(row + 1, 2, date);
						table.setWidget(row + 1, 3, author);
					}
				}
			};
		}
	}

	private String resourceId;

	public DocImportEditPanel() {
		super("Import from Google Docs", null, null, null);
		addStyleName(Style.DOC_IMPORTED_EDIT_PANEL);
		setFieldPanel(new InProgressPanel());
		showDeleteButton(false);
		showResetButton(false);
		showCancelButton(true);
		setErrorHandler(ErrorHandlerBuilder.build(false, true, null), false);
	}

	public void setGoogleDocs(List<GoogleDocument> list) {
		setResourceId(null);
		if (list != null) {
			setFieldPanel(new FieldPanel(list));
		} else {
			setFieldPanel(new InProgressPanel());
		}
	}

	public String getResourceId() {
		return resourceId;
	}

	private void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}
}

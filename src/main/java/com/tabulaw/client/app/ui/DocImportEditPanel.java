package com.tabulaw.client.app.ui;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.tabulaw.client.ui.edit.FieldGroupEditPanel;
import com.tabulaw.client.ui.field.AbstractFieldPanel;
import com.tabulaw.client.ui.field.FieldGroup;
import com.tabulaw.client.ui.field.IFieldRenderer;
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
			return new FieldGroup("Import Document");
		}

		@Override
		protected IFieldRenderer<FlowPanel> getRenderer() {
			return new IFieldRenderer<FlowPanel>() {
				@Override
				public void render(FlowPanel widget, FieldGroup fg) {
					FlexTable table = new FlexTable();
					widget.add(table);
					table.setHTML(0, 0, "<b>Document</b>");
					table.setHTML(0, 1, "<b>Date</b>");
					table.setHTML(0, 2, "<b>Author</b>");
					for (int row = 0; row < list.size(); row++) {
						final GoogleDocument doc = list.get(row);
						final CheckBox cb = new CheckBox(doc.getTitle());
						cb.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
							@Override
							public void onValueChange(
									ValueChangeEvent<Boolean> event) {
								setResourceId(doc.getResourceId(),
										event.getValue());
							}
						});
						ClickHandler ch = new ClickHandler() {
							@Override
							public void onClick(ClickEvent event) {
								cb.setValue(!cb.getValue(), true);
							}
						};
						Label date = new Label(doc.getDate());
						Label author = new Label(doc.getAuthor());
						date.addClickHandler(ch);
						author.addClickHandler(ch);
						table.setWidget(row + 1, 0, cb);
						table.setWidget(row + 1, 1, date);
						table.setWidget(row + 1, 2, author);
					}
				}
			};
		}
	}

	private Set<String> value = new HashSet<String>();

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
		value = new HashSet<String>();
		if (list != null) {
			setFieldPanel(new FieldPanel(list));
		} else {
			setFieldPanel(new InProgressPanel());
		}
	}

	public Set<String> getValue() {
		return value;
	}

	private void setResourceId(String resourceId, boolean enabled) {
		if (enabled) {
			value.add(resourceId);
		} else {
			value.remove(resourceId);
		}
	}
}

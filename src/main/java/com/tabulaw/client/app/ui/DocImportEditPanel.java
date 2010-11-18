package com.tabulaw.client.app.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.tabulaw.client.ui.edit.IndicatorFieldGroupEditPanel;
import com.tabulaw.client.ui.field.AbstractFieldPanel;
import com.tabulaw.client.ui.field.FieldGroup;
import com.tabulaw.client.ui.field.IFieldRenderer;
import com.tabulaw.client.validate.ErrorHandlerBuilder;
import com.tabulaw.common.data.GoogleDocument;

public class DocImportEditPanel extends IndicatorFieldGroupEditPanel {

	public static class Style {
		public final static String DOC_IMPORTED_EDIT_PANEL = "doc-import-edit-panel";
	}

	private class InProgressPanel extends AbstractFieldPanel {

		@Override
		protected FieldGroup generateFieldGroup() {
			return new FieldGroup("Import Document");
		}

		@Override
		protected IFieldRenderer<FlowPanel> getRenderer() {
			return new IFieldRenderer<FlowPanel>() {
				@Override
				public void render(FlowPanel widget, FieldGroup fg) {
					widget.add(new HTML(
							"Loading ... <img src=\"images/ajax-loader.gif\">"));
				}
			};
		}
	}

	private class DocumentRow {
		public DocumentRow(String resourceId, CheckBox cb, Label date,
				Label author, Label status) {
			this.resourceId = resourceId;
			this.cb = cb;
			this.date = date;
			this.author = author;
			this.status = status;
		}

		public boolean failure = false;
		public boolean downloaded = false;
		public String resourceId;
		public CheckBox cb;
		public Label date;
		public Label author;
		public Label status;

		public boolean isEnabled() {
			return !failure && !downloaded;
		}
	}

	private final Map<String, DocumentRow> rows = new HashMap<String, DocumentRow>();

	private class Renderer implements IFieldRenderer<FlowPanel> {

		private final List<GoogleDocument> list;
		private final List<CheckBox> cbs = new ArrayList<CheckBox>();

		public Renderer(List<GoogleDocument> list) {
			this.list = list;
		}

		@Override
		public void render(FlowPanel widget, FieldGroup fg) {
			FlexTable table = new FlexTable();
			widget.add(table);
			rows.clear();
			table.setHTML(0, 0, "<b>Document</b>");
			table.setHTML(0, 1, "<b>Date</b>");
			table.setHTML(0, 2, "<b>Author</b>");
			table.setHTML(0, 3, "<b>Status</b>");
			for (int row = 0; row < list.size(); row++) {
				final GoogleDocument doc = list.get(row);
				final CheckBox cb = new CheckBox(doc.getTitle());
				cbs.add(cb);
				cb.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
					@Override
					public void onValueChange(ValueChangeEvent<Boolean> event) {
						setResourceId(doc, event.getValue());
					}
				});
				Label date = new Label(doc.getDate());
				Label author = new Label(doc.getAuthor());
				Label status = new Label("available");
				final DocumentRow dr = new DocumentRow(doc.getResourceId(), cb,
						date, author, status);
				ClickHandler ch = new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						if (dr.isEnabled()) {
							cb.setValue(!cb.getValue(), true);
						}
					}
				};
				date.addClickHandler(ch);
				author.addClickHandler(ch);
				table.setWidget(row + 1, 0, cb);
				table.setWidget(row + 1, 1, date);
				table.setWidget(row + 1, 2, author);
				table.setWidget(row + 1, 3, status);
				rows.put(doc.getResourceId(), dr);
			}
		}

		public void setEnabled(boolean enabled) {
			setIndicatorVisible(!enabled);
			for (CheckBox cb : cbs) {
				cb.setEnabled(enabled);
			}
		}
	}

	private class FieldPanel extends AbstractFieldPanel {

		private final Renderer renderer;

		public FieldPanel(List<GoogleDocument> list) {
			this.renderer = new Renderer(list);
		}

		@Override
		protected FieldGroup generateFieldGroup() {
			return new FieldGroup("Import Document");
		}

		@Override
		protected IFieldRenderer<FlowPanel> getRenderer() {
			return renderer;
		}

		public void setEnabled(boolean enabled) {
			renderer.setEnabled(enabled);
		}
	}

	private Set<GoogleDocument> value = new HashSet<GoogleDocument>();

	private boolean enabled = true;

	public DocImportEditPanel() {
		super("Import from Google Docs");
		addStyleName(Style.DOC_IMPORTED_EDIT_PANEL);
		setFieldPanel(new InProgressPanel());
		showDeleteButton(false);
		showResetButton(false);
		showCancelButton(true);
		setErrorHandler(ErrorHandlerBuilder.build(false, true, null), false);
	}

	public void setGoogleDocs(List<GoogleDocument> list) {
		value = new HashSet<GoogleDocument>();
		if (list != null) {
			setFieldPanel(new FieldPanel(list));
		} else {
			setFieldPanel(new InProgressPanel());
		}
	}

	public void addGoogleDocsFailure(Collection<String> ids) {
		for (String id : ids) {
			rows.get(id).failure = true;
		}
		updateRows();
	}

	public void addGoogleDocsDownloaded(Collection<String> ids) {
		for (String id : ids) {
			rows.get(id).downloaded = true;
		}
		updateRows();
	}

	public Set<GoogleDocument> getValue() {
		return value;
	}

	public void resetValue() {
		value = new HashSet<GoogleDocument>();
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		this.enabled = enabled;
		AbstractFieldPanel panel = getFieldPanel();
		if (panel instanceof FieldPanel) {
			((FieldPanel) panel).setEnabled(enabled);
		}
		updateRows();
	}

	private void updateRows() {
		for (DocumentRow row : rows.values()) {
			row.cb.setEnabled(row.isEnabled() && enabled);
			if (row.failure) {
				row.cb.setValue(false);
				row.status.setText("failure");
			} else if (row.downloaded) {
				row.cb.setValue(true);
				row.status.setText("downloaded");
			}
		}
	}

	private void setResourceId(GoogleDocument document, boolean checked) {
		if (checked) {
			value.add(document);
		} else {
			value.remove(document);
		}
	}
}

package com.tabulaw.client.app.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.tabulaw.client.app.Poc;
import com.tabulaw.client.app.model.ClientModelCache;
import com.tabulaw.client.ui.Dialog;
import com.tabulaw.client.ui.Notifier;
import com.tabulaw.client.ui.edit.EditEvent;
import com.tabulaw.client.ui.edit.EditEvent.EditOp;
import com.tabulaw.client.ui.edit.IEditHandler;
import com.tabulaw.client.ui.field.FieldGroup;
import com.tabulaw.client.util.PopupWindow;
import com.tabulaw.client.util.PopupWindowCloseHandler;
import com.tabulaw.common.data.GoogleDocument;
import com.tabulaw.common.msg.Msg;
import com.tabulaw.common.msg.Msg.MsgLevel;
import com.tabulaw.model.DocRef;

public class DocImportDialog extends Dialog implements IEditHandler<FieldGroup> {

	private final DocImportEditPanel importPanel = new DocImportEditPanel();

	private String authKey;
	private int importId = 0;

	private PopupWindow popup;
	private boolean hasAccessToken = false;

	public DocImportDialog() {
		super(null, false);
		setText("Import from Google Docs");
		setAnimationEnabled(true);
		importPanel.addEditHandler(this);
		Button cancel = new Button("Cancel");
		cancel.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide();
			}
		});
		setWidget(cancel);
	}

	public void setGoogleDocs(List<GoogleDocument> list) {
		importPanel.setGoogleDocs(list);
	}

	@Override
	public void onEdit(EditEvent<FieldGroup> event) {
		if (event.getOp() == EditOp.SAVE) {
			Set<GoogleDocument> docs = importPanel.getValue();
			if (!docs.isEmpty()
					&& Window
							.confirm("Importing will take some time. Do you want to continue?")) {
				importPanel.setEnabled(false);
				doImport(authKey, docs);
			} else {
				hide();
			}
		} else if (event.getOp() == EditOp.CANCEL) {
			hide();
		}
	}

	@Override
	public void show() {
		super.show();
		importPanel.setEnabled(true);
		setGoogleDocs(null);
		if (!hasAccessToken) {
			PopupWindow.setCloseHandler(new PopupWindowCloseHandler() {
				@Override
				public void onClose() {
					hasAccessToken = true;
					showContent();
				}
			});
			popup = PopupWindow.open("/poc/oauthauthorize", "mywindow", null);
		} else {
			showContent();
		}

	}

	@Override
	public void hide() {
		super.hide();
		PopupWindow.setCloseHandler(new PopupWindowCloseHandler() {
			@Override
			public void onClose() {
			}
		});
		if (popup != null) {
			popup.close();
		}
		popup = null;
	}

	private void showContent() {
		setWidget(importPanel);
		loadDocuments();
	}

	private void loadDocuments() {
		Poc.getGoogledocsService().getDocuments(
				new AsyncCallback<List<GoogleDocument>>() {
					@Override
					public void onSuccess(List<GoogleDocument> result) {
						showDocuments(result);
					}

					@Override
					public void onFailure(Throwable caught) {
						caught.printStackTrace();
					}
				});
	}

	private void showDocuments(List<GoogleDocument> documents) {
		setGoogleDocs(documents);
	}

	private void doImport(String authKey, Collection<GoogleDocument> resourceId) {
		final int currentImportId = ++importId;
		Poc.getGoogledocsService().download(resourceId,
				new AsyncCallback<List<DocRef>>() {
					@Override
					public void onFailure(Throwable caught) {
						caught.printStackTrace();
					}

					@Override
					public void onSuccess(List<DocRef> downloaded) {
						if (currentImportId == importId) {
							refreshList(downloaded);
							hide();
						}
					}
				});
	}

	private void refreshList(List<DocRef> downloaded) {
		final ArrayList<Msg> msgs = new ArrayList<Msg>(downloaded.size());
		for (DocRef doc : downloaded) {
			msgs.add(new Msg("Document: '" + doc.getTitle() + "' uploaded.",
					MsgLevel.INFO));
		}
		// persist and propagate
		ClientModelCache.get().persistAll(downloaded, this);
		DeferredCommand.addCommand(new Command() {
			@Override
			public void execute() {
				Notifier.get().post(msgs);
			}
		});
	}
}

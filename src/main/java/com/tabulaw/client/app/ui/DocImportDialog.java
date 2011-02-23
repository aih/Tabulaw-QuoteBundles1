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
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.tabulaw.client.app.Poc;
import com.tabulaw.client.app.model.ClientModelCache;
import com.tabulaw.client.ui.Dialog;
import com.tabulaw.client.ui.Notifier;
import com.tabulaw.client.ui.RpcCommand;
import com.tabulaw.client.ui.edit.EditEvent;
import com.tabulaw.client.ui.edit.EditEvent.EditOp;
import com.tabulaw.client.ui.edit.IEditHandler;
import com.tabulaw.client.ui.field.FieldGroup;
import com.tabulaw.client.util.PopupWindow;
import com.tabulaw.client.util.PopupWindowCloseHandler;
import com.tabulaw.common.data.GoogleDocument;
import com.tabulaw.common.data.rpc.DocRefListPayload;
import com.tabulaw.common.data.rpc.GoogleDocumentListPayload;
import com.tabulaw.common.msg.Msg;
import com.tabulaw.common.msg.Msg.MsgLevel;
import com.tabulaw.model.DocRef;

public class DocImportDialog extends Dialog implements IEditHandler<FieldGroup> {

	public static class Style {
		public final static String DOC_IMPORT_DIALOG="doc-import-dialog";
	}
	
	private final DocImportEditPanel importPanel = new DocImportEditPanel();

	private int importId = 0;

	private PopupWindow popup;
	private boolean hasAccessToken = false;

	public DocImportDialog() {
		super(null, false);
		addStyleName(Style.DOC_IMPORT_DIALOG);
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
				doImport(docs);
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
		showContent();
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
		setGoogleDocs(null);
		if (!hasAccessToken) {
			PopupWindow.setCloseHandler(new PopupWindowCloseHandler() {
				@Override
				public void onClose() {
					hasAccessToken = true;
					loadDocuments();
				}
			});
			popup = PopupWindow.open("oauthauthorize", "mywindow", null);
		} else {
			loadDocuments();
		}
	}

	private void loadDocuments() {
		setWidget(importPanel);
		new RpcCommand<GoogleDocumentListPayload>() {

			@Override
			protected void doExecute() {
				Poc.getGoogledocsService().getDocuments(this);
			}

			@Override
			protected void handleSuccess(GoogleDocumentListPayload result) {
				super.handleSuccess(result);
				showDocuments(result.getDocuments());
			}
		}.execute();
	}

	private void showDocuments(List<GoogleDocument> documents) {
		setGoogleDocs(documents);
	}

	private void doImport(final Collection<GoogleDocument> resourceId) {
		final int currentImportId = ++importId;
		new RpcCommand<DocRefListPayload>() {

			@Override
			protected void doExecute() {
				Poc.getGoogledocsService().download(resourceId, this);
			}

			@Override
			protected void handleSuccess(DocRefListPayload result) {
				super.handleSuccess(result);
				if (currentImportId == importId) {
					refreshList(result.getDocRefs());
					List<Msg> msgs = result.getStatus().getMsgs();
					if (msgs == null || msgs.isEmpty()) {
						hide();
					} else {
						Map<String, String> failure = new HashMap<String, String>();
						Set<String> downloaded = new HashSet<String>();
						for (Msg msg : result.getStatus().getMsgs()) {
							failure.put(msg.getRefToken(), msg.getMsg());
						}
						for (GoogleDocument gdoc : resourceId) {
							downloaded.add(gdoc.getResourceId());
						}
						downloaded.removeAll(failure.keySet());
						importPanel.setEnabled(true);
						importPanel.addGoogleDocsFailure(failure);
						importPanel.addGoogleDocsDownloaded(downloaded);
						importPanel.resetValue();
					}
				}
			}
		}.execute();
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

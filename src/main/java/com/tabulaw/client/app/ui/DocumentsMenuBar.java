package com.tabulaw.client.app.ui;

import java.util.List;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;
import com.tabulaw.client.app.Poc;
import com.tabulaw.client.app.model.ClientModelCache;
import com.tabulaw.client.app.view.DocViewInitializer;
import com.tabulaw.client.model.IModelChangeHandler;
import com.tabulaw.client.model.ModelChangeEvent;
import com.tabulaw.client.ui.Notifier;
import com.tabulaw.client.view.ShowViewRequest;
import com.tabulaw.client.view.ViewManager;
import com.tabulaw.model.DocKey;
import com.tabulaw.model.DocRef;
import com.tabulaw.model.EntityType;

public class DocumentsMenuBar extends MenuBar {

	private final static DocumentsMenuBar MENU_BAR = new DocumentsMenuBar();

	private DocCreateDialog dlg;

	public DocumentsMenuBar() {
		super(true);
		update();
		Poc.getPortal().addModelChangeHandler(new IModelChangeHandler() {
			@Override
			public void onModelChangeEvent(ModelChangeEvent event) {
				update();
			}
		});
	}

	@SuppressWarnings("unchecked")
	public void update() {
		clearItems();
		addNewDocumentMenuItem();
		List<DocRef> docs = (List<DocRef>) ClientModelCache.get().getAll(
				EntityType.DOCUMENT);
		int count = 0;
		for (final DocRef doc : docs) {
			if (count++ >= 10) {
				break;
			}
			addItem(doc.getName(), new Command() {
				@Override
				public void execute() {
					Notifier.get().info("Current Document set.");
					DocKey docKey = doc.getModelKey();
					DocViewInitializer dvi = new DocViewInitializer(
							docKey);
					ViewManager.get().dispatch(new ShowViewRequest(dvi));
				}
			});
		}
	}

	private void addNewDocumentMenuItem(){
		addItem("New Document ...", new Command(){
			@Override
			public void execute() {
				if(dlg == null) {
					dlg = new DocCreateDialog();
					dlg.setGlassEnabled(true);
				}
				dlg.center();
			}			
		});
		addSeparator();
	}
	public static MenuBar getDocumentsMenuBar() {
		return MENU_BAR;
	}
}

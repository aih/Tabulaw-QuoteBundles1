package com.tabulaw.client.app.model;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;
import com.tabulaw.client.app.Poc;
import com.tabulaw.client.app.view.DocViewInitializer;
import com.tabulaw.client.ui.Notifier;
import com.tabulaw.client.ui.RpcCommand;
import com.tabulaw.client.ui.msg.Msgs;
import com.tabulaw.client.view.ShowViewRequest;
import com.tabulaw.client.view.ViewManager;
import com.tabulaw.common.data.rpc.DocPayload;
import com.tabulaw.common.msg.Msg;
import com.tabulaw.model.DocContent;
import com.tabulaw.model.DocKey;
import com.tabulaw.model.DocRef;

public class GoogleScholarDocFetcher {

	public static class GoogleScholarRpcCommand extends RpcCommand<DocPayload> {
		private String url;
		private Widget popupErrorContainer;
		private Command callback;

		public GoogleScholarRpcCommand(String url, Widget source, Widget popupErrorContainer, Command callback) {
			this.url = url;
			this.source = source;
			this.popupErrorContainer = popupErrorContainer;
			this.callback = callback;

		}

		@Override
		protected void doExecute() {
			Poc.getDocService().fetch(url, this);
		}

		@Override
		protected void handleFailure(Throwable caught) {
			super.handleFailure(caught);
			Log.error("Unable to fetch remote document", caught);
		}

		@Override
		protected void handleSuccess(DocPayload result) {
			super.handleSuccess(result);
			if (result.hasErrors()) {
				if (popupErrorContainer != null) {
					Msgs.post(result.getStatus().getMsgs(Msg.MsgAttr.EXCEPTION.flag), popupErrorContainer);
				}
				return;
			}

			final DocRef docRef = result.getDocRef();
			final DocKey docKey = docRef.getModelKey();
			final DocContent docContent = result.getDocContent();

			// persist the new doc and propagate through app
			ClientModelCache.get().persist(docRef, source);
			if (docContent != null)
				ClientModelCache.get().persist(docContent, null);

			showDoc(docKey, callback);
		}

	}
	public static void fetchGoogleScholarDoc(String url, Widget source, Widget popupErrorContainer){
		fetchGoogleScholarDoc(url, source, popupErrorContainer, null);
	}
	
	public static void fetchGoogleScholarDoc(String url, Widget source, Widget popupErrorContainer, Command callback){
		Log.debug("Checking for client cache of docRemoteUrl: " + url);
		DocRef mDoc = ClientModelCache.get().getCaseDocByRemoteUrl(url);
		if(mDoc == null) {
			Log.debug("Fetching remote doc: " + url);

			GoogleScholarRpcCommand googleScholarRpcCommand = new GoogleScholarRpcCommand(url, source, popupErrorContainer, callback);
			googleScholarRpcCommand.execute();
		}
		else {
			showDoc(mDoc.getModelKey(), callback);
		}
		
	}
	private static void showDoc(final DocKey docKey, Command callback) {
		final DocViewInitializer dvi = new DocViewInitializer(docKey);
		DeferredCommand.addCommand(new Command() {

			@Override
			public void execute() {
				ViewManager.get().dispatch(new ShowViewRequest(dvi));
			}
		});
		
		//additional external callback
		if (callback != null) {
			DeferredCommand.addCommand(callback);
		}
		
	}
}
/**
 * The Logic Lab
 * @author jpk
 * @since Mar 30, 2010
 */
package com.tabulaw.client.ui;

import java.util.ArrayList;
import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitHandler;
import com.tabulaw.client.data.rpc.RpcEvent;
import com.tabulaw.client.model.ClientModelCache;
import com.tabulaw.client.util.Fmt;
import com.tabulaw.client.util.GlobalFormat;
import com.tabulaw.common.data.dto.Doc;
import com.tabulaw.common.model.DocRef;
import com.tabulaw.common.model.EntityFactory;
import com.tabulaw.common.msg.Msg;
import com.tabulaw.common.msg.Msg.MsgLevel;
import com.tabulaw.util.StringUtil;

/**
 * Uploads documents to the tabulaw server.
 * <p>Fires a value change event signaling completion of upload.
 * @author jpk
 */
public class DocUploadWidget extends Composite implements HasValueChangeHandlers<String> {

	public static class Styles {

		public static final String DOC_UPLOAD = "docUpload";

		public static final String UPLOADS = "uploads";

		public static final String UPLOAD = "upload";

		public static final String SUBMIT = "submit";

		public static final String CANCEL = "cancel";
	}

	final FlowPanel panel = new FlowPanel();

	final FormPanel form = new FormPanel();

	final FlowPanel pnlUploads = new FlowPanel();

	final FileUpload[] uploads;

	final Button submit, cancel;

	final RpcUiHandler busyHandler;
	
	/**
	 * Constructor
	 * @param numUploadSlots the number of form file fields
	 * @param cancelHandler handles cancel events
	 * @param busyHandler if specified, called for form onSubmit and onSubmitComplete events 
	 */
	public DocUploadWidget(int numUploadSlots, final ClickHandler cancelHandler, RpcUiHandler busyHandler) {
		super();
		
		this.busyHandler = busyHandler;
		
		form.setAction(GWT.getModuleBaseURL() + "docupload");
		form.setEncoding(FormPanel.ENCODING_MULTIPART);
		form.setMethod(FormPanel.METHOD_POST);
		form.setWidget(panel);

		busyHandler = new RpcUiHandler(form);

		submit = new Button("Submit", new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				form.submit();
			}
		});
		submit.setStyleName(Styles.SUBMIT);

		cancel = new Button("Cancel", new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				cancelHandler.onClick(event);
			}
		});
		cancel.setStyleName(Styles.CANCEL);

		uploads = new FileUpload[numUploadSlots];
		for(int i = 0; i < numUploadSlots; i++) {
			FileUpload fu = new FileUpload();
			fu.setStyleName(Styles.UPLOAD);
			fu.setName("fupload_" + (i + 1));
			fu.setTitle("File " + (i + 1));
			uploads[i] = fu;
			pnlUploads.add(fu);
		}

		pnlUploads.setStyleName(Styles.UPLOADS);
		panel.setStyleName(Styles.DOC_UPLOAD);
		panel.add(pnlUploads);
		panel.add(submit);
		panel.add(cancel);

		form.addSubmitHandler(new SubmitHandler() {

			@Override
			public void onSubmit(SubmitEvent event) {
				int count = 0;
				for(FileUpload fu : uploads) {
					if(!StringUtil.isEmpty(fu.getFilename())) {
						count++;
					}
				}
				if(count == 0) {
					event.cancel();
					Notifier.get().info("At least one file must be specified for upload.");
					return;
				}
				if(DocUploadWidget.this.busyHandler != null) 
					DocUploadWidget.this.busyHandler.onRpcEvent(new RpcEvent(RpcEvent.Type.SENT));
			}
		});

		form.addSubmitCompleteHandler(new SubmitCompleteHandler() {

			@Override
			public void onSubmitComplete(SubmitCompleteEvent event) {
				// Notifier.get().info(event.getResults());
				if(DocUploadWidget.this.busyHandler != null) 
					DocUploadWidget.this.busyHandler.onRpcEvent(new RpcEvent(RpcEvent.Type.RECEIVED));
				String sresult = event.getResults();
				int startIndex = sresult.indexOf("[START]");
				int endIndex = sresult.indexOf("[END]");
				if(startIndex == -1 || endIndex == -1) {
					Notifier.get().error("Unable to upload the specified document(s).");
				}
				else {
					sresult = sresult.substring(startIndex + 7, endIndex);
					String[] sdocs = sresult.split(",");
					ArrayList<DocRef> mdocs = new ArrayList<DocRef>(sdocs.length);
					final ArrayList<Msg> msgs = new ArrayList<Msg>(sdocs.length);
					for(String sdoc : sdocs) {
						String[] nvs = sdoc.split("\\|");
						Doc doc = new Doc();
						for(String nv : nvs) {
							String[] arr = nv.split(":");
							String name = arr[0].trim(), value = arr[1].trim();
							if(name.endsWith("title")) {
								doc.setTitle(value);
							}
							else if(name.endsWith("date")) {
								Date date = Fmt.getDateTimeFormat(GlobalFormat.DATE).parse(value);
								doc.setDate(date);
							}
							else if(name.endsWith("hash")) {
								doc.setHash(value);
							}
						}
						DocRef mDoc = EntityFactory.get().buildDoc(doc.getTitle(), doc.getHash(), doc.getDate());
						mdocs.add(mDoc);
						msgs.add(new Msg("Document: '" + doc.getTitle() + "' uploaded.", MsgLevel.INFO));
					}
					// persist and propagate
					ClientModelCache.get().persistAll(mdocs, DocUploadWidget.this);
					ValueChangeEvent.fire(DocUploadWidget.this, "success");
					DeferredCommand.addCommand(new Command() {
						
						@Override
						public void execute() {
							Notifier.get().post(msgs, -1);
						}
					});
				}
			}
		});

		initWidget(form);
	}

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> handler) {
		return addHandler(handler, ValueChangeEvent.getType());
	}
}

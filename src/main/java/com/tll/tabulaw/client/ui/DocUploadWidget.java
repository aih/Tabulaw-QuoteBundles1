/**
 * The Logic Lab
 * @author jpk
 * @since Mar 30, 2010
 */
package com.tll.tabulaw.client.ui;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitHandler;
import com.tll.client.data.rpc.RpcEvent;
import com.tll.client.ui.RpcUiHandler;
import com.tll.client.util.Fmt;
import com.tll.client.util.GlobalFormat;
import com.tll.common.model.Model;
import com.tll.tabulaw.client.model.PocModelCache;
import com.tll.tabulaw.common.data.dto.Doc;
import com.tll.tabulaw.common.model.PocModelFactory;

/**
 * Uploads documents to the tabulaw server.
 * @author jpk
 */
public class DocUploadWidget extends AbstractModelChangeAwareWidget {

	public static class Styles {

		public static final String UPLOAD = "upload";
		public static final String SUBMIT = "submit";
	}

	private static final String UPLOAD_ACTION_URL = GWT.getModuleBaseURL() + "doc/upload";

	final FlowPanel panel = new FlowPanel();
	final FormPanel form = new FormPanel();
	final FileUpload upload = new FileUpload();
	final Button submit;

	// final TextField textBox = FieldFactory.ftext("fileName", null, "Filename",
	// null, 40);
	
	final RpcUiHandler busyHandler;

	public DocUploadWidget() {
		super();

		form.setAction(UPLOAD_ACTION_URL);
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

		upload.setName("upload");
		upload.setStyleName(Styles.UPLOAD);

		panel.add(upload);
		panel.add(submit);
		
		form.addSubmitHandler(new SubmitHandler() {
			
			@Override
			public void onSubmit(SubmitEvent event) {
				if(upload.getFilename() == null) {
					event.cancel();
					Notifier.get().info("Specify a filename to upload");
					return;
				}
				busyHandler.onRpcEvent(new RpcEvent(RpcEvent.Type.SENT));
			}
		});

		form.addSubmitCompleteHandler(new SubmitCompleteHandler() {
			
			@Override
			public void onSubmitComplete(SubmitCompleteEvent event) {
				//Notifier.get().info(event.getResults());
				busyHandler.onRpcEvent(new RpcEvent(RpcEvent.Type.RECEIVED));
				// TODO parse the response, stuff into new Model instance and fire value change event
				String sresult = event.getResults();
				sresult = sresult.replace("<pre>", "").replace("</pre>", "");
				String[] nvs = sresult.split("\\|");
				Doc doc = new Doc();
				for(String nv : nvs) {
					String[] arr = nv.split(":");
					String name = arr[0], value = arr[1];
					if("docTitle".equals(name)) {
						doc.setTitle(value);
					}
					else if("docDate".equals(name)) {
						Date date = Fmt.getDateTimeFormat(GlobalFormat.DATE).parse(value);
						doc.setDate(date);
					}
					else if("docHash".equals(name)) {
						doc.setHash(value);
					}
				}
				// persist and propagate
				Model mDoc = PocModelFactory.get().buildDoc(doc.getTitle(), doc.getHash(), doc.getDate());
				PocModelCache.get().persist(mDoc, DocUploadWidget.this);
			}
		});
		
		initWidget(form);
	}
}

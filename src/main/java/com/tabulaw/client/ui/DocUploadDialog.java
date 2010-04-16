/**
 * The Logic Lab
 * @author jpk
 * @since Apr 6, 2010
 */
package com.tabulaw.client.ui;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.tll.client.ui.Dialog;
import com.tll.client.ui.RpcUiHandler;

/**
 * Shows the doc upload panel in a dialog box.
 * @author jpk
 */
public class DocUploadDialog extends Dialog {

	private final DocUploadWidget docUpload;

	/**
	 * Constructor
	 */
	public DocUploadDialog() {
		super();
		
		docUpload = new DocUploadWidget(4, new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				hide();
			}
		}, new RpcUiHandler(this));
		
		setAnimationEnabled(true);
		setGlassEnabled(true);
		setText("Document upload");
		setWidget(docUpload);
		docUpload.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				hide();
			}
		});
	}

}

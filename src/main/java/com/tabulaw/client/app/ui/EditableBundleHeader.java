package com.tabulaw.client.app.ui;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.Widget;
import com.tabulaw.client.app.model.ClientModelCache;
import com.tabulaw.client.app.model.ServerPersistApi;
import com.tabulaw.client.app.ui.BundleEditWidget.Styles;
import com.tabulaw.client.convert.IConverter;
import com.tabulaw.client.ui.edit.EditableTextWidget;
import com.tabulaw.model.QuoteBundle;

/**
 * Bundle header widget with editable name/desc fields.
 * @author jpk
 */
public class EditableBundleHeader extends Composite {

	static final IConverter<String, String> headerDescTextExtractor = new IConverter<String, String>() {
		
		@Override
		public String convert(String in) throws IllegalArgumentException {
			int index = in.indexOf("</span>");
			if(index == -1) index = in.indexOf("</SPAN>");
			return in.substring(index + 7);
		}
	};

	static final IConverter<String, String> headerDescInnerHtmlSetter = new IConverter<String, String>() {
		
		@Override
		public String convert(String in) throws IllegalArgumentException {
			return "<span class=\"" + "echo" + "\">DESCRIPTION: </span>" + (in == null ? "" : in);
		}
	};

	protected final FlowPanel header = new FlowPanel();

//	protected final Label lblQb;
	
	protected final MenuBar menuBar = new MenuBar(false);	
	protected final MenuItem quoteBundlesMenuItem = new MenuItem("Quote Bundles", true, BundleQuotesMenuBar.getBundleQuotesMenuBar());
	
	protected final EditableTextWidget pName, pDesc;
	
	protected final FlowPanel buttons = new FlowPanel(); 

	protected QuoteBundle bundle;

	/**
	 * Constructor
	 */
	public EditableBundleHeader() {
		//lblQb = new Label("Quote Bundle");
		//lblQb = new Label();
		//lblQb.setStyleName("echo");
		menuBar.addStyleName("echo");
		menuBar.addItem(quoteBundlesMenuItem);

		buttons.setStyleName(Styles.BUTTONS);
		header.insert(buttons, 0);
		
		header.setStyleName("qbheader");
		//header.add(lblQb);
		header.add(menuBar);
		
		initWidget(header);

		pName = new EditableTextWidget();
		pName.addStyleName("name");
		pName.addValueChangeHandler(new ValueChangeHandler<String>() {

			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				bundle.setName(event.getValue());
				// save the quote bundle
				ClientModelCache.get().persist(bundle, EditableBundleHeader.this);
				// server side
				ServerPersistApi.get().updateBundleProps(bundle);
			}
		});
		header.add(pName);

		pDesc = new EditableTextWidget(headerDescTextExtractor, headerDescInnerHtmlSetter);
		pDesc.addStyleName("desc");
		pDesc.addValueChangeHandler(new ValueChangeHandler<String>() {

			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				bundle.setDescription(event.getValue());
				// save the quote bundle
				ClientModelCache.get().persist(bundle, EditableBundleHeader.this);
				// server side
				ServerPersistApi.get().updateBundleProps(bundle);
			}
		});
		header.add(pDesc);
		
		/*
		save = new Image(Resources.INSTANCE.save());
		save.setStyleName(Styles.SAVE);
		save.setTitle("Save Name and Description");
		save.setVisible(false);
		save.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				// save the quote bundle
				ClientModelCache.get().persist(bundle, EditHeader.this);
				// server side
				ClientModelCache.get().updateBundleProps(bundle);

				save.setVisible(false);
				undo.setVisible(false);
			}
		});

		undo = new Image(Resources.INSTANCE.undo());
		undo.setStyleName(Styles.UNDO);
		undo.setTitle("Revert Name and Description");
		undo.setVisible(false);
		undo.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				// revert
				pName.revert();
				pDesc.revert(); 
				save.setVisible(false);
				undo.setVisible(false);
			}
		});
		*/
	}

	/**
	 * Sets the quote bundle model updating the UI.
	 * @param bundle the quote bundle model data
	 */
	public void setModel(QuoteBundle bundle) {
		String name = bundle.getName();
		
		String desc = bundle.getDescription();
		
		// TODO debug
		//desc = bundle.toString();
		
		pName.setText(name == null ? "" : name);
		pDesc.setHTML(headerDescInnerHtmlSetter.convert(desc));
		this.bundle = bundle;
	}

	public final Widget getDraggable() {
		return menuBar;
	}
	
	protected void setLabelText(String text){
		quoteBundlesMenuItem.setText(text);
	}

}
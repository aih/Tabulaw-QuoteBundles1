package com.tabulaw.client.app.ui;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.TextBox;
import com.tabulaw.client.app.Poc;
import com.tabulaw.client.app.Resources;
import com.tabulaw.client.app.search.SearchEvent;

public class SearchWidget extends Composite {

	private final HorizontalPanel panel = new HorizontalPanel(); 
	private final HTML label = new HTML("Search open Quote Bundles:"); 
	private final TextBox search = new TextBox();
	private final Image searchButton = new Image(Resources.INSTANCE.magnifyingGlass());
	private final Image resetSearchButton = new Image(Resources.INSTANCE.XButton());

	public SearchWidget() {
		super();
		search.addStyleName("gwt-TextBox tbox");
		search.getElement().setAttribute("name", "quoteSearch");
		panel.setStyleName("search-widget");
		label.setWidth("200px");
		label.addStyleName("echo");
		panel.add(label);
		panel.add(search);
		panel.add(searchButton);
		panel.add(resetSearchButton);

		initWidget(panel);
		
		searchButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				Poc.getPortal().fireEvent(new SearchEvent(search.getText()));
			}
		});
		resetSearchButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent arg0) {
				Poc.getPortal().fireEvent(new SearchEvent(null));
			}
		}); 
		search.addKeyUpHandler(new KeyUpHandler() {

			@Override
			public void onKeyUp(KeyUpEvent event) {
				final int code = event.getNativeKeyCode();
				switch (code) {
				case 13:
					Poc.getPortal().fireEvent(new SearchEvent(search.getText()));
					break;
				case 27:
					search.setText(null);
					Poc.getPortal().fireEvent(new SearchEvent(null));
					break;
				}
			}
			
		});
	}
	
}

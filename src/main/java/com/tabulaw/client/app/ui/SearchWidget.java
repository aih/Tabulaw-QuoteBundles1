package com.tabulaw.client.app.ui;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.tabulaw.client.app.Poc;
import com.tabulaw.client.app.Resources;
import com.tabulaw.client.app.search.SearchEvent;
import com.tabulaw.client.ui.ImageButton;

public class SearchWidget extends Composite {

	private final FlowPanel panel = new FlowPanel(); 
	private final TextBox search = new TextBox();
	private final ImageButton searchButton = new ImageButton(Resources.INSTANCE.magnifyingGlassSmall(), "Search"); 		
	private final ImageButton resetSearchButton = new ImageButton(null,"X");

	public SearchWidget() {
		super();

		
		search.addStyleName("gwt-TextBox tbox");
		search.getElement().setAttribute("name", "quoteSearch");
		panel.setStyleName("search-widget");
		panel.add(search);
		searchButton.setWidth("80%");
		searchButton.getElement().setId("search");
		resetSearchButton.setWidth("20%");
		resetSearchButton.getElement().setId("reset");
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
			public void onClick(ClickEvent event) {
				search.setText(null);
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

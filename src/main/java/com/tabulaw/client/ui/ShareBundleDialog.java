package com.tabulaw.client.ui;


import com.allen_sauer.gwt.dnd.client.util.DOMUtil;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.tabulaw.client.app.Poc;
import com.tabulaw.client.app.ui.PermissionsListingWidget;
import com.tabulaw.client.app.ui.UsernameSuggestOracle;
import com.tabulaw.common.data.rpc.ModelPayload;
import com.tabulaw.model.QuoteBundle;
import com.tabulaw.model.User;

public class ShareBundleDialog extends AbstractInfoDialog {
	private final PermissionsListingWidget listing;
	private Button addButton = new Button("Add", this);
	private Button closeButton = new Button("Close", this);
	private SuggestBox suggestbox = new SuggestBox(new UsernameSuggestOracle());
	private QuoteBundle bundle;

	public void setBundle(QuoteBundle bundle) {
		this.bundle = bundle;
		listing.setBundleId(bundle.getId());
	}
	public ShareBundleDialog(String title) {
		super(title);
		FlowPanel panel = new FlowPanel(); 
		HorizontalPanel suggestPanel = new HorizontalPanel();
		suggestPanel.add(suggestbox);
		suggestPanel.add(addButton);

		listing = new PermissionsListingWidget();
		panel.add(suggestPanel);
		panel.add(listing );
		addContents(panel);
		addButton(closeButton);
	}

	@Override
	protected void onLoad() {
		super.onLoad();
		listing.refresh();
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			public void execute() {
				suggestbox.setFocus(true);
			}
		});
	}

	@Override
	public void onClick(ClickEvent clkEvt) {
		if (clkEvt.getSource() == closeButton) {
			hide();
		}
		if (clkEvt.getSource() == addButton) {
	        Poc.getUserDataService().getUserByEmail(suggestbox.getValue(),  new AsyncCallback<ModelPayload<User>>() {
	            public void onFailure(Throwable caught) {
	            	Window.alert(caught.getLocalizedMessage());
	            }
	            public void onSuccess(ModelPayload<User> payload) {
            		User user = payload.getModel();
	            	if	(user!=null) {
	            		Poc.getUserDataService().shareBundleForUser(user, bundle, new AsyncCallback<ModelPayload<QuoteBundle>>() {

							@Override
							public void onFailure(Throwable caught) {
				            	Window.alert(caught.getLocalizedMessage());
							}

							@Override
							public void onSuccess(ModelPayload<QuoteBundle> result) {
								listing.refresh(); 							
							}
						});
	            	}
	            }
	        });
			
		}
	}

}

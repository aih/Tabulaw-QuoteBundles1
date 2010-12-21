package com.tabulaw.client.app.ui.nav;

import com.google.gwt.user.client.ui.HTML;
import com.tabulaw.client.app.model.ClientModelCache;
import com.tabulaw.client.model.ModelChangeEvent;
import com.tabulaw.client.ui.AbstractModelChangeAwareWidget;
import com.tabulaw.model.QuoteBundle;

/**
 * Widget that updates its content from current quote bundle model change
 * events.
 * @author jpk
 */
public class CurrentQuoteBundleDisplayWidget extends AbstractModelChangeAwareWidget {

	private final HTML html = new HTML();
	private String crntQbId;

	public CurrentQuoteBundleDisplayWidget() {
		super();
		initWidget(html);
	}

	public void update() {
		QuoteBundle cqb = ClientModelCache.get().getCurrentQuoteBundle();
		if(cqb != null) {
			String id = cqb.getId();
			if(!id.equals(crntQbId)) {
				this.crntQbId = id;
				html.setHTML("<p><span class=\"echo\">Current Quote Bundle:</span>" + cqb.getName() + "</p>");
				html.setVisible(true);
			}
		}
		else {
			html.setVisible(false);
		}
	}

	@Override
	public void onModelChangeEvent(ModelChangeEvent event) {
		update();
	}

	public void clear() {
		html.setHTML("");
		crntQbId = null;
	}

} 
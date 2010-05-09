/**
 * The Logic Lab
 * @author jpk
 * @since Mar 23, 2010
 */
package com.tabulaw.client.app.ui;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.RichTextArea.Formatter;
import com.tabulaw.client.app.Resources;
import com.tabulaw.client.ui.toolbar.Toolbar;


/**
 * Edits documents using a rich text editor.
 * @author jpk
 */
public class DocEditWidget extends Composite implements HasHTML {

	class EditBar extends Composite implements ClickHandler {
		private final FlowPanel pnl = new FlowPanel();
		private final Toolbar toolbar = new Toolbar();
		private final Image imgPencil = new Image(Resources.INSTANCE.edit());
		private final PushButton btnBold;

		public EditBar() {
			super();
			imgPencil.setStyleName("pencil");
			btnBold = new PushButton("bold", this);
			
			toolbar.add(imgPencil);
			toolbar.addButton(btnBold, "Toggle Bold");
			
			pnl.setStyleName("editBar");
			pnl.add(toolbar);
			
			initWidget(pnl);
		}

		@Override
		public void onClick(ClickEvent event) {
			if(event.getSource() == btnBold) {
				rta.getFormatter().toggleBold();
			}
		}
	}
	
	private final EditBar editBar = new EditBar();

	private final RichTextArea rta = new RichTextArea();
	
	private final SimplePanel pnl = new SimplePanel();

	/**
	 * Constructor
	 */
	public DocEditWidget() {
		super();
		pnl.addStyleName("docEdit");
		pnl.add(rta);
		initWidget(pnl);
	}
	
	public EditBar getEditBar() {
		return editBar;
	}
	
	@Override
	public String getHTML() {
		return rta.getHTML();
	}
	
	@Override
	public void setHTML(String html) {
		rta.setHTML(html);
	}
	
	@Override
	public String getText() {
		return rta.getText();
	}

	@Override
	public void setText(String text) {
		rta.setText(text);
	}

	public Formatter getFormatter() {
		return rta.getFormatter();
	}
}


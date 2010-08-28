/**
 * The Logic Lab
 * @author jpk
 * @since Mar 23, 2010
 */
package com.tabulaw.client.app.ui;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
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
 * 
 * @author jpk
 */
public class DocEditWidget extends Composite implements HasHTML {

	class EditToolBar extends Composite implements ClickHandler {
		private final Toolbar toolbar = new Toolbar();
		private final FlowPanel pnl = new FlowPanel();

		Map<PushButton, ClickHandler> toolbarButtons = new LinkedHashMap<PushButton, ClickHandler>();
		Map<PushButton, String> toolbarTips = new HashMap<PushButton, String>();

		public EditToolBar() {
			super();

			registerControls();

			for (Map.Entry<PushButton, ClickHandler> entry : toolbarButtons.entrySet()) {
				PushButton button = entry.getKey();
				String tip = null;
				if (toolbarTips.containsKey(button)) {
					tip = toolbarTips.get(button);
				}
				toolbar.addButton(button, tip);

			}

			pnl.add(toolbar);
			initWidget(pnl);

		}

		private void addImageButton(ImageResource image, String tip, ClickHandler handler) {
			PushButton button = new PushButton(new Image(image), this);
			toolbarButtons.put(button, handler);
			toolbarTips.put(button, tip);
		}

		private void registerControls() {
			/*------text styles----------*/
			addImageButton(Resources.INSTANCE.bold(), "Bold", new ClickHandler() {
				public void onClick(ClickEvent event) {
					rta.getFormatter().toggleBold();
				}
			});

			addImageButton(Resources.INSTANCE.italic(), "Italic", new ClickHandler() {
				public void onClick(ClickEvent event) {
					rta.getFormatter().toggleItalic();
				}
			});

			addImageButton(Resources.INSTANCE.underline(), "Underline", new ClickHandler() {
				public void onClick(ClickEvent event) {
					rta.getFormatter().toggleUnderline();
				}
			});

			addImageButton(Resources.INSTANCE.subscript(), "Subscript", new ClickHandler() {
				public void onClick(ClickEvent event) {
					rta.getFormatter().toggleSubscript();
				}
			});
			addImageButton(Resources.INSTANCE.superscript(), "Superscript", new ClickHandler() {
				public void onClick(ClickEvent event) {
					rta.getFormatter().toggleSuperscript();
				}
			});

			/*------paragraph styles----------*/
			addImageButton(Resources.INSTANCE.indent(), "Left Indent", new ClickHandler() {
				public void onClick(ClickEvent event) {
					rta.getFormatter().rightIndent();
				}
			});

			addImageButton(Resources.INSTANCE.outdent(), "Left Indent", new ClickHandler() {
				public void onClick(ClickEvent event) {
					rta.getFormatter().leftIndent();
				}
			});

			addImageButton(Resources.INSTANCE.justifyLeft(), "Justify Left", new ClickHandler() {
				public void onClick(ClickEvent event) {
					rta.getFormatter().setJustification(RichTextArea.Justification.LEFT);
				}
			});

			addImageButton(Resources.INSTANCE.justifyCenter(), "Justify Center", new ClickHandler() {
				public void onClick(ClickEvent event) {
					rta.getFormatter().setJustification(RichTextArea.Justification.CENTER);
				}
			});

			addImageButton(Resources.INSTANCE.justifyRight(), "Justify Right", new ClickHandler() {
				public void onClick(ClickEvent event) {
					rta.getFormatter().setJustification(RichTextArea.Justification.RIGHT);
				}
			});
		}

		public void onClick(ClickEvent event) {
			if (toolbarButtons.containsKey(event.getSource())) {
				ClickHandler handler = toolbarButtons.get(event.getSource());
				handler.onClick(event);
			}

		}

	}

	private final EditToolBar editBar = new EditToolBar();

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

	public EditToolBar getEditToolBar() {
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

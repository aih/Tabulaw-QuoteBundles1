/**
 * The Logic Lab
 * @author jpk
 * @since Mar 23, 2010
 */
package com.tabulaw.client.app.ui;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.CustomButton;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.RichTextArea.Formatter;
import com.tabulaw.client.app.Resources;
import com.tabulaw.client.app.ui.event.IframeClickEvent;
import com.tabulaw.client.app.ui.event.IframeClickedHandler;
import com.tabulaw.client.ui.toolbar.Toolbar;

/**
 * Edits documents using a rich text editor.
 * 
 * @author jpk
 */
public class DocEditWidget extends Composite implements HasHTML {

	class EditToolBar extends Composite implements ClickHandler, ChangeHandler, KeyPressHandler   {
		private final int SPECIAL_CHAR_MENU_POSITION=5;
		
		private final Toolbar toolbar = new Toolbar();
		private final FlowPanel pnl = new FlowPanel();
		final private HandlerManager handlerManager = new HandlerManager(this);		

		private class SpecialCharCommand implements Command {
			private String html;

			public SpecialCharCommand(String html) {
				this.html = html;
			}

			@Override
			public void execute() {
				rta.getFormatter().insertHTML(html);
			}

		}

		private Map<CustomButton, ClickHandler> toolbarButtons = new LinkedHashMap<CustomButton, ClickHandler>();
		private Map<ListBox, ChangeHandler> toolbarListBoxes = new LinkedHashMap<ListBox, ChangeHandler>();
		private Map<String, String> headings = new LinkedHashMap<String, String>();
		private Map<String, FocusWidget> elements = new HashMap<String, FocusWidget>();

		private ListBox headingListBox;
		private ToggleButton bold, italic, underline, subscript, superscript;
		private MenuBar specialCharsMenuTop;

		public EditToolBar() {
			super();
			headings.put("p", "text");
			headings.put("h1", "Heading 1");
			headings.put("h2", "Heading 2");
			headings.put("h3", "Heading 3");

			registerControls();

			pnl.add(toolbar);
			initWidget(pnl);
			setStyleName("gwt-RichTextToolbar");

		}

		private void addImageButton(CustomButton button, String tip, ClickHandler handler) {
			button.addClickHandler(this);
			toolbarButtons.put(button, handler);
			toolbar.addButton(button, tip);
		}

		private void addListBox(ListBox listBox, Map<String, String> options, ChangeHandler handler) {
			listBox.addChangeHandler(this);
			listBox.setVisibleItemCount(1);
			for (Map.Entry<String, String> option : options.entrySet()) {
				listBox.addItem(option.getValue(), option.getKey());
			}

			toolbarListBoxes.put(listBox, handler);
			toolbar.add(listBox);
		}

		private void addSpecialCharMenuItem(MenuBar menubar, String html) {
			MenuItem menuItem = new MenuItem(html, true, new SpecialCharCommand(html));
			menubar.addItem(menuItem);

		}

		private MenuBar addSpecialCharMenu() {
			MenuBar topMenu = new MenuBar();

			MenuBar specialCharsMenu = new MenuBar(true);

			topMenu.addItem("<img src='poc/images/toolbar/specialChars.gif'/>", true, specialCharsMenu);
			// TODO Rename style. Set proper icon
			topMenu.setStyleName("quoteBundleMenuItem");

			addSpecialCharMenuItem(specialCharsMenu, "&para;");
			addSpecialCharMenuItem(specialCharsMenu, "&sect;");
			addSpecialCharMenuItem(specialCharsMenu, "&dagger;");
			addSpecialCharMenuItem(specialCharsMenu, "&Dagger;");
			
			return topMenu;
		}

		private void registerControls() {

			/*------text styles----------*/
			bold = new ToggleButton(new Image(Resources.INSTANCE.bold()));
			addImageButton(bold, "Bold", new ClickHandler() {
				public void onClick(ClickEvent event) {
					toggleBold();
				}
			});

			italic = new ToggleButton(new Image(Resources.INSTANCE.italic()));
			addImageButton(italic, "Italic", new ClickHandler() {
				public void onClick(ClickEvent event) {
					toggleItalic();
				}
			});
			underline = new ToggleButton(new Image(Resources.INSTANCE.underline()));
			addImageButton(underline, "Underline", new ClickHandler() {
				public void onClick(ClickEvent event) {
					toggleUnderline();
				}
			});

			subscript = new ToggleButton(new Image(Resources.INSTANCE.subscript()));
			addImageButton(subscript, "Subscript", new ClickHandler() {
				public void onClick(ClickEvent event) {
					rta.getFormatter().toggleSubscript();
				}
			});
			superscript = new ToggleButton(new Image(Resources.INSTANCE.superscript()));
			addImageButton(superscript, "Superscript", new ClickHandler() {
				public void onClick(ClickEvent event) {
					rta.getFormatter().toggleSuperscript();
				}
			});
			specialCharsMenuTop = addSpecialCharMenu();			
			toolbar.add(specialCharsMenuTop);
 
			/*------paragraph styles----------*/
			addImageButton(new PushButton(new Image(Resources.INSTANCE.indent())), "Left Indent", new ClickHandler() {
				public void onClick(ClickEvent event) {
					rta.getFormatter().rightIndent();
				}
			});

			addImageButton(new PushButton(new Image(Resources.INSTANCE.outdent())), "Left Indent", new ClickHandler() {
				public void onClick(ClickEvent event) {
					rta.getFormatter().leftIndent();
				}
			});

			addImageButton(new PushButton(new Image(Resources.INSTANCE.justifyLeft())), "Justify Left",
					new ClickHandler() {
						public void onClick(ClickEvent event) {
							rta.getFormatter().setJustification(RichTextArea.Justification.LEFT);
						}
					});

			addImageButton(new PushButton(new Image(Resources.INSTANCE.justifyCenter())), "Justify Center",
					new ClickHandler() {
						public void onClick(ClickEvent event) {
							rta.getFormatter().setJustification(RichTextArea.Justification.CENTER);
						}
					});

			addImageButton(new PushButton(new Image(Resources.INSTANCE.justifyRight())), "Justify Right",
					new ClickHandler() {
						public void onClick(ClickEvent event) {
							rta.getFormatter().setJustification(RichTextArea.Justification.RIGHT);
						}
					});
			headingListBox = new ListBox();
			addListBox(headingListBox, headings, new ChangeHandler() {
				@Override
				public void onChange(ChangeEvent event) {
					ListBox listBox = (ListBox) event.getSource();
					int listIndex = listBox.getSelectedIndex();
					String tag = listBox.getValue(listIndex);
					executeCommand("FormatBlock", tag);
				}
			});
		}

		public void onClick(ClickEvent event) {
			if (toolbarButtons.containsKey(event.getSource())) {
				ClickHandler handler = toolbarButtons.get(event.getSource());
				handler.onClick(event);
			}
			updateStatus();
			hideMenu();
		}

		@Override
		public void onChange(ChangeEvent event) {
			if (toolbarListBoxes.containsKey(event.getSource())) {
				ChangeHandler handler = toolbarListBoxes.get(event.getSource());
				handler.onChange(event);
			}
			rta.setFocus(true);
		}
		
		@Override
		public void onKeyPress(KeyPressEvent event) {
			updateStatus();
			Character eventChar = event.getCharCode();
			eventChar = Character.toUpperCase(eventChar);
			boolean preventDefault = false;
			if (event.isControlKeyDown()) {
				switch (eventChar) {
				case 'B':
					toggleBold();
					preventDefault = true;
					break;
				case 'I':
					toggleItalic();
					preventDefault = true;
					break;
				case 'U':
					toggleUnderline();
					preventDefault = true;
					break;
				}
				if (preventDefault) {
					event.preventDefault();
				}
			}
		}
		private void addIframeClickedHandler(IframeClickedHandler handler) {
			  handlerManager.addHandler(IframeClickEvent.getType(),handler);  
		}

		private void updateStatus() {
			String blockTag=null;
			try {
				blockTag = queryCommandValueAssumingFocus("FormatBlock");
			}
			catch(Exception e){
				blockTag="p";
			}
			if (blockTag!=null) {
				Iterator<String> iterator = headings.keySet().iterator();
				for (int i = 0; iterator.hasNext(); i++) {
					String key = iterator.next();
					if (key.equals(blockTag)) {
						headingListBox.setSelectedIndex(i);
					}
				}
			}

			bold.setDown(getFormatter().isBold());
			italic.setDown(getFormatter().isItalic());
			underline.setDown(getFormatter().isUnderlined());
			subscript.setDown(getFormatter().isSubscript());
			superscript.setDown(getFormatter().isSuperscript());

		}
		private void toggleBold() {
			rta.getFormatter().toggleBold();
		}
		private void toggleItalic() {
			rta.getFormatter().toggleItalic();
		}
		private void toggleUnderline() {
			rta.getFormatter().toggleUnderline();
		}
		private void resetSpecialCharsMenu(){
			specialCharsMenuTop.removeFromParent();
			toolbar.insert(specialCharsMenuTop, SPECIAL_CHAR_MENU_POSITION);
		}
		private void hideMenu() {
			resetSpecialCharsMenu();
			handlerManager.fireEvent(new IframeClickEvent());
		}
	}

	private final EditToolBar editBar = new EditToolBar();

	private final RichTextArea rta = new RichTextArea();

	private final Element elem = rta.getElement();

	private final SimplePanel pnl = new SimplePanel();

	/**
	 * Constructor
	 */
	public DocEditWidget() {
		super();
		pnl.addStyleName("docEdit");
		pnl.addStyleName("documentContainer");
		pnl.add(rta);
		rta.addKeyPressHandler(editBar);
		rta.addClickHandler(editBar);
		initWidget(pnl);
	}
	

	private native void executeCommand(String cmd, String param) /*-{
		this.@com.tabulaw.client.app.ui.DocEditWidget::elem.contentWindow.document.execCommand(cmd, false, param);
	}-*/;

	private native String queryCommandValueAssumingFocus(String cmd) /*-{
		return this.@com.tabulaw.client.app.ui.DocEditWidget::elem.contentWindow.document.queryCommandValue(cmd);
	}-*/;

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
	public void addIframeClickedHandler(IframeClickedHandler handler) {
		editBar.addIframeClickedHandler(handler);  
	}
}

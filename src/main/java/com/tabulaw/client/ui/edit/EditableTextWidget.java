/**
 * The Logic Lab
 * @author jpk
 * @since Apr 27, 2010
 */
package com.tabulaw.client.ui.edit;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.tabulaw.client.convert.IConverter;
import com.tabulaw.client.ui.FocusCommand;
import com.tabulaw.client.ui.field.TextField;

/**
 * Wraps a TextField allowing it to be clicked in read-only mode whereby the
 * field turns editable.
 * @author jpk
 */
public class EditableTextWidget extends TextField implements HasHTML {

	static class StringPassThroughConverter implements IConverter<String, String> {

		@Override
		public String convert(String in) throws IllegalArgumentException {
			return in;
		}

	}

	private static final StringPassThroughConverter stringPassThroughConverter = new StringPassThroughConverter();

	private final IConverter<String, String> html2text, text2html;

	private HandlerRegistration hrMouseOver, hrMouseOut, hrClick, hrKeyDown, hrBlur;
	private String origValue;

	/**
	 * Constructor
	 */
	public EditableTextWidget() {
		this(stringPassThroughConverter, stringPassThroughConverter);
	}

	/**
	 * Constructor
	 * @param html2text responsible for extracting desired edit text from the
	 *        innerHTML of static text
	 * @param text2html responsible for re-setting the static html from the edited text content
	 */
	public EditableTextWidget(IConverter<String, String> html2text, IConverter<String, String> text2html) {
		super("tfield", null, null, null, 30);
		if(html2text == null || text2html == null) throw new NullPointerException();
		((TextBox) getEditable()).setTitle("'Enter' to save or 'Esc' to cancel");
		this.html2text = html2text;
		this.text2html = text2html;
	}

	protected void init() {
		if(hrMouseOut == null) {
			hrMouseOut = getReadOnlyWidget().addMouseOutHandler(new MouseOutHandler() {

				@Override
				public void onMouseOut(MouseOutEvent event) {
					((Widget) event.getSource()).removeStyleName("hover");
				}
			});
			hrMouseOver = getReadOnlyWidget().addMouseOverHandler(new MouseOverHandler() {

				@Override
				public void onMouseOver(MouseOverEvent event) {
					((Widget) event.getSource()).addStyleName("hover");
				}
			});
			hrClick = getReadOnlyWidget().addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					removeStyleName("hover");
					int width = getReadOnlyWidget().getOffsetWidth();
					((Widget) getEditable()).getElement().getStyle().setWidth(width, Style.Unit.PX);
					origValue = getHTML();
					setReadOnly(false);
					getEditable().setValue(extractText(getReadOnlyWidget().getHTML()), false);
					DeferredCommand.addCommand(new FocusCommand(getEditable(), true));
				}
			});

			hrKeyDown = getEditable().addKeyDownHandler(new KeyDownHandler() {

				@Override
				public void onKeyDown(KeyDownEvent event) {
					if(event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
						setHTML(origValue);
						setReadOnly(true);
					}
					else if(event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
						String changedValue = getEditable().getValue();
						getReadOnlyWidget().setHTML(text2html.convert(changedValue));
						setReadOnly(true);
						ValueChangeEvent.fireIfNotEqual(EditableTextWidget.this, html2text.convert(origValue), changedValue);
					}
				}
			});
			
			hrBlur = getEditable().addBlurHandler(new BlurHandler() {
				
				@Override
				public void onBlur(BlurEvent event) {
					setReadOnly(true);
				}
			});
			
			setReadOnly(true);
		}
	}

	protected void shutdown() {
		if(hrMouseOut != null) {
			hrMouseOut.removeHandler();
			hrMouseOver.removeHandler();
			hrClick.removeHandler();
			hrKeyDown.removeHandler();
			hrBlur.removeHandler();
			hrMouseOut = hrMouseOver = hrClick = hrKeyDown = hrBlur = null;
		}
	}

	@Override
	protected void onLoad() {
		super.onLoad();
		init();
	}

	@Override
	protected void onUnload() {
		super.onUnload();
		shutdown();
	}

	@Override
	public String getText() {
		return getReadOnlyWidget().getText();
	}

	@Override
	public void setText(String text) {
		getReadOnlyWidget().setText(text);
		//((TextBox) getEditable()).setText(text);
	}

	@Override
	public String getHTML() {
		return getReadOnlyWidget().getHTML();
	}

	@Override
	public void setHTML(String html) {
		getReadOnlyWidget().setHTML(html);
		//((TextBox) getEditable()).setText(extractText(html));
	}

	protected String extractText(String html) {
		return html2text.convert(html);
	}

	@Override
	protected void setReadOnlyContent() {
		// no-op - we manage it
	}

}

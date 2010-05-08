/**
 * The Logic Lab
 * @author jpk Nov 3, 2007
 */
package com.tabulaw.client.ui.edit;

import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.tabulaw.client.ui.FocusCommand;
import com.tabulaw.client.ui.field.AbstractFieldPanel;
import com.tabulaw.client.ui.field.FieldGroup;
import com.tabulaw.client.ui.field.IFieldWidget;
import com.tabulaw.client.ui.msg.IMsgDisplay;
import com.tabulaw.client.validate.Error;
import com.tabulaw.client.validate.ErrorClassifier;
import com.tabulaw.client.validate.ErrorDisplay;
import com.tabulaw.client.validate.ErrorHandlerDelegate;
import com.tabulaw.common.msg.Msg;

/**
 * Wraps a panel holding editable content that fires edit events.
 * @param <T> edit content type
 * @author jpk
 */
public abstract class AbstractEditPanel<T> extends Composite implements ClickHandler, IHasEditHandlers<T> {

	/**
	 * Styles - (admin.css)
	 * @author jpk
	 */
	protected static class Styles {

		/**
		 * The style name for {@link AbstractEditPanel}s.
		 */
		public static final String ENTITY_EDIT = "entityEdit";
		/**
		 * The button row style.
		 */
		public static final String BTN_ROW = "btnRow";
		/**
		 * Save style.
		 */
		public static final String SAVE = "save";
		/**
		 * Cancel style.
		 */
		public static final String CANCEL = "cancel";
		/**
		 * Delete style.
		 */
		public static final String DELETE = "delete";
		/**
		 * Reset style.
		 */
		public static final String RESET = "reset";
		/**
		 * The edit portal style.
		 */
		public static final String PORTAL = "portal";
	}

	private static final String DEFAULT_SAVE_TEXT = "Save";
	private static final String DEFAULT_DELETE_TEXT = "Delete";
	private static final String DEFAULT_CANCEL_TEXT = "Cancel";
	private static final String DEFAULT_RESET_TEXT = "Reset";

	/**
	 * The composite's target widget
	 */
	protected final FlowPanel panel = new FlowPanel();

	private final SimplePanel portal = new SimplePanel();

	/**
	 * Contains the actual edit fields.
	 */
	protected AbstractFieldPanel fieldPanel;

	/**
	 * The optional set error handler which get passed into the field group in the
	 * current field panel.
	 */
	protected ErrorHandlerDelegate errorHandler;

	/**
	 * The panel containing the edit buttons
	 */
	private final FlowPanel pnlButtonRow = new FlowPanel();

	private final Button btnSave;

	private final Button btnReset, btnDelete, btnCancel;

	/**
	 * Constructor
	 */
	public AbstractEditPanel() {
		this(null, null, null, null);
	}

	/**
	 * Constructor
	 * @param saveText Save button text (required)
	 * @param deleteText Delete button text (optional). If <code>null</code>,
	 *        delete button is not displayed.
	 * @param cancelText Cancel button text (optional). If <code>null</code>,
	 *        cancel button is not displayed.
	 * @param resetText Reset button text (optional). If <code>null</code>, reset
	 *        button is not displayed.
	 */
	public AbstractEditPanel(String saveText, String deleteText, String cancelText, String resetText) {
		super();
		portal.setStyleName(Styles.PORTAL);

		pnlButtonRow.setStyleName(Styles.BTN_ROW);

		String btnText;

		btnText = saveText == null ? DEFAULT_SAVE_TEXT : saveText;
		btnSave = new Button(btnText, this);
		btnSave.addStyleName(Styles.SAVE);
		pnlButtonRow.add(btnSave);

		btnText = deleteText == null ? DEFAULT_DELETE_TEXT : deleteText;
		btnDelete = new Button(btnText, this);
		btnDelete.addStyleName(Styles.DELETE);
		btnDelete.setVisible(deleteText != null);
		pnlButtonRow.add(btnDelete);

		btnText = cancelText == null ? DEFAULT_CANCEL_TEXT : cancelText;
		btnCancel = new Button(btnText, this);
		btnCancel.addStyleName(Styles.CANCEL);
		btnCancel.setVisible(cancelText != null);
		pnlButtonRow.add(btnCancel);

		btnText = resetText == null ? DEFAULT_RESET_TEXT : resetText;
		btnReset = new Button(btnText, this);
		btnReset.addStyleName(Styles.RESET);
		btnReset.setVisible(resetText != null);
		pnlButtonRow.add(btnReset);

		panel.add(portal);
		panel.add(pnlButtonRow);
		panel.setStyleName(Styles.ENTITY_EDIT);

		initWidget(panel);
	}

	/**
	 * Constructor
	 * @param saveText Save button text (required)
	 * @param deleteText Delete button text (optional). If <code>null</code>,
	 *        delete button is not displayed.
	 * @param cancelText Cancel button text (optional). If <code>null</code>,
	 *        cancel button is not displayed.
	 * @param resetText Reset button text (optional). If <code>null</code>, reset
	 *        button is not displayed.
	 * @param fieldPanel the field panel
	 */
	public AbstractEditPanel(String saveText, String deleteText, String cancelText, String resetText, AbstractFieldPanel fieldPanel) {
		this(saveText, deleteText, cancelText, resetText);
		setFieldPanel(fieldPanel);
	}

	protected final Panel getPortal() {
		return portal;
	}

	public final AbstractFieldPanel getFieldPanel() {
		return fieldPanel;
	}

	public final void setFieldPanel(AbstractFieldPanel fieldPanel) {
		if(fieldPanel == null) throw new IllegalArgumentException("A field panel must be specified.");
		if(this.fieldPanel == fieldPanel) return;
		this.fieldPanel = fieldPanel;
		portal.setWidget(fieldPanel);
		
		// transfer over the error handler
		if(errorHandler != null) fieldPanel.setErrorHandler(errorHandler);
	}

	/**
	 * Sets the error handler for field validation feedback and optionally adds
	 * the message display to this panel.
	 * @param errorHandler the error handler to set
	 * @param addMsgDisplay add the held msg display to this panel?
	 */
	public void setErrorHandler(ErrorHandlerDelegate errorHandler, boolean addMsgDisplay) {
		if(errorHandler == null) throw new NullPointerException();
		if(this.errorHandler == errorHandler) return;
		
		// remove old error handler
		if(this.errorHandler != null) {
			IMsgDisplay msgDisplay = this.errorHandler.getMsgDisplay();
			if(msgDisplay != null) msgDisplay.getDisplayWidget().removeFromParent();
		}
		
		// set new error handler
		if(fieldPanel != null) fieldPanel.setErrorHandler(errorHandler);
		if(addMsgDisplay) {
			IMsgDisplay msgDisplay = errorHandler.getMsgDisplay();
			if(msgDisplay != null) panel.insert(msgDisplay.getDisplayWidget(), 0);
		}
		this.errorHandler = errorHandler;
	}

	public final void setSaveButtonText(String text) {
		btnSave.setText(text);
	}

	public final void setDeleteButtonText(String text) {
		btnDelete.setText(text);
	}

	public final void setCancelButtonText(String text) {
		btnCancel.setText(text);
	}

	public final void setResetButtonText(String text) {
		btnReset.setText(text);
	}

	public final void showDeleteButton(boolean show) {
		btnDelete.setVisible(show);
	}

	public final void showCancelButton(boolean show) {
		btnCancel.setVisible(show);
	}

	public final void showResetButton(boolean show) {
		btnReset.setVisible(show);
	}

	@Override
	public final HandlerRegistration addEditHandler(IEditHandler<T> handler) {
		return addHandler(handler, EditEvent.TYPE);
	}

	/**
	 * Applies field error messages to the fields contained in the member
	 * {@link AbstractFieldPanel}.
	 * @param msgs The field error messages to apply
	 * @param classifier the error classifier
	 * @param clearExisting Remove existing errors of the given error classifier
	 *        before applying?
	 */
	public final void applyFieldErrors(final List<Msg> msgs, ErrorClassifier classifier, boolean clearExisting) {
		final FieldGroup root;
		try {
			root = fieldPanel.getFieldGroup();
		}
		catch(final IllegalStateException e) {
			// presume field group not initialized yet
			return;
		}
		if(clearExisting) errorHandler.clear(classifier);
		for(final Msg msg : msgs) {
			final IFieldWidget<?> fw = root.getFieldWidgetByProperty(msg.getRefToken());
			String emsg;
			if(fw != null) {
				emsg = msg.getMsg();
			}
			else {
				emsg = msg.getRefToken() + ": " + msg.getMsg();
			}
			errorHandler.handleError(new Error(classifier, fw, emsg), ErrorDisplay.ALL_FLAGS);
		}
	}

	/**
	 * @return The edit content.
	 */
	protected abstract T getEditContent();

	public final void onClick(ClickEvent event) {
		final Object sender = event.getSource();
		if(sender == btnSave) {
			if(fieldPanel.getFieldGroup().isValid()) {
				T editContent = getEditContent();
				if(editContent != null) {
					EditEvent.fireSave(this, editContent);
				}
			}
		}
		else if(sender == btnReset) {
			fieldPanel.getFieldGroup().reset();
		}
		else if(sender == btnDelete) {
			// T editContent = getEditContent();
			// if(editContent != null) {
			EditEvent.fireDelete(this);
			// }
		}
		else if(sender == btnCancel) {
			EditEvent.fireCancel(this);
		}
	}

	/**
	 * Turns on/off editability.
	 * @param editable
	 */
	public void setEditable(boolean editable) {
		fieldPanel.getFieldGroup().setReadOnly(!editable);
		pnlButtonRow.setVisible(editable);
	}

	@Override
	protected void onLoad() {
		Log.debug("EditPanel.onLoad()..");
		super.onLoad();
		if(btnCancel != null) {
			DeferredCommand.addCommand(new FocusCommand(btnCancel, true));
		}
	}

	@Override
	protected void onUnload() {
		Log.debug("EditPanel.onUnload()..");
		super.onUnload();
	}
}

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
import com.tabulaw.client.validate.IErrorHandler;
import com.tabulaw.common.msg.Msg;

/**
 * Wraps a panel holding editable content that fires edit events.
 * @param <T> edit content type
 * @param <P> field panel type
 * @author jpk
 */
public abstract class AbstractEditPanel<T, P extends AbstractFieldPanel<?>> extends Composite 
implements ClickHandler, IHasEditHandlers<T> {

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
	protected P fieldPanel;

	/**
	 * Ref to the optional message display which is gotten from the error handler
	 * when set.
	 */
	protected IMsgDisplay msgDisplay;

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

		if(saveText == null) saveText = DEFAULT_SAVE_TEXT;
		btnSave = new Button(saveText, this);
		btnSave.addStyleName(Styles.SAVE);
		pnlButtonRow.add(btnSave);

		if(deleteText == null) deleteText = DEFAULT_DELETE_TEXT;
		btnDelete = new Button(deleteText, this);
		btnDelete.addStyleName(Styles.DELETE);
		pnlButtonRow.add(btnDelete);

		if(cancelText == null) cancelText = DEFAULT_CANCEL_TEXT;
		btnCancel = new Button(cancelText, this);
		btnCancel.addStyleName(Styles.CANCEL);
		pnlButtonRow.add(btnCancel);

		if(resetText == null) resetText = DEFAULT_RESET_TEXT;
		btnReset = new Button(resetText, this);
		btnReset.addStyleName(Styles.RESET);
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
	public AbstractEditPanel(String saveText, String deleteText, String cancelText, String resetText, P fieldPanel) {
		this(saveText, deleteText, cancelText, resetText);
		setFieldPanel(fieldPanel);
	}

	protected final Panel getPortal() {
		return portal;
	}

	public final P getFieldPanel() {
		return fieldPanel;
	}

	public final void setFieldPanel(P fieldPanel) {
		if(fieldPanel == null) throw new IllegalArgumentException("A field panel must be specified.");
		if(this.fieldPanel != null) throw new IllegalStateException("Field panel already set.");
		this.fieldPanel = fieldPanel;
		portal.setWidget(fieldPanel);
	}

	/**
	 * Sets the error handler for field validation feedback and optionally adds
	 * the message display to this panel.
	 * @param errorHandler the error handler to set
	 * @param addMsgDisplay add the held msg display to this panel?
	 */
	public void setErrorHandler(ErrorHandlerDelegate errorHandler, boolean addMsgDisplay) {
		fieldPanel.setErrorHandler(errorHandler);
		msgDisplay = errorHandler.getMsgDisplay();
		if(addMsgDisplay && msgDisplay != null) {
			panel.insert(msgDisplay.getDisplayWidget(), 0);
		}
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
		final IErrorHandler errorHandler = root.getErrorHandler();
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
			fieldPanel.reset();
		}
		else if(sender == btnDelete) {
			//T editContent = getEditContent();
			//if(editContent != null) {
				EditEvent.fireDelete(this);
			//}
		}
		else if(sender == btnCancel) {
			EditEvent.fireCancel(this);
		}
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

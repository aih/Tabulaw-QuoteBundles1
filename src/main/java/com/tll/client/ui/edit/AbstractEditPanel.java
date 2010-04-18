/**
 * The Logic Lab
 * @author jpk Nov 3, 2007
 */
package com.tll.client.ui.edit;

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
import com.tll.client.ui.FocusCommand;
import com.tll.client.ui.field.AbstractFieldPanel;
import com.tll.client.ui.field.FieldGroup;
import com.tll.client.ui.field.IFieldWidget;
import com.tll.client.ui.msg.IMsgDisplay;
import com.tll.client.validate.Error;
import com.tll.client.validate.ErrorClassifier;
import com.tll.client.validate.ErrorDisplay;
import com.tll.client.validate.ErrorHandlerDelegate;
import com.tll.client.validate.IErrorHandler;
import com.tll.common.msg.Msg;

/**
 * Wraps a panel holding editable content that fires edit events.
 * @param <T> edit content type
 * @param <P> field panel type
 * @author jpk
 */
public abstract class AbstractEditPanel<T, P extends AbstractFieldPanel<?>> extends Composite implements ClickHandler, IHasEditHandlers<T> {

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

	/**
	 * The composite's target widget
	 */
	private final FlowPanel panel = new FlowPanel();

	private final SimplePanel portal = new SimplePanel();

	/**
	 * Contains the actual edit fields.
	 */
	protected final P fieldPanel;

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

	private Button btnReset, btnDelete, btnCancel;

	/**
	 * Constructor
	 * @param fieldPanel The required {@link AbstractFieldPanel}
	 * @param showCancelBtn Show the cancel button? Causes a cancel edit event
	 *        when clicked.
	 * @param showDeleteBtn Show the delete button? Causes a delete edit event
	 *        when clicked.
	 * @param showResetBtn Show the reset button?
	 */
	public AbstractEditPanel(P fieldPanel, boolean showCancelBtn, boolean showDeleteBtn, boolean showResetBtn) {
		super();
		if(fieldPanel == null) throw new IllegalArgumentException("A field panel must be specified.");
		this.fieldPanel = fieldPanel;

		portal.setStyleName(Styles.PORTAL);
		portal.setWidget(fieldPanel);

		pnlButtonRow.setStyleName(Styles.BTN_ROW);

		// default to add mode
		btnSave = new Button("Add", this);
		btnSave.addStyleName(Styles.SAVE);
		pnlButtonRow.add(btnSave);

		showDeleteButton(showDeleteBtn);

		showCancelButton(showCancelBtn);

		panel.add(portal);
		panel.add(pnlButtonRow);
		panel.setStyleName(Styles.ENTITY_EDIT);

		initWidget(panel);
	}
	
	protected final Panel getPortal() {
		return portal;
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

	public final void showResetButton(boolean show) {
		if(btnReset == null) {
			btnReset = new Button("Reset", this);
			btnReset.addStyleName(Styles.RESET);
			pnlButtonRow.add(btnReset);
		}
		btnReset.setVisible(show);
	}

	public final void showDeleteButton(boolean show) {
		if(btnDelete == null) {
			btnDelete = new Button("Delete", this);
			btnDelete.addStyleName(Styles.DELETE);
			pnlButtonRow.add(btnDelete);
		}
		btnDelete.setVisible(show);
	}

	public final void showCancelButton(boolean show) {
		if(btnCancel == null) {
			btnCancel = new Button("Cancel", this);
			btnCancel.addStyleName(Styles.CANCEL);
			pnlButtonRow.add(btnCancel);
		}
		btnCancel.setVisible(show);
	}

	@Override
	public final HandlerRegistration addEditHandler(IEditHandler<T> handler) {
		return addHandler(handler, EditEvent.TYPE);
	}

	/**
	 * Sets the edit mode to either add or update.
	 * @param isAdd
	 */
	public void setEditMode(boolean isAdd) {
		btnSave.setText(isAdd ? "Add" : "Update");
	}

	protected final boolean isAdd() {
		return "Add".equals(btnSave.getText());
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
			T editContent = getEditContent();
			if(editContent != null) {
				if(isAdd()) {
					EditEvent.fireAdd(this, editContent);
				}
				else {
					EditEvent.fireUpdate(this, editContent);
				}
			}
		}
		else if(sender == btnReset) {
			fieldPanel.reset();
		}
		else if(sender == btnDelete) {
			T editContent = getEditContent();
			if(editContent != null) {
				EditEvent.fireDelete(this, editContent);
			}
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

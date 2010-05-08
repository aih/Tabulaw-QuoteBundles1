/**
 * The Logic Lab
 * @author jpk
 * @since Mar 14, 2010
 */
package com.tabulaw.client.ui.edit;

import com.tabulaw.client.ui.field.AbstractFieldPanel;
import com.tabulaw.client.ui.field.FieldGroup;

/**
 * Edit panel whose edit content is a {@link FieldGroup}.
 * @author jpk
 */
public class FieldGroupEditPanel extends AbstractEditPanel<FieldGroup> {

	/**
	 * Constructor
	 */
	public FieldGroupEditPanel() {
		super();
	}

	/**
	 * Constructor
	 * @param saveText
	 * @param deleteText
	 * @param cancelText
	 * @param resetText
	 * @param fieldPanel
	 */
	public FieldGroupEditPanel(String saveText, String deleteText, String cancelText, String resetText,
			AbstractFieldPanel fieldPanel) {
		super(saveText, deleteText, cancelText, resetText, fieldPanel);
	}

	/**
	 * Constructor
	 * @param saveText
	 * @param deleteText
	 * @param cancelText
	 * @param resetText
	 */
	public FieldGroupEditPanel(String saveText, String deleteText, String cancelText, String resetText) {
		super(saveText, deleteText, cancelText, resetText);
	}

	@Override
	protected FieldGroup getEditContent() {
		return fieldPanel.getFieldGroup();
	}
}

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
public class FieldGroupEditPanel extends AbstractEditPanel<FieldGroup, AbstractFieldPanel<?>> {

	/**
	 * Constructor
	 * @param fieldPanel
	 * @param showCancelBtn
	 * @param showDeleteBtn
	 * @param showResetBtn
	 */
	public FieldGroupEditPanel(AbstractFieldPanel<?> fieldPanel, boolean showCancelBtn, boolean showDeleteBtn,
			boolean showResetBtn) {
		super(fieldPanel, showCancelBtn, showDeleteBtn, showResetBtn);
	}

	@Override
	protected FieldGroup getEditContent() {
		return fieldPanel.getFieldGroup();
	}
}

package com.tabulaw.client.ui.field;

import com.google.gwt.event.dom.client.HasBlurHandlers;
import com.google.gwt.event.dom.client.HasFocusHandlers;
import com.google.gwt.event.dom.client.HasKeyDownHandlers;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HasValue;
import com.tabulaw.client.ui.IHasHoverHandlers;

/**
 * Consolidation of supported behavior for the editable widget for a field widget.
 * @author jpk
 * @param <T> the value type
 */
public interface IEditable<T> extends 
Focusable, HasValue<T>, HasBlurHandlers, HasFocusHandlers, IHasHoverHandlers, HasKeyDownHandlers {
}
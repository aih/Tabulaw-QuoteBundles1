package com.tll.client.ui.field;

import com.google.gwt.event.dom.client.HasBlurHandlers;
import com.google.gwt.event.dom.client.HasFocusHandlers;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HasValue;
import com.tll.client.ui.IHasHoverHandlers;

/**
 * IEditable - Marker type interface to aggregate focusability, "hoverability"
 * and value providing ability.
 * @author jpk
 * @param <T> the value type
 */
interface IEditable<T> extends Focusable, HasValue<T>, HasBlurHandlers, HasFocusHandlers, IHasHoverHandlers {
}
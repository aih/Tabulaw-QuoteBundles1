/**
 * The Logic Lab
 * @author jpk
 * Mar 4, 2009
 */
package com.tabulaw.client.ui.field;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Widget;
import com.tabulaw.client.ui.IHasHoverHandlers;
import com.tabulaw.client.ui.IHoverHandler;
import com.tabulaw.client.ui.IWidgetRef;
import com.tabulaw.client.ui.field.IFieldWidget.Styles;
import com.tabulaw.client.ui.msg.MsgPopupRegistry;
import com.tabulaw.client.validate.Error;
import com.tabulaw.client.validate.ErrorClassifier;
import com.tabulaw.client.validate.PopupValidationFeedback;

/**
 * FieldErrorHandler - Localized error handling for field widgets based on popup
 * messages to appear on mouse hover browser events.
 * @author jpk
 */
public class FieldErrorHandler extends PopupValidationFeedback implements IHoverHandler {

	/**
	 * MouseRegs
	 * @author jpk
	 */
	static final class MouseRegs {

		HandlerRegistration rMseOut, rMsgOvr;
	}

	/**
	 * The cache of the current invalid fields necessary to add/remove
	 * hoverability.
	 */
	private final Map<IFieldWidget<?>, MouseRegs> invalids = new HashMap<IFieldWidget<?>, MouseRegs>();

	/**
	 * Constructor
	 * @param mregistry
	 */
	public FieldErrorHandler(MsgPopupRegistry mregistry) {
		super(mregistry);
	}

	@Override
	protected void doHandleError(Error error) {
		super.doHandleError(error);
		final Widget target = error.getTarget() == null ? null : error.getTarget().getWidget();
		if(target != null) {
			// handle styling
			target.removeStyleName(Styles.DIRTY);
			target.addStyleName(Styles.INVALID);

			// track popup hovering
			MouseRegs regs = invalids.get(target);
			if(regs == null) {
				regs = new MouseRegs();
				invalids.put((IFieldWidget<?>) target, regs);
			}
			trackHover((IFieldWidget<?>) target, regs, true);

			// turn off incremental validation when the error originates from the
			// server
			if(error.getClassifier() != null && error.getClassifier().isServer()) {
				((IFieldWidget<?>) target).validateIncrementally(false);
			}
		}
	}

	@Override
	protected void doResolveError(IWidgetRef source, ErrorClassifier classifier) {
		super.doResolveError(source, classifier);
		if(source instanceof IFieldWidget<?>) {
			// handle styling
			source.getWidget().removeStyleName(Styles.INVALID);

			// un-track popup hovering
			final MouseRegs regs = invalids.remove(source);
			if(regs != null) {
				trackHover((IFieldWidget<?>) source, regs, false);
			}

			// reset incremental validation if server error
			if(classifier != null && classifier.isServer()) {
				((IFieldWidget<?>) source).validateIncrementally(true);
			}
		}
	}

	@Override
	public void onMouseOver(MouseOverEvent event) {
		final IFieldWidget<?> field = resolveField(event);
		if(field != null) {
			mregistry.getOperator(field.getWidget(), false).showMsgs(true);
		}
	}

	@Override
	public void clear(ErrorClassifier classifier) {
		//super.clear(classifier);
		Set<IFieldWidget<?>> set = new HashSet<IFieldWidget<?>>(invalids.keySet());
		for(final IFieldWidget<?> fw : set) {
			doResolveError(fw, classifier);
		}
	}

	@Override
	public void clear() {
		clear(null);
	}

	@Override
	public void onMouseOut(MouseOutEvent event) {
		final IFieldWidget<?> field = resolveField(event);
		if(field != null) {
			mregistry.getOperator(field.getWidget(), false).showMsgs(false);
		}
	}

	private IFieldWidget<?> resolveField(MouseEvent<?> event) {
		final Object src = event.getSource();
		for(final IFieldWidget<?> fw : invalids.keySet()) {
			if(src == fw || src == fw.getFieldLabel()) return fw;
		}
		return null;
	}

	/**
	 * Adds or removes hoverability to the given field.
	 * @param field the subject field
	 * @param mouseRegs the associated mouse registrations for the given field
	 * @param track track (add) or don't track (remove)?
	 */
	private void trackHover(IFieldWidget<?> field, MouseRegs mouseRegs, boolean track) {
		// resolve the hoverable
		if(track) {
			final IHasHoverHandlers hoverable = field.getFieldLabel() == null ? field.getEditable() : field.getFieldLabel();
			if(mouseRegs.rMseOut == null) mouseRegs.rMseOut = hoverable.addMouseOutHandler(this);
			if(mouseRegs.rMsgOvr == null) mouseRegs.rMsgOvr = hoverable.addMouseOverHandler(this);
		}
		else {
			if(mouseRegs.rMseOut != null) {
				mouseRegs.rMseOut.removeHandler();
				mouseRegs.rMseOut = null;
			}
			if(mouseRegs.rMsgOvr != null) {
				mouseRegs.rMsgOvr.removeHandler();
				mouseRegs.rMsgOvr = null;
			}
		}
	}
}

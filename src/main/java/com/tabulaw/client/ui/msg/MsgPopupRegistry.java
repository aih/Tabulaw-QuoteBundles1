/**
 * The Logic Lab
 * @author jpk
 * Feb 18, 2009
 */
package com.tabulaw.client.ui.msg;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Widget;

/**
 * MsgPopupRegistry - Cache of message popups each referencing a widget by which
 * it is positioned.
 * <p>
 * Since this type of registry caches these popups, thier life-cycle is
 * "extended" beyond hiding the popup. As such, <b>the registry client is
 * responsible for ultimately clearing them in this registry.</b>
 * @author jpk
 */
public final class MsgPopupRegistry {

	/**
	 * The managed cache of popups for this registry keyed by ref widget.
	 */
	private final Map<Widget, MsgPopup> cache = new HashMap<Widget, MsgPopup>();

	/**
	 * Provides a flyweight type operator against <em>existing</em> popups for the
	 * given widget.
	 * @param w The targeted widget
	 * @param drillDown if <code>true</code>, all dom-wise nested message popups
	 *        will be bound to the returned operator. if <code>false</code>, only
	 *        the message popup for the given widget is bound.
	 * @return a flyweight message operator operating over all collected popups.
	 */
	public IMsgOperator getOperator(Widget w, boolean drillDown) {
		return new MsgOperatorFlyweight(drill(w, drillDown));
	}

	/**
	 * Provides a msg popup operator for the given {@link Widget} which doesn't
	 * consider dom-wise nested popups. If no popup exists for this {@link Widget}
	 * , one is created.
	 * @param w the target widget
	 * @return the never-<code>null</code> popup operator.
	 */
	public IMsgOperator getOrCreateOperator(Widget w) {
		return getMsgPopup(w);
	}

	/**
	 * Provides a flyweight type operator against <em>all existing</em> popups
	 * held in this registry.
	 * @return a flyweight message operator operating over all collected popups.
	 */
	public IMsgOperator getAllOperator() {
		return new MsgOperatorFlyweight(cache.values());
	}

	/**
	 * Life-cycle provision that clears out all cached message popups from this
	 * registry.
	 */
	public void clear() {
		cache.clear();
	}

	/**
	 * Provides a never-<code>null</code> set of message popups whose ref widget
	 * either matches the given widget or is a dom-wise child of the given widget.
	 * @param w
	 * @param drillDown if <code>true</code>, all dom-wise nested message popups
	 *        will be bound to the returned operator. if <code>false</code>, only
	 *        the message popup for the given widget is bound.
	 * @return Never-<code>null</code> set of message popups which may be empty
	 */
	private Set<MsgPopup> drill(Widget w, boolean drillDown) {
		final HashSet<MsgPopup> set = new HashSet<MsgPopup>();
		for(final MsgPopup mp : cache.values()) {
			if(mp.getRefWidget() == w || (drillDown && (DOM.isOrHasChild(w.getElement(), mp.getRefWidget().getElement())))) {
				set.add(mp);
			}
		}
		return set;
	}

	/**
	 * Searches the held cache of popups for the one that targets the given
	 * widget. If one exists, it is returned otherwise one is created, added the
	 * cache then returned.
	 * @param w The target widget
	 * @return The never <code>null</code> bound message popup.
	 */
	private MsgPopup getMsgPopup(Widget w) {
		MsgPopup mp = cache.get(w);
		if(mp == null) {
			mp = new MsgPopup(w);
			cache.put(w, mp);
		}
		return mp;
	}
}

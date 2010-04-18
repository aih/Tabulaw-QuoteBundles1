package com.tll.client.ui.msg;

import java.util.Collection;
import java.util.Iterator;

import com.google.gwt.event.dom.client.ScrollEvent;
import com.tll.client.ui.DragEvent;
import com.tll.client.ui.Position;
import com.tll.common.msg.Msg;

/**
 * MsgOperatorFlyweight - A flyweight for a collection of {@link IMsgOperator}s.
 * The flyweight behaves as if it were a single message operator.
 * @author jpk
 */
final class MsgOperatorFlyweight implements IMsgOperator, Iterable<IMsgOperator> {

	/**
	 * The collection of bound operators.
	 */
	private final Collection<? extends IMsgOperator> operators;

	/**
	 * Constructor
	 * @param operators
	 */
	public MsgOperatorFlyweight(Collection<? extends IMsgOperator> operators) {
		super();
		if(operators == null) throw new IllegalArgumentException();
		this.operators = operators;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Iterator<IMsgOperator> iterator() {
		return (Iterator<IMsgOperator>) operators.iterator();
	}

	@Override
	public void addMsg(Msg msg, Integer classifier) {
		for(final IMsgOperator o : operators) {
			o.addMsg(msg, classifier);
		}
	}

	@Override
	public void addMsgs(Iterable<Msg> msgs, Integer classifier) {
		for(final IMsgOperator o : operators) {
			o.addMsgs(msgs, classifier);
		}
	}

	@Override
	public void removeMsg(Msg msg) {
		for(final IMsgOperator o : operators) {
			o.removeMsg(msg);
		}
	}

	@Override
	public void removeMsgs(int classifier) {
		for(final IMsgOperator o : operators) {
			o.removeMsgs(classifier);
		}
	}

	@Override
	public void removeMsgs(Iterable<Msg> msgs) {
		for(final IMsgOperator o : operators) {
			o.removeMsgs(msgs);
		}
	}

	@Override
	public void removeUnclassifiedMsgs() {
		for(final IMsgOperator o : operators) {
			o.removeUnclassifiedMsgs();
		}
	}

	@Override
	public void clearMsgs() {
		for(final IMsgOperator o : operators) {
			o.clearMsgs();
		}
	}

	@Override
	public void showMsgs(Position position, int milliDuration, boolean showMsgLevelImages) {
		for(final IMsgOperator o : operators) {
			o.showMsgs(position, milliDuration, showMsgLevelImages);
		}
	}

	/**
	 * We return <code>true</code> when at least one is showing.
	 */
	@Override
	public boolean isShowing() {
		for(final IMsgOperator o : operators) {
			if(o.isShowing()) return true;
		}
		return false;
	}

	@Override
	public void showMsgs(boolean show) {
		for(final IMsgOperator o : operators) {
			o.showMsgs(show);
		}
	}

	@Override
	public void onDrag(DragEvent event) {
		switch(event.dragMode) {
			case DRAGGING:
				break;
			case START:
				// tell the contained msg popups we are dragging
				for(final IMsgOperator o : operators) {
					o.showMsgs(false);
				}
				break;
			case END:
				for(final IMsgOperator o : operators) {
					o.showMsgs(true);
				}
				break;
		}
	}

	@Override
	public void onScroll(ScrollEvent event) {
		// hide all showing msg popups
		for(final IMsgOperator o : operators) {
			o.showMsgs(false);
		}
	}

}
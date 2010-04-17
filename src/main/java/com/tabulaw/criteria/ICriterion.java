package com.tabulaw.criteria;

import java.io.Serializable;

/**
 * ICriterion - Common criterion element methods.
 * @author jpk
 */
public interface ICriterion extends Serializable {

	/**
	 * Returns true if this criterion is set. By default, this method checks to
	 * see if a value is set. Subclasses may override this to provide more complex
	 * logic if necessary
	 * @return <code>true</code> if criterion is defined.
	 */
	boolean isSet();

	/**
	 * @return <code>true</code> if this {@link ICriterion} is a
	 *         {@link CriterionGroup}.
	 */
	boolean isGroup();

	/**
	 * Clears the value of this criterion element as well as any and all nested
	 * values.
	 */
	void clear();
}

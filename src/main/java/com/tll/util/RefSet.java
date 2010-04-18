/**
 * The Logic Lab
 * @author jpk
 * @since Sep 23, 2009
 */
package com.tll.util;

import java.util.HashSet;


/**
 * RefSet
 * @author jpk
 * @param <E> the element type
 */
@SuppressWarnings("serial")
public class RefSet<E> extends HashSet<E> {

	public boolean exists(E arg) {
		for(final E e : this) {
			if(e == arg) return true;
		}
		return false;
	}
}

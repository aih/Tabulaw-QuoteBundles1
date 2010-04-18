/**
 * The Logic Lab
 * @author jpk
 * @since Sep 23, 2009
 */
package com.tll.util;


/**
 * Binding - Simple encapsulation of two instances of prescribed types.
 * @author jpk
 * @param <S> the "source" type
 * @param <T> the "target" type
 */
public class Binding<S, T> {

	public S src;
	public T tgt;

	/**
	 * Constructor
	 * @param src the "source" ref
	 * @param tgt the "target" ref
	 */
	public Binding(S src, T tgt) {
		super();
		this.src = src;
		this.tgt = tgt;
	}

	@Override
	public String toString() {
		return "src: " + src + ", tgt: " + tgt;
	}
}

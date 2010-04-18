/**
 * The Logic Lab
 * @author jpk
 * @since Sep 23, 2009
 */
package com.tll.util;

import java.util.HashSet;
import java.util.Iterator;

/**
 * BindingRefSet
 * @author jpk
 * @param <S> the "source" type
 * @param <T> the "target" type
 */
@SuppressWarnings("serial")
public class BindingRefSet<S, T> extends HashSet<Binding<S, T>> {

	public T findTarget(S arg) {
		for(final Binding<S, T> bndg : this) {
			if(bndg.src == arg) return bndg.tgt;
		}
		return null;
	}

	public S findSource(T arg) {
		for(final Binding<S, T> bndg : this) {
			if(bndg.tgt == arg) return bndg.src;
		}
		return null;
	}

	public Binding<S, T> findBindingBySource(S arg) {
		for(final Binding<S, T> bndg : this) {
			if(bndg.src == arg) return bndg;
		}
		return null;
	}

	public Binding<S, T> findBindingByTarget(T arg) {
		for(final Binding<S, T> bndg : this) {
			if(bndg.tgt == arg) return bndg;
		}
		return null;
	}

	public Iterator<S> sourceIterator() {
		final HashSet<S> set = new HashSet<S>(this.size());
		for(final Binding<S, T> b : this) {
			set.add(b.src);
		}
		return set.iterator();
	}

	public Iterator<T> targetIterator() {
		final HashSet<T> set = new HashSet<T>(this.size());
		for(final Binding<S, T> b : this) {
			set.add(b.tgt);
		}
		return set.iterator();
	}
}

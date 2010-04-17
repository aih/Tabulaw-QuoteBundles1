/**
 * The Logic Lab
 * @author jpk
 * Mar 7, 2008
 */
package com.tabulaw.dao;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import com.tabulaw.common.model.IEntity;
import com.tll.IPropertyValueProvider;

/**
 * SearchResult - Generic container for a single search result. Wraps either a
 * single entity or a single scalar element.
 * @author jpk
 */
public final class SearchResult implements IPropertyValueProvider {

	/**
	 * The raw search result element.
	 */
	private final Object element;

	/**
	 * Constructor
	 * @param element
	 */
	public SearchResult(final Object element) {
		super();
		if(element instanceof IEntity == false /*&& element instanceof IScalar == false*/)
			throw new IllegalArgumentException("Invalid search result element type");
		this.element = element;
	}

	/**
	 * @return the raw search result element.
	 */
	public Object getElement() {
		return element;
	}

	/**
	 * @return The type of this search result. May be <code>null</code>.
	 */
	Class<?> getRefType() {
		if(element instanceof IEntity) {
			// entity
			return ((IEntity) element).getClass();
		}
		// scalar
		//return ((IScalar) element).getRefType();
		throw new IllegalStateException();
	}

	@Override
	public Object getPropertyValue(String propertyPath) {
		if(element == null) return null;
		final BeanWrapper bw = new BeanWrapperImpl(element);
		if(element instanceof IEntity) {
			return bw.getPropertyValue(propertyPath);
		}
		/*
		else if(element instanceof IScalar) {
			return bw.getPropertyValue("tupleMap[" + propertyPath + "]");
		}
		*/
		throw new IllegalStateException("Unhandled element type:" + element.getClass());
	}
}

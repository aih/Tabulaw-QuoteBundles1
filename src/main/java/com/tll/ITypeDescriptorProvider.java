/**
 * 
 */
package com.tll;

/**
 * ITypeDescriptorProvider - Indicates the ability to provide a UI presentable
 * description of an object's type.
 * @author jpk
 */
public interface ITypeDescriptorProvider {

	/**
	 * @return UI presentable text describing the object type.
	 */
	String typeDesc();
}

/**
 * 
 */
package com.tll;

/**
 * IDescriptorProvider - Indicates the ability to provide a descriptor usually
 * describing on object's state somehow.
 * @author jpk
 */
public interface IDescriptorProvider {

	/**
	 * @return UI presentable descriptor.
	 */
	String descriptor();
}

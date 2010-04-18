/**
 * The Logic Lab
 * @author jpk
 * Jan 7, 2009
 */
package com.tll.util;

/**
 * ObjectUtil - {@link Object} utility methods.
 * @author jpk
 */
public final class ObjectUtil {

	/**
	 * Constructor
	 */
	private ObjectUtil() {
		super();
	}

	/**
	 * <p>
	 * Compares two objects for equality, where either one or both objects may be
	 * <code>null</code>.
	 * </p>
	 * 
	 * <pre>
	 * ObjectUtils.equals(null, null)                  = true
	 * ObjectUtils.equals(null, &quot;&quot;)                    = false
	 * ObjectUtils.equals(&quot;&quot;, null)                    = false
	 * ObjectUtils.equals(&quot;&quot;, &quot;&quot;)                      = true
	 * ObjectUtils.equals(Boolean.TRUE, null)          = false
	 * ObjectUtils.equals(Boolean.TRUE, &quot;true&quot;)        = false
	 * ObjectUtils.equals(Boolean.TRUE, Boolean.TRUE)  = true
	 * ObjectUtils.equals(Boolean.TRUE, Boolean.FALSE) = false
	 * </pre>
	 * @param object1 the first object, may be <code>null</code>
	 * @param object2 the second object, may be <code>null</code>
	 * @return <code>true</code> if the values of both objects are the same
	 */
	public static boolean equals(Object object1, Object object2) {
		if(object1 == object2) {
			return true;
		}
		if((object1 == null) || (object2 == null)) {
			return false;
		}
		return object1.equals(object2);
	}
}

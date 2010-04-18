package com.tll.util;

import java.util.LinkedHashMap;
import java.util.Map;

import com.tll.INameValueProvider;

/**
 * Utility methods for enum classes.
 * @author jpk
 */
public final class EnumUtil {

	/**
	 * Attempts to provide the enum class given an enum class name.
	 * @param <E>
	 * @param enumClassName
	 * @return Enum class
	 */
	@SuppressWarnings("unchecked")
	public static <E extends Enum<?>> Class<E> enumClassFromString(String enumClassName) {
		try {
			return (Class<E>) Class.forName(enumClassName);
		}
		catch(final ClassNotFoundException e) {
			return null;
		}
	}

	/**
	 * Overrides the default bahavior of Enum.valueOf() to account for enums that
	 * implement the {@link INameValueProvider} interface.
	 * @param <E> enum type
	 * @param enumType the enum class.
	 * @param text the string value of the enum constant.
	 * @return the found Enum constant of the given enum class.
	 * @throws IllegalArgumentException When the given text does not correspond to
	 *         any enum element for the given enumType.
	 */
	public static <E extends Enum<?>> E fromString(Class<E> enumType, String text) throws IllegalArgumentException {

		for(final E e : enumType.getEnumConstants()) {
			if(e instanceof INameValueProvider<?> && ((INameValueProvider<?>) e).getValue().equals(text)) {
				return e;
			}
			else if(e.toString().equals(text)) {
				return e;
			}
		}
		throw new IllegalArgumentException("Invalid text for enum of type '" + enumType.getName() + ": '" + text + "'.");

	}

	/**
	 * Considers enums that implement the {@link INameValueProvider} interface
	 * otherwise Enum#toString method is employed.
	 * @param enm the enum for which to obtain a string
	 * @return a string representation for the given enum.
	 */
	public static String toString(Enum<?> enm) {
		if(enm instanceof INameValueProvider<?>) {
			final Object ov = ((INameValueProvider<?>) enm).getValue();
			return ov == null ? null : ov.toString();
		}
		return enm.toString();
	}

	/**
	 * Considers enums that implement the {@link INameValueProvider} interface
	 * otherwise Enum#toString method is employed.
	 * @param enm the enum for which to obtain a string
	 * @return a string representation for the given enum.
	 */
	public static String name(Enum<?> enm) {
		if(enm instanceof INameValueProvider<?>) {
			return ((INameValueProvider<?>) enm).getName();
		}
		return enm.toString();
	}

	/**
	 * Converts an enum to a Map factorting in the possibility the given enum type
	 * may be a {@link INameValueProvider} instance. <br>
	 * NOTE: The enum "name" is the map key.
	 * @param <E> enum type
	 * @param enumType
	 * @return a name/value Map
	 */
	@SuppressWarnings("unchecked")
	public static <E extends Enum<?>> Map<String, String> toMap(Class<E> enumType) {
		final Map<String, String> map = new LinkedHashMap<String, String>();
		for(final Object e : enumType.getEnumConstants()) {
			if(e instanceof INameValueProvider) {
				final INameValueProvider<String> senum = (INameValueProvider<String>) e;
				map.put(senum.getName(), senum.getValue());
			}
			else {
				final String s = e.toString();
				map.put(s, s);
			}
		}
		return map;
	}

	/**
	 * Convenience method that returns the enum element corresponding to the given
	 * ordinal and enum type.
	 * @param <E> enum type
	 * @param ordinal The enum ordinal.
	 * @param enumType The enum class.
	 * @return {@link Enum}
	 */
	public static <E extends Enum<?>> E fromOrdinal(Class<E> enumType, int ordinal) {
		for(final E enm : enumType.getEnumConstants()) {
			if(enm.ordinal() == ordinal) return enm;
		}
		throw new IllegalArgumentException("Invalid ordinal: " + ordinal + " for enum type: " + enumType.getSimpleName());
	}

	/**
	 * 
	 */
	private EnumUtil() {
	}

}

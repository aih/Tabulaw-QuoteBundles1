/**
 * The Logic Lab
 * @author jpk
 * Feb 25, 2009
 */
package com.tabulaw.client.convert;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.tabulaw.INameValueProvider;

/**
 * EnumToDataMapConverter
 * @param <E> the enum type
 * @author jpk
 */
public class EnumToDataMapConverter<E extends Enum<E>> implements IConverter<Map<E, String>, Class<E>> {

	@SuppressWarnings("rawtypes")
	public static final EnumToDataMapConverter INSTANCE = new EnumToDataMapConverter();

	/**
	 * Constructor
	 */
	private EnumToDataMapConverter() {
		super();
	}

	@Override
	public Map<E, String> convert(Class<E> enmType) throws IllegalArgumentException {
		final HashMap<E, String> map = new LinkedHashMap<E, String>();
		for(final E enm : enmType.getEnumConstants()) {
			if(enm instanceof INameValueProvider<?>) {
				final INameValueProvider<?> nvp = (INameValueProvider<?>) enm;
				map.put(enm, nvp.getName());
			}
			else {
				map.put(enm, enm.name());
			}
		}
		return map;
	}

}

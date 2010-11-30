package com.tabulaw.cassandra.om.mappings;

import org.apache.commons.lang.StringUtils;


public class BooleanTypeMapping extends AbstractStringBasedTypeMapping<Boolean> {

	@Override
	public String asString(Boolean value) {
		return value ? "t" : "";
	}

	@Override
	public Boolean fromString(String string) {
		return "t".equals(StringUtils.lowerCase(string));
	}
}

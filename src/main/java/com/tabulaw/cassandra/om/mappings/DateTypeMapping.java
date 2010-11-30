package com.tabulaw.cassandra.om.mappings;

import java.util.Date;


public class DateTypeMapping extends AbstractStringBasedTypeMapping<Date> {

	@Override
	public String asString(Date value) {
		return "" + value.getTime();
	}

	@Override
	public Date fromString(String string) {
		return new Date(Long.valueOf( string ));
	}
}

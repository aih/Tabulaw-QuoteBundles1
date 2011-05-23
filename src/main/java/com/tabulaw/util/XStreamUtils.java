package com.tabulaw.util;

import com.tabulaw.model.User;
import com.thoughtworks.xstream.XStream;

public class XStreamUtils {
	private static XStream xs;
	static {
        xs = new XStream();
        xs.alias("role", User.Role.class);
		
	}
    public static String toXML(Object obj)
    {
        return xs.toXML(obj);
    }
    public static Object fromXML(String xml){
    	return xs.fromXML(xml);
    }

}

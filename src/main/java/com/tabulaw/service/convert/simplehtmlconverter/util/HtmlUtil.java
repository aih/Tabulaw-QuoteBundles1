package com.tabulaw.service.convert.simplehtmlconverter.util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlUtil {
	public static String decodeHTMLEntities(String text) {
		Map<String, String> entities = new HashMap<String, String>();
		entities.put("&quot;","\"");
		entities.put("&apos;","'");
		entities.put("&amp;","&");
		entities.put("&lt;","<");
		entities.put("&gt;",">");
		return replace(text,entities);
	}
	
	public static String replace(final String template, final Map<String, String> map) {
		final StringBuilder list = new StringBuilder("(");
		for (final String key : map.keySet()) {
			list.append(key);
			list.append("|");
		}
		list.append("[^\\s\\S])");
		Pattern pattern = Pattern.compile(list.toString());
		Matcher matcher = pattern.matcher(template);
		final StringBuffer stringBuffer = new StringBuffer();
		while (matcher.find()) {
			final String string = matcher.group(1);
			matcher.appendReplacement(stringBuffer, map.get(string));
		}
		matcher.appendTail(stringBuffer);
		return stringBuffer.toString();
	}
	

}

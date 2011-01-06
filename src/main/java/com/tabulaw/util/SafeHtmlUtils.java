package com.tabulaw.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import org.apache.commons.io.IOUtils;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.SimpleHtmlSerializer;
import org.htmlcleaner.TagNode;

public class SafeHtmlUtils {
	private static String PRUNED_TAGS = "script,object,applet,form,embed,style,link,meta,iframe";
	private static String ON_PREFIX = "on";
	private static String[] PRUNED_ATTRS = new String[]{"class", "style", "href", "src", "background"};
	
	public static void sanitizeHtml(InputStream input, OutputStream output) throws IOException {
		String src = IOUtils.toString(input);
		String dst = sanitizeHtml(src);
		IOUtils.write(dst, output);
	}

	public static String sanitizeHtml(String src) throws IOException{
		final CleanerProperties props = new CleanerProperties();
		props.setPruneTags(PRUNED_TAGS);
		HtmlCleaner cleaner = new HtmlCleaner(props);

		
		TagNode root = cleaner.clean(src);
		
		cleanChilds(root);

		final SimpleHtmlSerializer htmlSerializer = new SimpleHtmlSerializer(props);
		
		return htmlSerializer.getAsString(root, "utf-8");
	}
	
	private static void cleanChilds(TagNode node) {
		for(Object child : node.getChildren()) {
			if (child instanceof TagNode) {
				TagNode childTag=(TagNode) child;
				//remove pruned attributes 
				for (String attrName : PRUNED_ATTRS) {
					if (childTag.hasAttribute(attrName)) {
						childTag.removeAttribute(attrName);
					}
				}
				//remove on* attributes 
				for(String attrName : new ArrayList<String>(childTag.getAttributes().keySet())) {
					if (attrName.startsWith(ON_PREFIX)) {
						childTag.removeAttribute(attrName);
					}
				}
				
				cleanChilds(childTag);
			}
		}
	}

}

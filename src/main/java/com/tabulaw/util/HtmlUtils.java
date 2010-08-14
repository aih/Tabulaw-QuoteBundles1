package com.tabulaw.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.htmlcleaner.ContentToken;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

public class HtmlUtils {
	
	private static final Set<String> INLINE_ELEMENTS = new HashSet<String>(Arrays.asList(
		    "a", "abbr", "acronym", "b", "basefont", "bdo", "big", "cite", 
		    "code", "dfn", "em", "font", "i", "img", "label", "q", "s", 
		    "small", "span", "strike", "strong", "sub", "sup", "tt", "u"
	)); 
	
	public static boolean isInlineElement(String element) {
		return INLINE_ELEMENTS.contains(StringUtils.lowerCase(element));
	}	
	
	/**
	 * Finds the quote given in plain text format in the html.<br><br>
	 * 
	 * Returns start position of quote and end position of quote <br>
	 * Position in html presented as: <br>
	 * 	&nbsp;&nbsp; path to the html element which contains the text<br> 
	 *  &nbsp;&nbsp; offset in the html element <br><br>
	 *  
	 *  For example: quote = "Just a test" and <br>
	 *  			 html = "&lt;html&gt;&lt;body&gt;&lt;center&gt;&lt;p&gt;text text&lt;i&gt;text Jus&lt;b&gtt&lt;/b&gt&lt;/i&gt;&lt;b&gt; a&lt;/b&gt; test text&lt;/p&gt; text&lt;/center&gt;&lt;/body&gt;&lt;/html&gt;"
	 *  <br><br>
	 *  will return:<br>
	 *  			start position: [0, 0, 1, 0] (elements: &lt;center&gt;, &lt;p&gt;, &lt;i&gt; (the second element in p), text element)<br> 
	 *  			start offset: 5<br>
	 *  			end position: [0, 0, 3] (elemets: &lt;center&gt;, &lt;p&gt;, text element (fourth element in p))<br> 
	 *  			end offset: 5<br>
	 * 
	 * 
	 * @param quote
	 * @param html
	 * @return
	 * @throws IOException
	 */
	public static QuotePosition findQuoteInHtml(String quote, String html) throws IOException {
		HtmlCleaner cleaner = new HtmlCleaner();
		TagNode root = cleaner.clean(html);
		quote = StringEscapeUtils.unescapeHtml(quote);
		String[] words = quote.trim().split("\\s+");
		TagNode body = root.getElementsByName("body", true)[0];
		
		StringBuilder bodyText = new StringBuilder( 
			StringEscapeUtils.unescapeHtml(body.getText().toString()));
		int index = 0;
		while ((index = bodyText.indexOf("&nbsp", index)) != -1) {
			int length = index + 5 < bodyText.length() && bodyText.charAt(index + 5) == ';' ? 6 : 5;
			bodyText.replace(index, index + length, " ");
			index += 1;
		}
		
		// find the quote in the html without tags
		// the quote in the html will be represented as
		// 		* number of start word
		//      * number of last word 
		int startIndex = 0;
		int startWordNumber = -1;
		int lastWordPosition = 0;
		String lastWord = words[words.length - 1];
		// find the first word of the quote
		while ((startIndex = bodyText.indexOf(words[0], startIndex)) != -1) {
			if (startIndex + words[0].length() < bodyText.length() &&
					! Character.isWhitespace(bodyText.charAt(startIndex + words[0].length()))) {
				startIndex += words[0].length();
				continue;
			}
			startWordNumber++;
			// checks is this start word of the whole quote
			lastWordPosition = checkWordSequence(bodyText, startIndex, words);
			if (lastWordPosition != -1) {
				break;
			}
			startIndex += words[0].length();
		}
		// the quote doesn't exist in the html
		if (startIndex == -1) {
			startWordNumber = -1;
			return null;
		}
		// find number of the last word of the quote
		int lastWordNumber = 0;
		startIndex = 0;
		while ((startIndex = bodyText.indexOf(lastWord, startIndex)) != lastWordPosition) {
			lastWordNumber++;				
			startIndex += lastWord.length();
		}
			
		LevelsList startLevels = HtmlWordsFinder.findWordInTag(body, words[0], startWordNumber, false);
		LevelsList endLevels = HtmlWordsFinder.findWordInTag(body, lastWord, lastWordNumber, true);
		return new QuotePosition(
				startLevels.getPosition(), 
				startLevels.getOffset(), 
				endLevels.getPosition(), 
				endLevels.getOffset()
		);		
	}
	
	/**
	 * Checks does @content contain @words sequence after @startIndex    
	 * 
	 * @param content
	 * @param startIndex
	 * @param words
	 * @return position after the quote or -1 if doesn't quote
	 */
	private static int checkWordSequence(StringBuilder content, int startIndex, String[] words) {
		int nextIndex = startIndex;
		String lastWord = words[words.length - 1];
		for (int i = 0; i < words.length - 1; i++) {
			String word = words[i];
			if (nextIndex + word.length() > content.length()) {
				return -1;
			}
			String htmlWord = content.substring(nextIndex, nextIndex + word.length());
			if (! word.equals(htmlWord)) {
				return -1;
			}				
			nextIndex += word.length();
			while (nextIndex < content.length() &&
					Character.isWhitespace(content.charAt(nextIndex))) {
				nextIndex++;
			}
		}
		if (content.indexOf(lastWord, nextIndex) == nextIndex) {
			return nextIndex;
		}
		return -1;
		
	}
	
	public static class QuotePosition {
		public int[] startPosition;
		public int startOffset;
		public int[] endPosition;
		public int endOffset;
		
		public QuotePosition(int[] startPosition, int startOffset,
				int[] endPosition, int endOffset) {
			super();
			this.startPosition = startPosition;
			this.startOffset = startOffset;
			this.endPosition = endPosition;
			this.endOffset = endOffset;
		}				
	}
	
	private static class HtmlWordsFinder {
		private String word;
		private LevelsList levels;
		private int requestWordNumber;
		private Prefix prefix;
		private boolean positionOfEnd;
		
		public static LevelsList findWordInTag(TagNode startTag, String word, 
				int wordNumber, boolean positionOfEnd) {
			HtmlWordsFinder finder = new HtmlWordsFinder();
			finder.word = word;
			finder.requestWordNumber = wordNumber;
			finder.prefix = new Prefix();
			finder.levels = new LevelsList();
			finder.positionOfEnd = positionOfEnd;
			
			finder.findWordInTag(startTag, 0);
			return finder.levels;
		}
		
		private boolean findWordStartPosition(String content, final int level) {
			int index = 0;
			while ((index = content.indexOf(word, index)) != -1) {
				// if the word is placed in the end of the text element
				// we should save it as prefix for next element
				// because it may not be our word 
				index += word.length();
				if (index >= content.length()) {
					break;
				}
				if (Character.isWhitespace(content.charAt(index))) {
					requestWordNumber--;
					if (requestWordNumber == -1) {
						// if the needed word starts in prefix return prefix position
						// else return position in current tag
						if (index > prefix.getPrefix().length()) {
							levels.set(level + 1, index - word.length());
						} else {
							levels.clear();
							levels.addAll(prefix.getPrefixPosition());
						}
						return true;
					}
				} else {
					index++;
				}						
			}
			return false;
		}
		
		private boolean findWordEndPosition(String content, final int level) {
			int index = 0;
			int resultIndex = 0;
			while ((index = content.indexOf(word)) != -1) {
				resultIndex += index + word.length() 
							- prefix.getPrefix().length();
				requestWordNumber--;
				if (requestWordNumber == -1) {
					levels.set(level + 1, resultIndex);
					return true;
				}
				if (index + word.length() < content.length()) {
					content = content.substring(index + word.length());
				} else {
					content = "";
				}
			}
			return false;
		}
		
		private void processTextElement(String content, final int level) {	
			content = StringEscapeUtils.unescapeHtml(content);
			if (StringUtils.isNotEmpty(prefix.getPrefix())) {
				content = prefix.getPrefix() + content;					
			}			
			boolean reslut = positionOfEnd ? findWordEndPosition(content, level) :
					findWordStartPosition(content, level);
			if (reslut) {
				return;
			}
			
			// if content of current text element doesn't end with whitespace
			// we should save the last word of this element as prefix for next 
			// text.
			// For example. we try to find word "Just" in <i>Jus<b>t</b</i>
			// we should save "Jus" as prefix for text in <b> tag 
			if (! content.matches("^.*\\s$")) {
				Pattern pattern = Pattern.compile("^.*\\s([^\\s]+)$");
				Matcher matcher = pattern.matcher(content);
				if (matcher.matches()) {
					int offset = content.length() - prefix.getPrefix().length() -
						matcher.group(1).length();
					prefix.setPrefix(matcher.group(1));
					prefix.setPosition(levels, level, offset);
				} else {
					if (StringUtils.isEmpty(prefix.getPrefix())) {
						prefix.setPosition(levels, level, 0);
					}
					prefix.setPrefix(content);
				}
			} else {
				prefix.setPrefix("");
			}
		}
		
		private void findWordInTag(TagNode node, final int level) {
			// if the current element is block element 
			// we shouldn't use suffix of prevous element as prefix for this
			boolean isBlock = ! isInlineElement(node.getName());
			if (isBlock) {
				prefix.setPrefix("");
			}
			for (Object child : node.getChildren()) {
				if (child instanceof ContentToken) {
					// if current child is text, try to find the word
					levels.set(level, levels.get(level) + 1);
					levels.set(level + 1, -1);
					String content = ((ContentToken) child).getContent().replace("&nbsp;", " ");
					processTextElement(content, level);					
				}
				if (child instanceof TagNode) {
					// if current child is tag - recursion
					levels.set(level, levels.get(level) + 1);
					levels.set(level + 1, -1);
					findWordInTag((TagNode) child, level + 1); 
				}
				if (requestWordNumber == -1) {
					return ;
				}
			}
			if (isBlock) {
				prefix.setPrefix("");
			}
		}		
	}
	
	private static class Prefix {
		private String prefix;		
		private LevelsList prefixPosition;
		
		public Prefix() {
			prefix = "";
		}
		
		public String getPrefix() {
			return prefix;
		}
		
		public void setPrefix(String prefix) {
			this.prefix = prefix;
		}
		
		public void setPosition(LevelsList current, int level, int offset) {
			prefixPosition = new LevelsList();
			for (int i = 0; i <= level; i++) {
				prefixPosition.add(current.get(i));
			}
			prefixPosition.add(offset);
		}

		public LevelsList getPrefixPosition() {
			return prefixPosition;
		}		
	}
	
	private static class LevelsList extends ArrayList<Integer> {

		private static final long serialVersionUID = 1L;		
		
		@Override
		public Integer get(int index) {
			while (index >= size()) {
				this.add(-1);
			}
			return super.get(index);
		}

		@Override
		public Integer set(int index, Integer element) {
			while (index >= size()) {
				this.add(-1);
			}
			return super.set(index, element);
		}
		
		public int[] getPosition() {
			int i;
			for (i = size() - 1; i >= 0; i--) {
				if (get(i) != -1) {
					break;
				}
			}
			if (i < 1) {
				return new int[0];
			}
			int[] result = new int[i];
			for (int j = 0; j < i; j++) {
				result[j] = get(j);
			}
			return result;
		}
		
		public int getOffset() {
			int i;
			for (i = size() - 1; i >= 0; i--) {
				if (get(i) != -1) {
					break;
				}
			}
			if (i == -1) {
				return -1;
			}
			return get(i);
		}
	}
}

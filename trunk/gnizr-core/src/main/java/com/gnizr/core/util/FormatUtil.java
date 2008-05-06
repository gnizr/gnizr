package com.gnizr.core.util;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.htmlparser.Parser;
import org.htmlparser.Tag;
import org.htmlparser.tags.FrameTag;
import org.htmlparser.tags.HeadingTag;
import org.htmlparser.tags.ScriptTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.htmlparser.visitors.NodeVisitor;
import org.htmlparser.visitors.TextExtractingVisitor;
import org.w3c.tidy.Tidy;

import com.gnizr.db.dao.Bookmark;

/**
 * Utility class for text formating and transformation. 
 * 
 * @author Harry Chen
 * @since 2.3
 *
 */
public class FormatUtil {
	
	private static final Logger logger = Logger.getLogger(FormatUtil.class);
	
	public static String tidyHTML(String htmlCode, boolean bodyOnly, String encoding){
		if(htmlCode == null){
			throw new NullPointerException("Input HTML code string is NULL");
		}
		StringReader stringReader = new StringReader(htmlCode);
		StringWriter tidyHtmlWriter = new StringWriter();
		try{
			Tidy tidy = new Tidy();
			tidy.setPrintBodyOnly(bodyOnly);
			tidy.setJoinClasses(true);
			tidy.setJoinStyles(true);
			tidy.setQuoteAmpersand(true);
			tidy.setHideComments(true);
			tidy.setInputEncoding(encoding);
			tidy.setOutputEncoding(encoding);		
			tidy.parse(stringReader,tidyHtmlWriter);
			return tidyHtmlWriter.getBuffer().toString();
		}catch(Exception e){
			logger.error("FormatUtil: tidyHTML error: " + e);
		}finally{		
			if(stringReader != null){
				stringReader.close();
			}
		}
		return htmlCode;		
	}
		
	public static String removeAbusiveTags(String htmlCode){
		if(htmlCode == null){
			throw new NullPointerException("Input HTML code string is NULL");
		}		
		try{
			Parser parser = Parser.createParser(htmlCode, "UTF-8");
			final NodeList nl = parser.parse(null);
			nl.visitAllNodesWith(new NodeVisitor(){
				public void visitTag(Tag tag){
					if((tag instanceof ScriptTag) ||
					   (tag instanceof FrameTag)){
						if(tag.getParent() == null){
							nl.remove(tag);
						}else{
							tag.getParent().getChildren().remove(tag);
						}
					}
					if(tag instanceof HeadingTag){
						if("H1".equalsIgnoreCase(tag.getTagName())||
						   "H2".equalsIgnoreCase(tag.getTagName())){
							tag.setTagName("H3");
							tag.getEndTag().setTagName("/H3");
						}
					}					
				}
			});			
			return nl.toHtml();
		}catch(Exception e){
			System.out.println(e);
		}
		return "";
	}
	
	
	/**
	 * Removes HTML tags from a HTML string
	 * @param htmlCode string contains HTML tags that need to be removed.
	 * @return a new string with HTML tags removed.
	 */
	public static String extractTextFromHtml(String htmlCode){
		if(htmlCode == null){
			throw new NullPointerException("Input HTML code string is NULL");
		}
		
		Parser parser = Parser.createParser(htmlCode,"UTF-8");
		TextExtractingVisitor visitor = new TextExtractingVisitor();
		try {
			parser.visitAllNodesWith(visitor);
			return visitor.getExtractedText();
		} catch (ParserException e) {
			logger.debug("HTML parsing error: " + htmlCode, e);
		}
		return "";
	}
	
	/**
	 * Removes text strings that are longer than a specific length. 
	 * 
	 * @param text the body of a text from which long texts should be removed.
	 * @param maxLength the max length of the text to be filtered out.
	 * @return a new string that contain texts that each of which is no longer than <code>maxLength</code>
	 */
	public static String removeLongWord(String text, int maxLength){
		if(text == null){
			throw new NullPointerException("Input string is NULL");
		}
		int max = 0;
		if(maxLength > max){
			max = maxLength;
		}
		return text.replaceAll("\\w{"+max+",}+"," ");
	}
	
	/**
	 * Replaces '*' (star) enclosed text with a pair of tags. 
	 * If any where in <code>text</code> matches
	 * <pre>
	 *  <code>*[non-space-separated-text]*</code>
	 * </pre>
	 * then the matching text will be replaced with a new string, which starts with the 
	 * string defined <code>pre</code>, and ends with the string defined by <code>post</code>.
	 * <p>
	 * For example, if the text is 
	 * <pre>hello world *foobar*!</pre>
	 * and <code>pre</code> is <code>&ltb&gt</code> and <code>post</code> is <code>&lt/b&gt</code>, 
	 * then the output string is
	 * <pre>hello world &ltb&gtfoobar&lt/b&gt!</pre>
	 * </p>
	 * @param text the input text
	 * @param pre the starting tag
	 * @param post the ending tag
	 * @return a new string with the matching star-enclosed text replaced.
	 */
	public static String highlightStarEnclosedText(String text, String pre, String post){
		try{
			String srcText = text;
			Pattern p = Pattern.compile("(.*)\\*(\\S+)\\*(.*)");
			Matcher m = p.matcher(text);
			while(m.matches()){
				srcText = m.replaceFirst("$1" + pre+"$2"+post + "$3");
				m = p.matcher(srcText);
			}
			return srcText;
		}catch(Exception e){
			
		}
		return text;
	}
	
	/**
	 * Returns a <code>Map</code> representation of <code>Bookmark</code>.
	 * 
	 * @param bookmark an instantiated bookmark object
	 * @return a map object that transforms bookmark properties into key/value pairs.
	 */
	public static Map<String, Object> getBookmarkAsMap(Bookmark bookmark){
		Map<String, Object> map = null;
		if(bookmark != null){
			map = new HashMap<String, Object>();
			map.put("id",bookmark.getId());
			if(bookmark.getTitle() != null){
				map.put("title",bookmark.getTitle());
			}
			if(bookmark.getNotes() != null){
				map.put("notes",bookmark.getNotes());
			}
			if(bookmark.getCreatedOn() != null){
				map.put("createdOn",bookmark.getCreatedOn());
			}
			if(bookmark.getTagList() != null && bookmark.getTagList().size() > 0){
				map.put("tags",bookmark.getTagList());
			}
			if(bookmark.getLastUpdated() != null){
				map.put("lastUpdated",bookmark.getLastUpdated());
			}
			if(bookmark.getLink() != null){
				if(bookmark.getLink().getUrl() != null){
					map.put("url",bookmark.getLink().getUrl());
				}
				if(bookmark.getLink().getUrlHash() != null){
					map.put("urlHash",bookmark.getLink().getUrlHash());
				}
				if(bookmark.getLink().getCount() > 0){
					map.put("urlCount",bookmark.getLink().getCount());
				}
			}
			if(bookmark.getUser() != null){
				if(bookmark.getUser().getUsername() != null){
					map.put("username", bookmark.getUser().getUsername());
				}
				if(bookmark.getUser().getFullname() != null){
					map.put("fullname", bookmark.getUser().getFullname());
				}
			}						
		}
		return map;		
	}
	
}

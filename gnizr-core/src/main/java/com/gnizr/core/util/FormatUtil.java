package com.gnizr.core.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.htmlparser.Parser;
import org.htmlparser.util.ParserException;
import org.htmlparser.visitors.TextExtractingVisitor;

public class FormatUtil {
	
	private static final Logger logger = Logger.getLogger(FormatUtil.class);
	
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
}

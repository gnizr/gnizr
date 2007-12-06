/*
 * gnizr is a trademark of Image Matters LLC in the United States.
 * 
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either expressed or implied. See the License
 * for the specific language governing rights and limitations under the License.
 * 
 * The Initial Contributor of the Original Code is Image Matters LLC.
 * Portions created by the Initial Contributor are Copyright (C) 2007
 * Image Matters LLC. All Rights Reserved.
 */
package com.gnizr.core.web.view.freemarker;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.htmlparser.Parser;
import org.htmlparser.PrototypicalNodeFactory;
import org.htmlparser.lexer.Page;
import org.htmlparser.nodes.TextNode;
import org.htmlparser.util.NodeList;

import freemarker.ext.beans.StringModel;
import freemarker.template.SimpleScalar;
import freemarker.template.SimpleSequence;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;

public class HighlightTextMethod implements TemplateMethodModelEx{

	private static final Logger logger = Logger.getLogger(HighlightTextMethod.class);
	
	public Object exec(List args) throws TemplateModelException {
		if(args.size() != 3 && args.size() != 4){
			throw new TemplateModelException("Wrong number of arguments");
		}
		String content = "";
		if(args.get(0) instanceof SimpleScalar){
			content = ((SimpleScalar)args.get(0)).getAsString();
		}else if(args.get(0) instanceof StringModel){
			content = ((StringModel)args.get(0)).getAsString();
		}
		SimpleSequence   textList = (SimpleSequence)args.get(1);
		SimpleScalar htmlTag = (SimpleScalar)args.get(2);
		SimpleSequence cssClass = null;
		if(args.size() == 4){
			 cssClass = (SimpleSequence)args.get(3);
		}				
		try{
			String lt = createLeftTag(htmlTag, cssClass);
			String rt = createRightTag(htmlTag);
			List<String> t2hl = createText2Highlight(textList);		
			
			PrototypicalNodeFactory factory = new PrototypicalNodeFactory();
			factory.setTextPrototype(new HighlightTextNode("",t2hl,lt,rt));
			Parser htmlParser = new Parser();
			htmlParser.setNodeFactory(factory);
			Parser.createParser(content,"UTF-8");
			htmlParser.setInputHTML(content);
			NodeList nodeList = htmlParser.parse(null);
			content = nodeList.toHtml();
		}catch(Exception e){
			logger.error(e);
		}
		return content;
	}

	private List<String> createText2Highlight(SimpleSequence seq) throws TemplateModelException{
		List<String> text2highlight = new ArrayList<String>();
		int n = seq.size();
		for(int i = 0; i < n; i++){
			text2highlight.add(((SimpleScalar)seq.get(i)).getAsString());
		}
		return text2highlight;
	}
	
	private String createLeftTag(SimpleScalar htmlTag, SimpleSequence cssClass) throws TemplateModelException{
		StringBuffer sb = new StringBuffer("<");
		sb.append(htmlTag.getAsString());
		if(cssClass != null){
			int n = cssClass.size();
			if(n > 0){
				sb.append(" class=\"");
			}
			for(int i = 0 ; i < n; i ++){
				String cls = ((SimpleScalar)cssClass.get(i)).getAsString();
				sb.append(cls);
				if(i < (n-1)){
					sb.append(" ");
				}
			}
			if(n > 0){
				sb.append("\"");
			}
		}
		sb.append(">");
		return sb.toString();
	}
	
	private String createRightTag(SimpleScalar htmlTag){
		return "</" + htmlTag.getAsString() + ">";
	}
	
	class HighlightTextNode extends TextNode{
		private List<String> text2highlight;
		private String leftTag;
		private String rightTag;
		
		public HighlightTextNode(String text) {
			super(text);
		}

		public HighlightTextNode(Page page, int start, int end){
			super(page,start,end);
		}
		
		public HighlightTextNode(String string, List<String> t2hl, String lt, String rt) {
			this(string);
			this.text2highlight = t2hl;
			this.leftTag = lt;
			this.rightTag = rt;
		}

		private static final long serialVersionUID = -1971308871733743362L;

		@Override
		public String toHtml(boolean flag) {
			String s = mPage.getText(getStartPosition(), getEndPosition());
			for(String t : text2highlight){				
				Matcher m = Pattern.compile("\\b"+t+"\\b", Pattern.CASE_INSENSITIVE).matcher(s);
				while(m.find()){
					String matchedText = m.group();
					s = s.replace(matchedText, leftTag+matchedText+rightTag);
				}						
			}
			return s;
		}
	}
}

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
package com.gnizr.db.dao.tag;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.gnizr.db.dao.MachineTag;

/**
 * <p>A paser for parsing a sequence of tag strings. Tags maybe either regular tags or machine tags. 
 * Machine tags are tag strings with special syntax. An example of a machine tag is <code>geo:lat=32.00</code>.
 * The syntax of a machine tag is in the form: <code>ns:predicate=value</code>
 * The <code>ns</code> field is the namespace prefix. The <code>predicate</code> field is the predicate 
 * (or property) that describes the URL of the bookmark. The <code>value</code> field is the value of
 * of the predicate.</p> 
 * <p>Machine tags without namespace prefix, which defaults to <code>gnizr</code>, may be expressed
 * in an alternative syntax: <code>predicate:value</code>.
 * </p>
 * <p>Given a sequence of tag string: <code>java programming "foo bar" for:user dc:date=1999-10-10</code>,
 * the products of this parser is as the follows.</p>
 * 
 * <h5>Tags:</h5>
 * <ul>
 * <li>java</li>
 * <li>programming</li>
 * <li>"foo</li>
 * <li>bar"</li>
 * <li>for:user</li>
 * <li>dc:date=1999-10-10</li>
 * </ul>
 * 
 * <h5>Machine Tags</h5>
 * <ul>
 * <li>for:user (predicate=for, value=user)</li>
 * <li>dc:date=1999-10-10 (ns=dc, predicate=date, value=1999-10-10)</li>
 * </ul>
 * 
 * </p>
 * 
 * @author Harry Chen
 *
 */
public class TagsParser {

	private static final Logger logger = Logger.getLogger(TagsParser.class);
	private String parseString;
	private List<String> tagList;
	private List<MachineTag> machineTagList;
	
	public TagsParser(String parseString){
		this(parseString,null);
	}
	
	public TagsParser(String parseString, String delimitRegex){
		logger.debug("new TagParser(): parseString="+parseString);
		this.parseString = parseString;		
		this.tagList = new ArrayList<String>();
		this.machineTagList = new ArrayList<MachineTag>();
		if(parseString != null && parseString.length() > 0){
			//checkQuoteBalance();
			doParse(delimitRegex);
		}
	}
	
	/*
	private void checkQuoteBalance() throws QuoteNotBalancedException {
		int qCnt = 0;
		for(int i = 0; i < parseString.length(); i++){
			if(parseString.charAt(i) == '\"'){
				qCnt++;
			}
		}
		if((qCnt % 2) != 0){
			throw new QuoteNotBalancedException();
		}
	}
	*/
	private void doParse(String regex){
		Scanner scanner = new Scanner(parseString.trim());		
		if(regex != null){
			scanner = scanner.useDelimiter(regex);
		}
		while(scanner.hasNext()){
			String tagTerm = getNextTagTerm(scanner);
			logger.debug("tagTerm="+tagTerm);
			if(tagTerm.contains(":") == true){			
				MachineTag machineTagTerm = getMachineTag(tagTerm);
				if(machineTagTerm != null){					
					machineTagList.add(machineTagTerm);		
					logger.debug("machineTag="+machineTagTerm.toString());
				}
			}
			tagList.add(tagTerm);
		}		
	}
	
	private String getNextTagTerm(Scanner scanner){
		String nextTerm = scanner.next().trim();
		return nextTerm;
	}
	
	/*
	private String getNextTagTerm(Scanner scanner){
		String nextTerm = scanner.next();
		if(nextTerm.contains("\"") == true && !nextTerm.matches(".*\".*\".*")){
			StringBuilder sb = new StringBuilder(nextTerm);
			while(scanner.hasNext()){
				nextTerm = scanner.next();
				sb.append(" ");
				sb.append(nextTerm);
				if(nextTerm.contains("\"")){
					nextTerm = sb.toString();
					break;
				}
			}
		}
		return nextTerm;
	}
	*/
	private MachineTag getMachineTag(String tagTerm) {
		
		Pattern fullSyntaxPattern = Pattern.compile("([a-z0-9]+):(\\w+)=(.*)");
		Pattern alterSyntaxPattern = Pattern.compile("(\\w+):(\\S+)");
		
		String mtExpression = tagTerm;
		Matcher m = fullSyntaxPattern.matcher(mtExpression);
		if(m.matches()){
			String ns = m.group(1);
			String pred = m.group(2);
			String value = m.group(3);
			return new MachineTag(ns,pred,value);
		}else{
			m = alterSyntaxPattern.matcher(mtExpression);
			if(m.matches()){
				String pred = m.group(1);
				String value = m.group(2);
				return new MachineTag(null,pred,value);
			}
		}
		return null;
	}
	
	public List<String> getTags(){
		return tagList;
	}
	
	public List<MachineTag> getMachineTags(){
		return machineTagList;
	}
	
	public String getParseString(){
		return parseString;
	}
}

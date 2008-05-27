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
package com.gnizr.web.action.search;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;

import com.gnizr.core.search.DocumentCreator;
import com.gnizr.core.search.SearchIndexManager;
import com.gnizr.core.search.SearchTermSuggestion;
import com.gnizr.web.action.AbstractAction;

public class SearchQuerySuggest extends AbstractAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7070010776104912157L;
	private static final Logger logger = Logger
			.getLogger(SearchQuerySuggest.class);

	private String q;
	private Map<String, String> keywords = new HashMap<String, String>();

	private SearchTermSuggestion searchTermSuggestion;
	private SearchIndexManager searchIndexManager;

	public SearchIndexManager getSearchIndexManager() {
		return searchIndexManager;
	}

	public void setSearchIndexManager(SearchIndexManager searchIndexManager) {
		this.searchIndexManager = searchIndexManager;
	}

	public SearchTermSuggestion getSearchTermSuggestion() {
		return searchTermSuggestion;
	}

	public void setSearchTermSuggestion(
			SearchTermSuggestion searchTermSuggestion) {
		this.searchTermSuggestion = searchTermSuggestion;
	}

	public Map<String, String> getKeywords() {
		return keywords;
	}

	public String getQ() {
		return q;
	}

	public void setQ(String q) {
		this.q = q;
	}

	private IndexReader getIndexReader() throws CorruptIndexException, IOException {
		File indexDir = searchIndexManager.getIndexDirectory();
		if(indexDir != null){
			return IndexReader.open(indexDir);
		}
		return null;
	}

	@Override
	protected String go() throws Exception {
		logger.debug("Start SearchTermSuggestion. q = " + getQ()
				+ " keywords size: " + keywords.size());
		if (q != null && searchTermSuggestion != null) {		
			TermQuery termQuery = null;
			QueryParser parser = new QueryParser(DocumentCreator.FIELD_TEXT,DocumentCreator.createDocumentAnalyzer());
			Query query = null;
			try{
				query = parser.parse(q);
			}catch(Exception e){
				logger.debug("QueryParser error: " + e);
			}
			if(query == null){
				return SUCCESS;
			}			
			if((query instanceof TermQuery) == false){
				logger.debug("No search term suggestion. Non TermQuery is not curently support: " + q);
				return SUCCESS;
			}else{
				termQuery = (TermQuery)query;
				String fld = termQuery.getTerm().field();
				if(fld.equals(DocumentCreator.FIELD_TEXT) == false &&
				   fld.equals(DocumentCreator.FIELD_TAG) == false){
					logger.debug("No search term suggestion. Term field is neither 'tag' or 'text'. Field: " + fld);
					return SUCCESS;
				}
			}
				
			String[] results = new String[0];
			IndexReader idxReader = null; 
			try{				
				idxReader = getIndexReader();			
				String field = termQuery.getTerm().field();
				String value = termQuery.getTerm().text();				
				results = searchTermSuggestion.suggest(value,idxReader,field);
				if(results != null && results.length > 0){
					if(field.equals(DocumentCreator.FIELD_TAG)){
						keywords = formatToKeywords(results,DocumentCreator.FIELD_TAG);
					}else{
						keywords = formatToKeywords(results,null);
					}
				}
			}catch(Exception e){
				logger.error(e);
			}finally{
				if(idxReader != null){
					try{
						idxReader.close();
					}catch(Exception e){
						logger.error("Unable to close IndexReader for bookmark search index");
					}
				}
			}
		}
		return SUCCESS;
	}
	
	private Map<String,String> formatToKeywords(String[] terms, String field){
		Map<String,String> map = new HashMap<String, String>();
		for(String t : terms){
			String key = t;
			if(field != null){				
				key = key.replace(":", "\\:");
				key = key.replace("!", "\\!");
				key = key.replace("~", "\\~");
				key = field + ":" + key;
			}
			map.put(key,key);
		}
		return map;
	}

}

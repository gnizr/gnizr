package com.gnizr.web.action.search;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;

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
			String[] results = new String[0];
			IndexReader idxReader = null; 
			try{
				idxReader = getIndexReader();
			}catch(Exception e){
				logger.error(e);
			}
			if(idxReader != null){
				try{
					results = searchTermSuggestion.suggest(q,idxReader,DocumentCreator.FIELD_TEXT);
				}finally{
					idxReader.close();
				}
			}else{
				results = searchTermSuggestion.suggest(q);
			}
			for (String s : results) {
				keywords.put(s, s);
			}
		}
		return SUCCESS;
	}

}

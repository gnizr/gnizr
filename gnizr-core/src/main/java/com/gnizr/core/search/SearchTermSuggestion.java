package com.gnizr.core.search;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PrefixQuery;

public class SearchTermSuggestion implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5872374638400499500L;

	private static final Logger logger = Logger.getLogger(SearchTermSuggestion.class);
	
	private SearchSuggestIndexer searchSuggestIndexer;
	
	private int minQueryStringLength = 3;
	
	private int maxSuggestionSize = 5;
	
	public SearchSuggestIndexer getSearchSuggestIndexer() {
		return searchSuggestIndexer;
	}

	public void setSearchSuggestIndexer(SearchSuggestIndexer searchSuggestIndexer) {
		this.searchSuggestIndexer = searchSuggestIndexer;
	}
	
	public String[] suggest(String queryString){
		List<String> results = new ArrayList<String>(5);
		if(queryString != null && queryString.length() >= minQueryStringLength){
			Term term = new Term("t", queryString);
			PrefixQuery prefixQuery = new PrefixQuery(term);
			IndexReader indexReader = null;
			IndexSearcher indexSearch = null;
			try{
				indexReader = searchSuggestIndexer.openSuggestIndexReader();
				indexSearch = new IndexSearcher(indexReader);
				Hits hits = indexSearch.search(prefixQuery);
				for(int i = 0; i < hits.length() && i < maxSuggestionSize; i++){
					String value = hits.doc(i).get("t");
					results.add(value);
				}
			}catch(Exception e){
				logger.error(e);
			}finally{
				try {
					if(indexSearch != null){
						indexSearch.close();
					}
				} catch (IOException e) {
					logger.error(e);
				}
				if(indexReader != null){
					try {
						indexReader.close();
					} catch (IOException e) {
						logger.error(e);
					}
				}
			}		
		}
		return results.toArray(new String[results.size()]);
	}

	public int getMinQueryStringLength() {
		return minQueryStringLength;
	}

	public void setMinQueryStringLength(int minQueryStringLength) {
		this.minQueryStringLength = minQueryStringLength;
	}

	public int getMaxSuggestionSize() {
		return maxSuggestionSize;
	}

	public void setMaxSuggestionSize(int maxSuggestionSize) {
		this.maxSuggestionSize = maxSuggestionSize;
	}
	
}

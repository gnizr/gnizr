package com.gnizr.core.search;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

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
	
	private class SuggestWord implements Comparable<SuggestWord>{

		private String word;
		private int docFreq;
		
		public SuggestWord(String word, int docFreq){
			this.word = word;
			this.docFreq = docFreq;
		}
		
		public String getWord() {
			return word;
		}

		public int getDocFreq() {
			return docFreq;
		}
		/**
		 * Compares this object with another <code>SuggestWord</code>. Returns
		 * @return a negative, zero or posititive integer if this object
		 * has a doc freq number greater than, equals to, or less than <code>w1</code> 
		 */
		public int compareTo(SuggestWord w1) {
			if(this.docFreq < w1.docFreq){
				return 1;
			}else if(this.docFreq == w1.docFreq){
				return 0;
			}else{
				return -1;
			}
		}		
	}
	
	public String[] suggest(String queryString, IndexReader idxReader, String field){
		List<String> results = new ArrayList<String>(maxSuggestionSize);
		if(queryString != null && queryString.length() >= minQueryStringLength){
			Term term = new Term("t", queryString);
			PrefixQuery prefixQuery = new PrefixQuery(term);
			IndexReader indexReader = null;
			IndexSearcher indexSearch = null;
			try{
				indexReader = searchSuggestIndexer.openSuggestIndexReader();
				indexSearch = new IndexSearcher(indexReader);
				Hits hits = indexSearch.search(prefixQuery);
				int maxNumCandidate = maxSuggestionSize;
				if(idxReader != null && field != null){
					maxNumCandidate = maxSuggestionSize * 10;
				}
				PriorityQueue<SuggestWord> suggestQueue = new PriorityQueue<SuggestWord>(maxNumCandidate);
				for(int i = 0; i < hits.length() && i < maxNumCandidate; i++){
					String sugWord = hits.doc(i).get("t");
					// check if the 'sugWord' matches at least one doc in the 
					// source index database (idxReader)
					if(idxReader != null && field != null){
						int freq = idxReader.docFreq(new Term(field,sugWord));
						if(freq > 0){
							suggestQueue.add(new SuggestWord(sugWord,freq));
						}
					}else{
						suggestQueue.add(new SuggestWord(sugWord,0));
					}					
				}
				int qSize = suggestQueue.size();
				for(int i = 0; i < maxSuggestionSize && i < qSize; i++){
					SuggestWord word = suggestQueue.poll();
					results.add(word.getWord());
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
	
	
	
	public String[] suggest(String queryString){
		return suggest(queryString, null, null);
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

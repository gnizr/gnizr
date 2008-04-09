package com.gnizr.core.search;

import java.io.Serializable;

import org.apache.log4j.Logger;

/**
 * The configuration profile for initializing the search index database. 
 * 
 * @author Harry Chen
 * @since 2.4
 */
public class SearchIndexProfile implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1417786047589830064L;

	private static final Logger logger = Logger.getLogger(SearchIndexProfile.class);
	
	private String searchIndexDirectory;
	
	private boolean resetSearchIndexOnStart;
	
	private String searchSuggestDataFile;
	
	private boolean suggestPopularTagsEnabled;

	public boolean isSuggestPopularTagsEnabled() {
		return suggestPopularTagsEnabled;
	}

	public void setSuggestPopularTagsEnabled(boolean suggestPopularTagsEnabled) {
		this.suggestPopularTagsEnabled = suggestPopularTagsEnabled;
	}

	public SearchIndexProfile(){
		this.searchIndexDirectory = null;
		this.resetSearchIndexOnStart = true;
	}
	
	public String getSearchIndexDirectory() {
		return searchIndexDirectory;
	}

	public void setSearchIndexDirectory(String indexDirectory) {
		this.searchIndexDirectory = indexDirectory;
		final String m = "Set search index directory: " + indexDirectory;
		logger.info(m);
	}

	public boolean isResetSearchIndexOnStart() {
		return resetSearchIndexOnStart;
	}

	public void setResetSearchIndexOnStart(boolean overwrite) {
		this.resetSearchIndexOnStart = overwrite;
		final String m = "Set search index resetSearchIndexOnStart: " + overwrite;
		logger.info(m);
	}

	public String getSearchSuggestDataFile() {
		return searchSuggestDataFile;
	}

	public void setSearchSuggestDataFile(String searchSuggestDataFile) {
		this.searchSuggestDataFile = searchSuggestDataFile;
	}
	
}

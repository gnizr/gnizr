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
	
	private boolean overwrite;
	
	public SearchIndexProfile(){
		this.searchIndexDirectory = null;
		this.overwrite = true;
	}
	
	public String getSearchIndexDirectory() {
		return searchIndexDirectory;
	}

	public void setSearchIndexDirectory(String indexDirectory) {
		this.searchIndexDirectory = indexDirectory;
		final String m = "Set search index directory: " + indexDirectory;
		logger.info(m);
	}

	public boolean isOverwrite() {
		return overwrite;
	}

	public void setOverwrite(boolean overwrite) {
		this.overwrite = overwrite;
		final String m = "Set search index overwrite: " + overwrite;
		logger.info(m);
	}
	
}

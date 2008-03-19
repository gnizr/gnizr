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
	
	private String directoryPath;
	
	private boolean overwrite;
	
	public SearchIndexProfile(){
		this.directoryPath = null;
		this.overwrite = true;
	}
	
	public String getDirectoryPath() {
		return directoryPath;
	}

	public void setDirectoryPath(String directoryPath) {
		this.directoryPath = directoryPath;
		final String m = "Set search index directory: " + directoryPath;
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

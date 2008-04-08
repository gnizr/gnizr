package com.gnizr.core.search;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;

public class SearchSuggestIndexer implements Serializable {

	public static final String SUGGEST_INDEX_NAME = "suggest_index";

	/**
	 * 
	 */
	private static final long serialVersionUID = 3081443635218877501L;

	private static final Logger logger = Logger
			.getLogger(SearchSuggestIndexer.class);

	private SearchIndexProfile searchIndexProfile;

	private File suggestIndexDirectory;

	public void init() {
		if (searchIndexProfile == null) {
			throw new NullPointerException("SearchIndexProfile is not defined");
		}
		String searchIndexDir = searchIndexProfile.getSearchIndexDirectory();
		suggestIndexDirectory = new File(searchIndexDir, SUGGEST_INDEX_NAME);

		String dataFile = searchIndexProfile.getSearchSuggestDataFile();
		File file = new File(dataFile);
		if (file.exists() == true) {
			logger.debug("Attempt to read data from " + file.toString());
			try {
				loadData(new FileInputStream(file), suggestIndexDirectory);
			} catch (IOException e) {
				logger.error("Unable to reading from " + file, e);
			}
		} else {
			logger
					.debug("Attempt to read data from a resource in the class path");
			try {
				loadData(SearchSuggestIndexer.class
						.getResourceAsStream(dataFile), suggestIndexDirectory);
			} catch (Exception e) {
				logger.error("Unable to find resource in the classpath: "
						+ file, e);
			}
		}
	}

	private void loadData(InputStream is, File indexDir) {
		if (is != null) {
			IndexWriter writer = null;
			InputStreamReader isReader = null;
			try {
				writer = new IndexWriter(indexDir, new StandardAnalyzer(), true);
				isReader = new InputStreamReader(is);
				BufferedReader bufferedReader = new BufferedReader(isReader);
				String aline = bufferedReader.readLine();
				while (aline != null) {
					StringTokenizer tokenizer = new StringTokenizer(aline,
							"\n\r\f", false);
					while (tokenizer.hasMoreTokens()) {
						String token = tokenizer.nextToken().trim();
						if (token != null) {
							Document doc = new Document();
							doc.add(new Field("t", token, Field.Store.YES,
									Field.Index.UN_TOKENIZED));
							writer.addDocument(doc);
						}
					}
					aline = bufferedReader.readLine();
				}
				writer.optimize();
			} catch (Exception e) {

			} finally {
				try {
					if (writer != null) {
						writer.close();
					}
				} catch (Exception e) {
					logger.error(e);
				}
				try {
					if (isReader != null) {
						isReader.close();
					}
				} catch (Exception e) {
					logger.error(e);
				}
			}
		}
	}

	public SearchIndexProfile getSearchIndexProfile() {
		return searchIndexProfile;
	}

	public void setSearchIndexProfile(SearchIndexProfile searchIndexProfile) {
		this.searchIndexProfile = searchIndexProfile;
	}

	public File getSuggestIndexDirectory() {
		return suggestIndexDirectory;
	}

}

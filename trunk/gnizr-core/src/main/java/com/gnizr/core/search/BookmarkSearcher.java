package com.gnizr.core.search;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;

import com.gnizr.db.dao.DaoResult;

/**
 * Provides bookmark search function. This class can be used to perform search over the collection of bookmarks
 * saved by a given user or all users in the system. 
 * 
 * @author Harry Chen
 * @since 2.4.0
 */
public class BookmarkSearcher implements Serializable {

	private static final long serialVersionUID = -5131043207555467304L;

	private SearchIndexManager searchIndexManager;

	/**
	 * Checks whether <code>searchIndexManager</code> is properly 
	 * set on this class. 
	 */
	public void init() {
		if(searchIndexManager == null){
			throw new NullPointerException("BookmarkSearcher.init(): searchIndexManager is not defined");
		}
	}

	/**
	 * Returns the <code>SearchIndexManager</code> object used by this class.
	 * @return an instantiated <code>SearchIndexManager</code>. Returns <code>null</code>,
	 * if the object hasn't been set.
	 */
	public SearchIndexManager getSearchIndexManager() {
		return searchIndexManager;
	}

	/**
	 * Sets the <code>SearchindexManager</code> object to be used by this class.
	 * @param searchIndexManager an instantiated <code>SearchIndexManager</code> object.
	 */
	public void setSearchIndexManager(SearchIndexManager searchIndexManager) {
		this.searchIndexManager = searchIndexManager;
	}

	/**
	 * Find bookmarks that matches the input query from the collection 
	 * of bookmarks saved by all users. 
	 *  
	 * @param query the search query expressed using the Lucene query syntax.
	 * @param offset defines the start position to collect bookmarks from the search result. 
	 * The starting position of the first item in search result is <code>0</code>. 
	 * @param count the maximum number of bookmarks to collect from the search result. This number should be less than 
	 * or equal to <code>DaoResult.getSize</code>. 
	 * @return a search result object of the matching bookmarks.
	 * 
	 * @throws IOException
	 * @throws ParseException
	 */
	public DaoResult<BookmarkDoc> searchAll(String query, int offset, int count)
			throws IOException, ParseException {
		DaoResult<BookmarkDoc> result = null;
		IndexSearcher searcher = null;
		try {
			File directory = searchIndexManager.getIndexDirectory();
			searcher = new IndexSearcher(IndexReader.open(directory));
			Analyzer analyzer = DocumentCreator.createDocumentAnalyzer();
			QueryParser parser = new QueryParser(DocumentCreator.FIELD_TEXT,analyzer);
			Hits hits = searcher.search(parser.parse(query));
			List<BookmarkDoc> bmDocs = new ArrayList<BookmarkDoc>();
			if (offset >= 0 && offset < hits.length()) {
				if (count > 0) {					
					for (int i = offset; i < hits.length() && bmDocs.size() < count; i++) {
						BookmarkDoc doc = createBookmarkDoc(hits.doc(i));
						if (doc != null) {
							bmDocs.add(doc);
						}
					}
				}
			}
			result = new DaoResult<BookmarkDoc>(bmDocs,hits.length());
		} finally {
			if (searcher != null) {
				searcher.close();
			}
		}
		return result;
	}


	/**
	 * Find bookmarks that matches the input query from the collection 
	 * of bookmarks saved by a given user. 
	 *  
	 * @param query the search query expressed using the Lucene query syntax.
	 * @param username search only within this user's bookmark collection.
	 * @param offset defines the start position to collect bookmarks from the search result. 
	 * The starting position of the first item in search result is <code>0</code>. 
	 * @param count the maximum number of bookmarks to collect from the search result. This number should be less than 
	 * or equal to <code>DaoResult.getSize</code>. 
	 * @return a search result object of the matching bookmarks.
	 * 
	 * @throws IOException
	 * @throws ParseException
	 */
	public DaoResult<BookmarkDoc> searchUser(String query, String username, int offset, int count)
		throws IOException, ParseException{
		DaoResult<BookmarkDoc> result = null;
		IndexSearcher searcher = null;
		try {
			File directory = searchIndexManager.getIndexDirectory();
			searcher = new IndexSearcher(IndexReader.open(directory));
			Analyzer analyzer = DocumentCreator.createDocumentAnalyzer();
			QueryParser parser = new QueryParser(DocumentCreator.FIELD_TEXT,analyzer);
			
			TermQuery matchUserQuery = new TermQuery(new Term(DocumentCreator.FIELD_USER,username));
			Query inputQuery = parser.parse(query);
			BooleanQuery boolQuery = new BooleanQuery();
			boolQuery.add(matchUserQuery, BooleanClause.Occur.MUST);
			boolQuery.add(inputQuery,BooleanClause.Occur.MUST);
			
			Hits hits = searcher.search(boolQuery);
			List<BookmarkDoc> bmDocs = new ArrayList<BookmarkDoc>();
			if (offset >= 0 && offset < hits.length()) {
				if (count > 0) {					
					for (int i = offset; i < hits.length() && bmDocs.size() < count; i++) {
						BookmarkDoc doc = createBookmarkDoc(hits.doc(i));
						if (doc != null) {
							bmDocs.add(doc);
						}
					}
				}
			}
			result = new DaoResult<BookmarkDoc>(bmDocs,hits.length());
		} finally {
			if (searcher != null) {
				searcher.close();
			}
		}
		return result;
	}
	
	
	private BookmarkDoc createBookmarkDoc(Document doc) {
		if(doc != null){
			String bmid = doc.get(DocumentCreator.FIELD_BOOKMARK_ID);
			String url = doc.get(DocumentCreator.FIELD_URL);
			String urlHash = doc.get(DocumentCreator.FIELD_URL_MD5);
			String notes = doc.get(DocumentCreator.FIELD_NOTES);
			String username = doc.get(DocumentCreator.FIELD_USER);
			String title = doc.get(DocumentCreator.FIELD_TITLE);
		
			BookmarkDoc bmDoc = new BookmarkDoc();
			bmDoc.setBookmarkId(Integer.parseInt(bmid));
			bmDoc.setUrl(url);
			bmDoc.setUrlHash(urlHash);
			bmDoc.setNotes(notes);
			bmDoc.setUsername(username);
			bmDoc.setTitle(title);
			return bmDoc;
		}
		return null;
		
	}

}

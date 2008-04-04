package com.gnizr.core.search;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
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

public class BookmarkSearcher implements Serializable {

	private static final long serialVersionUID = -5131043207555467304L;

	private SearchIndexProfile searchIndexProfile;

	public SearchIndexProfile getSearchIndexProfile() {
		return searchIndexProfile;
	}

	public void setSearchIndexProfile(SearchIndexProfile searchIndexProfile) {
		this.searchIndexProfile = searchIndexProfile;
	}

	public void init() {
		if (searchIndexProfile == null) {
			throw new NullPointerException(
					"BookmarkSearcher.init(): searchIndexProfile is not defined");
		}
	}

	public DaoResult<BookmarkDoc> searchAll(String query, int offset, int count)
			throws IOException, ParseException {
		DaoResult<BookmarkDoc> result = null;
		IndexSearcher searcher = null;
		try {
			searcher = new IndexSearcher(searchIndexProfile.getSearchIndexDirectory());
			QueryParser parser = new QueryParser(DocumentCreator.FIELD_TITLE,
					new StandardAnalyzer());
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

	public DaoResult<BookmarkDoc> searchUser(String query, String username, int offset, int count)
		throws IOException, ParseException{
		DaoResult<BookmarkDoc> result = null;
		IndexSearcher searcher = null;
		try {
			searcher = new IndexSearcher(searchIndexProfile.getSearchIndexDirectory());
			QueryParser parser = new QueryParser(DocumentCreator.FIELD_TITLE,
					new StandardAnalyzer());
			
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

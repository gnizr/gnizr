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
package com.gnizr.core.search;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.KeywordAnalyzer;
import org.apache.lucene.analysis.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

import au.id.jericho.lib.html.Source;
import au.id.jericho.lib.html.TextExtractor;

import com.gnizr.db.dao.Bookmark;

public class DocumentCreator implements Serializable {

	private static final Logger logger = Logger
			.getLogger(DocumentCreator.class);

	
	public static final String FIELD_BOOKMARK_ID = "bmid";
	public static final String FIELD_URL = "url";
	public static final String FIELD_URL_MD5 = "urlMD5";
	public static final String FIELD_TITLE = "title";
	public static final String FIELD_NOTES = "notes";
	public static final String FIELD_TAG = "tag";
	public static final String FIELD_TEXT = "text"; // combined NOTES, TITLE and TAGS
	public static final String FIELD_USER = "user";
	public static final String FIELD_CREATED_ON = "createdOn";
	public static final String FIELD_LAST_UPDATED = "lastUpdated";
	public static final String FIELD_INDEX_TYPE = "indexType";

	public static final String INDEX_TYPE_LEAD = "lead";

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 980901600301331248L;

	
	/**
	 * Adds a <code>Field</code> to the input <code>Document</code> to indicate 
	 * that this document is the leading document in search. When multiple bookmarks
	 * of the same URL is bookmarked, a leading document is the one document that
	 * is the representative of all other bookmarks.  
	 * 
	 * @param doc a new <code>Field</code> will be added to this document
	 * @return the same input instance with a new <code>Field</code> added. The new
	 * field name is <code>indexType</code> and has value <code>lead</code>.
	 */
	public static Document addIndexTypeLead(Document doc) {
		if (doc == null) {
			throw new NullPointerException("input Document object is NULL");
		}
		doc.add(new Field(FIELD_INDEX_TYPE, INDEX_TYPE_LEAD, Field.Store.YES,
				Field.Index.UN_TOKENIZED));
		return doc;
	}

	/**
	 * Removes the <code>Field</code> that indicates the document being a 
	 * leading document. Nothing happens if the input document doesn't
	 * have a <code>indexType</code> field with value <code>lead</code>. 
	 * 
	 * @param doc removes the leading document field from this document
	 * @return the same input instance with the leading document field
	 * removed, if it exists.
	 */
	@SuppressWarnings("unchecked")
	public static Document removeIndexTypeLead(Document doc) {
		if (doc == null) {
			throw new NullPointerException("input Document object is NULL");
		}
		Field[] fields = doc.getFields(FIELD_INDEX_TYPE);
		if (fields != null && fields.length > 0) {
			List<Field> f2keep = new ArrayList<Field>();
			for (Field f : fields) {
				if (!f.stringValue().equals(INDEX_TYPE_LEAD)) {
					f2keep.add(f);
				}
			}
			doc.removeFields(FIELD_INDEX_TYPE);
			for (Field f : f2keep) {
				doc.add(f);
			}
		}
		return doc;
	}
	
	public static Field createFieldBookmarkId(int id){
		return new Field(FIELD_BOOKMARK_ID, String.valueOf(id),
				Field.Store.YES, Field.Index.TOKENIZED);
	}
	
	public static Field createFieldTitle(String title){
		if(title == null){
			throw new NullPointerException("title is NULL");
		}
		return new Field(FIELD_TITLE, title, Field.Store.YES,Field.Index.NO);	
	}

	public static Field createFieldText(String text){
		if(text == null){
			throw new NullPointerException("text is NULL");
		}
		return new Field(FIELD_TEXT, text, Field.Store.NO, Field.Index.TOKENIZED);
	}
	
	public static Field createFieldUser(String username){
		if(username == null){
			throw new NullPointerException("username is NULL");
		}
		return new Field(FIELD_USER,username,Field.Store.YES, Field.Index.TOKENIZED);
	}
	
	public static Field createFieldUrl(String url){
		if(url == null){
			throw new NullPointerException("url is NULL");
		}
		return new Field(FIELD_URL, url, Field.Store.YES, Field.Index.NO);
	}
	
	public static Field createFieldUrlHash(String urlHash){
		if(urlHash == null){
			throw new NullPointerException("urlHash is NULL");
		}
		return new Field(FIELD_URL_MD5, urlHash,Field.Store.YES,Field.Index.TOKENIZED);
	}
	
	public static Field createFieldNotes(String notes){
		if(notes == null){
			throw new NullPointerException("notes is NULL");
		}
		return new Field(FIELD_NOTES, notes, Field.Store.COMPRESS, Field.Index.NO);
	}
	
	public static Field createFieldTag(String tag){
		if(tag == null){
			throw new NullPointerException("tag is NULL");
		}
		return new Field(FIELD_TAG, tag, Field.Store.NO, Field.Index.TOKENIZED);
	}
	
	public static Field createFieldCreatedOn(Date date){
		if(date == null){
			throw new NullPointerException("createdOn is NULL");
		}
		String createdOn = DateTools.dateToString(date,DateTools.Resolution.DAY);
		return new Field(FIELD_CREATED_ON, createdOn, Field.Store.NO,
				Field.Index.UN_TOKENIZED);
	}

	public static Field createFieldLastUpdated(Date date){
		if(date == null){
			throw new NullPointerException("lastUpdated is NULL");			
		}
		String lastUpdated = DateTools.dateToString(date,DateTools.Resolution.DAY);
		return new Field(FIELD_LAST_UPDATED, lastUpdated, Field.Store.NO,
				Field.Index.UN_TOKENIZED);
	}
	
	public static Document createDocument(Bookmark bookmark) {
		if (bookmark == null) {
			return null;
		}
		Document doc = new Document();
		if (bookmark.getId() <= 0) {
			logger.error("DocumentCreator detects an input Bookmark ID <= 0");
			return null;
		}
		doc.add(createFieldBookmarkId(bookmark.getId()));
		
		if (bookmark.getTitle() == null || bookmark.getTitle().length() == 0) {
			logger.error("DocumentCreator detects an input "
					+ "Bookmark Title has length 0. BookmarkId="
					+ bookmark.getId());
			return null;
		}
		doc.add(createFieldTitle(bookmark.getTitle()));
		doc.add(createFieldText(bookmark.getTitle()));

		
		// user:
		if (bookmark.getUser() == null
				|| bookmark.getUser().getUsername() == null) {
			logger.error("DocumentCreator detects an input "
					+ "Bookmark Username == null. BookmarkId="
					+ bookmark.getId());
			return null;
		}
		doc.add(createFieldUser(bookmark.getUser().getUsername()));
		
		if (bookmark.getLink() == null || bookmark.getLink().getUrl() == null) {
			logger.error("DocumentCreator detects an input "
					+ "Bookmark URL == null. BookmarkId=" + bookmark.getId());
			return null;
		}
		doc.add(createFieldUrl(bookmark.getLink().getUrl()));		
		
		// url: & urlMD5:
		if (bookmark.getLink() == null || bookmark.getLink().getUrlHash() == null) {
			logger.error("DocumentCreator detects an input "
					+ "Bookmark URL Hash == null. BookmarkId=" + bookmark.getId());
			return null;
		}
		doc.add(createFieldUrlHash(bookmark.getLink().getUrlHash()));
		
		// notes:
		String notes = extractText(bookmark.getNotes());
		if (notes != null) {
			doc.add(createFieldNotes(bookmark.getNotes()));
			doc.add(createFieldText(bookmark.getNotes()));
		}
		// tag:
		List<String> tagList = bookmark.getTagList();
		for (String tag : tagList) {
			doc.add(createFieldTag(tag));
			doc.add(createFieldText(tag));
		}
		// createdOn:
		if (bookmark.getCreatedOn() == null) {
			logger.error("DocumentCreator detects an input "
					+ "Bookmark createdOn == null. BookmarkId="
					+ bookmark.getId());
			return null;
		}
		doc.add(createFieldCreatedOn(bookmark.getCreatedOn()));
		
		// lastUpdated:
		if (bookmark.getLastUpdated() == null) {
			logger.error("DocumentCreator detects an input "
					+ "Bookmark lastUpdated == null. BookmarkId="
					+ bookmark.getId());
			return null;
		}		
		doc.add(createFieldLastUpdated(bookmark.getLastUpdated()));		
		return doc;
	}

	public static Analyzer createDocumentAnalyzer(){
		PerFieldAnalyzerWrapper wrapper = new PerFieldAnalyzerWrapper(new StandardAnalyzer());
		wrapper.addAnalyzer(DocumentCreator.FIELD_BOOKMARK_ID,new KeywordAnalyzer());
		wrapper.addAnalyzer(DocumentCreator.FIELD_URL_MD5,new KeywordAnalyzer());
		wrapper.addAnalyzer(DocumentCreator.FIELD_TAG, new KeywordAnalyzer());
		wrapper.addAnalyzer(DocumentCreator.FIELD_USER,new KeywordAnalyzer());
		return wrapper;
	}
	
	private static String extractText(String htmlContent) {
		if (htmlContent != null && htmlContent.length() > 0) {
			Source source = new Source(htmlContent);
			TextExtractor extractor = new TextExtractor(source);
			extractor.setConvertNonBreakingSpaces(true);
			extractor.setExcludeNonHTMLElements(false);
			extractor.setIncludeAttributes(false);
			String output = extractor.toString();
			if (output != null && output.length() > 0) {
				return output;
			}
		}
		return null;
	}

}

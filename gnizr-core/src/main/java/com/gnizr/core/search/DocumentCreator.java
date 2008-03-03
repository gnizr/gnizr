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
import java.util.List;

import org.apache.log4j.Logger;
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
	 * of the same URL is bookmarked, a leading document is one document that
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

	public static Document createDocument(Bookmark bookmark) {
		if (bookmark == null) {
			return null;
		}
		Document doc = new Document();
		if (bookmark.getId() <= 0) {
			logger.error("DocumentCreator detects an input Bookmark ID <= 0");
			return null;
		}
		doc.add(new Field(FIELD_BOOKMARK_ID, String.valueOf(bookmark.getId()),
				Field.Store.YES, Field.Index.UN_TOKENIZED));
		if (bookmark.getTitle() == null || bookmark.getTitle().length() == 0) {
			logger.error("DocumentCreator detects an input "
					+ "Bookmark Title has length 0. BookmarkId="
					+ bookmark.getId());
			return null;
		}
		doc.add(new Field(FIELD_TITLE, bookmark.getTitle(), Field.Store.YES,
				Field.Index.TOKENIZED));
		if (bookmark.getLink() == null || bookmark.getLink().getUrl() == null) {
			logger.error("DocumentCreator detects an input "
					+ "Bookmark URL == null. BookmarkId=" + bookmark.getId());
			return null;
		}
		doc.add(new Field(FIELD_URL, bookmark.getLink().getUrl(),
				Field.Store.YES, Field.Index.NO));
		if (bookmark.getUser() == null
				|| bookmark.getUser().getUsername() == null) {
			logger.error("DocumentCreator detects an input "
					+ "Bookmark Username == null. BookmarkId="
					+ bookmark.getId());
			return null;
		}
		if (bookmark.getLink() == null || bookmark.getLink().getUrlHash() == null) {
			logger.error("DocumentCreator detects an input "
					+ "Bookmark URL Hash == null. BookmarkId=" + bookmark.getId());
			return null;
		}
		doc.add(new Field(FIELD_URL_MD5, bookmark.getLink().getUrlHash(),
				Field.Store.YES,Field.Index.UN_TOKENIZED));
		
		doc.add(new Field(FIELD_USER, bookmark.getUser().getUsername(),
				Field.Store.YES, Field.Index.UN_TOKENIZED));
		String notes = extractText(bookmark.getNotes());
		if (notes != null) {
			doc.add(new Field(FIELD_NOTES, notes, Field.Store.COMPRESS,
					Field.Index.TOKENIZED));
		}
		List<String> tagList = bookmark.getTagList();
		for (String tag : tagList) {
			doc.add(new Field(FIELD_TAG, tag, Field.Store.NO,
					Field.Index.UN_TOKENIZED));
		}
		if (bookmark.getCreatedOn() == null) {
			logger.error("DocumentCreator detects an input "
					+ "Bookmark createdOn == null. BookmarkId="
					+ bookmark.getId());
			return null;
		}
		String createdOn = DateTools.dateToString(bookmark.getCreatedOn(),
				DateTools.Resolution.DAY);
		if (bookmark.getLastUpdated() == null) {
			logger.error("DocumentCreator detects an input "
					+ "Bookmark lastUpdated == null. BookmarkId="
					+ bookmark.getId());
			return null;
		}
		String lastUpdated = DateTools.dateToString(bookmark.getLastUpdated(),
				DateTools.Resolution.DAY);
		doc.add(new Field(FIELD_CREATED_ON, createdOn, Field.Store.NO,
				Field.Index.UN_TOKENIZED));
		doc.add(new Field(FIELD_LAST_UPDATED, lastUpdated, Field.Store.NO,
				Field.Index.UN_TOKENIZED));
		return doc;
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

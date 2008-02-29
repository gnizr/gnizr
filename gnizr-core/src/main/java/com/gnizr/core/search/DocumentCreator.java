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
	public static final String FIELD_TITLE = "title";
	public static final String FIELD_NOTES = "notes";
	public static final String FIELD_TAG = "tag";
	public static final String FIELD_USER = "user";
	public static final String FIELD_CREATED_ON = "createdOn";
	public static final String FIELD_LAST_UPDATED = "lastUpdated";

	/**
	 * 
	 */
	private static final long serialVersionUID = 980901600301331248L;

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
							+ "Bookmark URL == null. BookmarkId="
							+ bookmark.getId());
			return null;
		}
		doc.add(new Field(FIELD_URL, bookmark.getLink().getUrl(),
				Field.Store.YES, Field.Index.NO));
		if(bookmark.getUser() == null || bookmark.getUser().getUsername() == null){
			logger.error("DocumentCreator detects an input "
					+ "Bookmark Username == null. BookmarkId="+bookmark.getId());
			return null;
		}
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
		if(bookmark.getCreatedOn() == null){
			logger.error("DocumentCreator detects an input " + 
					"Bookmark createdOn == null. BookmarkId=" + bookmark.getId());
			return null;
		}
		String createdOn = DateTools.dateToString(bookmark.getCreatedOn(),
				DateTools.Resolution.DAY);
		if(bookmark.getLastUpdated() == null){
			logger.error("DocumentCreator detects an input " + 
					"Bookmark lastUpdated == null. BookmarkId=" + bookmark.getId());
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

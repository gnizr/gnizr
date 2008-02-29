package com.gnizr.core.search;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;

import org.apache.lucene.document.Document;

import com.gnizr.db.dao.Bookmark;
import com.gnizr.db.dao.Link;
import com.gnizr.db.dao.User;

public class TestDocumentCreator extends TestCase {

	
	private Bookmark bmark1;
	
	@Override
	protected void setUp() throws Exception {		
		super.setUp();
		bmark1 = new Bookmark(101);
		bmark1.setUser(new User("jsmith"));
		bmark1.setTitle("Title of a Bookmark");
		bmark1.setLink(new Link("http://example.org/foo1.html"));
		bmark1.setTags("tag1 tag2 foo.bar machine:tag=233");
		bmark1.setNotes("Some <b>notes</b> of the bookmark <i>not wellformed");
		
		SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date createdOn = dFormat.parse("2008-01-23");
		Date lastUpdated = dFormat.parse("2008-02-11");
		
		bmark1.setCreatedOn(createdOn);
		bmark1.setLastUpdated(lastUpdated);
	}

	public void testCreateDocument() throws Exception {
		Document doc1 = DocumentCreator.createDocument(bmark1);
		assertNotNull(doc1);
		
		String id = doc1.get(DocumentCreator.FIELD_BOOKMARK_ID);
		assertEquals("101",id);

		String username = doc1.get(DocumentCreator.FIELD_USER);
		assertEquals("jsmith",username);
		
		String title = doc1.get(DocumentCreator.FIELD_TITLE);
		assertEquals("Title of a Bookmark",title);
	
		String link = doc1.get(DocumentCreator.FIELD_URL);
		assertEquals("http://example.org/foo1.html",link);
		
		String[] tags = doc1.getValues(DocumentCreator.FIELD_TAG);
		assertEquals(4,tags.length);
		List<String> tagList = Arrays.asList(tags);
		assertTrue(tagList.contains("tag1"));
		assertTrue(tagList.contains("tag2"));
		assertTrue(tagList.contains("foo.bar"));
		assertTrue(tagList.contains("machine:tag=233"));
		
		String cDate = doc1.get(DocumentCreator.FIELD_CREATED_ON);
		String lDate = doc1.get(DocumentCreator.FIELD_LAST_UPDATED);
		assertEquals("20080123",cDate);
		assertEquals("20080211",lDate);
	}

}

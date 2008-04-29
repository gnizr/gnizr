package com.gnizr.core.search;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

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
		Link ln = new Link("http://example.org/foo1.html");
		ln.setUrlHash("123456789abcdefg");
		bmark1.setLink(ln);
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
		
		String md5 = doc1.get(DocumentCreator.FIELD_URL_MD5);
		assertEquals("123456789abcdefg",md5);
		
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
		
		String txt = doc1.get(DocumentCreator.FIELD_TEXT);
		assertNotNull(txt);
	}

	public void testRemoveIndexTypeLead() throws Exception{
		Document doc1 = new Document();
		doc1.add(new Field(DocumentCreator.FIELD_BOOKMARK_ID,"100",Field.Store.YES,Field.Index.UN_TOKENIZED));
		doc1.add(new Field(DocumentCreator.FIELD_INDEX_TYPE,DocumentCreator.INDEX_TYPE_LEAD,Field.Store.NO,Field.Index.UN_TOKENIZED));
		doc1.add(new Field(DocumentCreator.FIELD_INDEX_TYPE,"other-type",Field.Store.NO,Field.Index.UN_TOKENIZED));
		doc1 = DocumentCreator.removeIndexTypeLead(doc1);
		Field[] fields = doc1.getFields(DocumentCreator.FIELD_INDEX_TYPE);
		assertEquals(1,fields.length);
		assertEquals("other-type",fields[0].stringValue());
		fields = doc1.getFields(DocumentCreator.FIELD_BOOKMARK_ID);
		assertEquals(1,fields.length);	
	}
	
	public void testAddIndexTypeLead() throws Exception {
		Document doc1 = new Document();
		doc1.add(new Field(DocumentCreator.FIELD_BOOKMARK_ID,"100",Field.Store.YES,Field.Index.UN_TOKENIZED));
		doc1 = DocumentCreator.addIndexTypeLead(doc1);
		Field[] fields = doc1.getFields(DocumentCreator.FIELD_INDEX_TYPE);
		assertEquals(1,fields.length);
		assertEquals(DocumentCreator.INDEX_TYPE_LEAD,fields[0].stringValue());	
	}
	
}

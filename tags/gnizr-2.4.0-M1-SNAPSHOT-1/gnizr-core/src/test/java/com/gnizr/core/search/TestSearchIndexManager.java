package com.gnizr.core.search;

import junit.framework.TestCase;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;

public class TestSearchIndexManager extends TestCase {

	private IndexStoreProfile profile;
	private SearchIndexManager manager;
	
	private Document doc1;
	private Document doc2;
	private String urlHash;
	
	protected void setUp() throws Exception {
		super.setUp();
		profile = new IndexStoreProfile();
		profile.setDirectoryPath("target/search-data");
		manager = new SearchIndexManager();
		manager.setProfile(profile);
		manager.init();
		
		urlHash = "a1234567890";

		doc1 = new Document();
		doc1.add(new Field(DocumentCreator.FIELD_URL_MD5,urlHash,Field.Store.YES,Field.Index.UN_TOKENIZED));
		doc1.add(new Field(DocumentCreator.FIELD_BOOKMARK_ID,"111",Field.Store.YES,Field.Index.UN_TOKENIZED));
		
		doc2 = new Document();
		doc2.add(new Field(DocumentCreator.FIELD_URL_MD5,urlHash,Field.Store.YES,Field.Index.UN_TOKENIZED));
		doc2.add(new Field(DocumentCreator.FIELD_BOOKMARK_ID,"112",Field.Store.YES,Field.Index.UN_TOKENIZED));
		doc2 = DocumentCreator.addIndexTypeLead(doc2);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testDestroy() throws Exception {
		assertTrue(manager.isActive());
		manager.destroy();
		Thread.sleep(5000);
		assertFalse(manager.isActive());
		
	}
	
	private void reloadIndexDB() throws Exception{
		IndexWriter writer = new IndexWriter(profile.getDirectoryPath(),new StandardAnalyzer(),true);
		writer.addDocument(doc1);
		writer.addDocument(doc2);
		writer.close();
	}

	public void testFindLeadDocument() throws Exception{
		reloadIndexDB();
		Document d = manager.findLeadDocument(urlHash);	
		assertNotNull(d);	
		String v = d.get(DocumentCreator.FIELD_INDEX_TYPE);
		assertEquals(DocumentCreator.INDEX_TYPE_LEAD,v);
		String id = d.get(DocumentCreator.FIELD_BOOKMARK_ID);
		assertEquals("112",id);
		testDestroy();
	}
	
	public void testFindNonLeadDocument() throws Exception{
		reloadIndexDB();
		Document d = manager.findNonLeadDocument(urlHash);	
		assertNotNull(d);	
		String v = d.get(DocumentCreator.FIELD_INDEX_TYPE);
		assertNull(v);
		String id = d.get(DocumentCreator.FIELD_BOOKMARK_ID);
		assertEquals("111",id);
		testDestroy();
	}
	
	
	public void testUpdateIndex() throws Exception {
		reloadIndexDB();
		doc2.add(new Field(DocumentCreator.FIELD_TITLE,"some title",Field.Store.YES,Field.Index.TOKENIZED));
		manager.updateIndex(doc2);
		Thread.sleep(5000);
		Document leadDoc = manager.findLeadDocument(urlHash);
		assertEquals(leadDoc.get(DocumentCreator.FIELD_TITLE),"some title");
		testDestroy();
	}

	public void testDeleteIndex() throws Exception {
		reloadIndexDB();		
		manager.deleteIndex(doc2);
		Thread.sleep(5000);
		// after deleting the current lead doc, the non-lead doc will
		// be promoted to be the lead doc.
		Document leadDoc = manager.findLeadDocument(urlHash);
		assertEquals(leadDoc.get(DocumentCreator.FIELD_BOOKMARK_ID),"111");
		testDestroy();
	}

	public void testAddIndex() throws Exception {
		reloadIndexDB();
		String doc3UrlHash = "999aaa2222";
		Document doc3 = new Document();
		doc3.add(new Field(DocumentCreator.FIELD_URL_MD5,doc3UrlHash,Field.Store.YES,Field.Index.UN_TOKENIZED));
		doc3.add(new Field(DocumentCreator.FIELD_BOOKMARK_ID,"2000",Field.Store.YES,Field.Index.UN_TOKENIZED));
		manager.addIndex(doc3);
		Thread.sleep(5000);
		Document leadDoc1 = manager.findLeadDocument(urlHash);
		Document leadDoc2 = manager.findLeadDocument(doc3UrlHash);
		assertEquals("112",leadDoc1.get(DocumentCreator.FIELD_BOOKMARK_ID));
		assertEquals("2000",leadDoc2.get(DocumentCreator.FIELD_BOOKMARK_ID));
		testDestroy();
	}

}

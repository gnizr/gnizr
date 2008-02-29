package com.gnizr.core.search;

import junit.framework.TestCase;

public class TestSearchIndexManager extends TestCase {

	private IndexStoreProfile profile;
	private SearchIndexManager manager;
	
	protected void setUp() throws Exception {
		super.setUp();
		profile = new IndexStoreProfile();
		profile.setDirectoryPath("search-data");
		manager = new SearchIndexManager();
		manager.setProfile(profile);
		manager.init();
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

	public void testUpdateIndex() {
		fail("Not yet implemented");
	}

	public void testAddIndex() {
		fail("Not yet implemented");
	}

	public void testDeleteIndex() {
		fail("Not yet implemented");
	}

}

package com.gnizr.core.util;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import com.gnizr.core.GnizrCoreTestBase;
import com.gnizr.core.search.BookmarkDoc;
import com.gnizr.db.dao.Bookmark;
import com.gnizr.db.dao.DaoResult;
import com.gnizr.db.dao.User;
import com.gnizr.db.dao.bookmark.BookmarkDao;
import com.sun.syndication.feed.module.opensearch.OpenSearchModule;
import com.sun.syndication.feed.synd.SyndCategory;
import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.SyndFeedOutput;

public class TestSyndFeedFactory extends GnizrCoreTestBase {
	
	private List<Bookmark> bmarks = null;
	private String title = "TITLE STRING FOOBAR";
	private String link = "http://foo.com/gnizr";
	private Date pubDate = GnizrDaoUtil.getNow();
	private String feedUri = "urn:x-id-101";
	private String author = "gnizr";
	private int itemsPerPage = 10;
	private int startIndex = 0;
	private int totalResult = -1;
	private String searchDescriptionUrl = "http://foo.com/gnizr/search-description.xml";
	
	protected void setUp() throws Exception {
		super.setUp();
		BookmarkDao bmarkDao = getGnizrDao().getBookmarkDao();
		DaoResult<Bookmark> result = bmarkDao.pageBookmarks(new User(1),0,10);		
		bmarks = result.getResult();
		assertFalse(bmarks.isEmpty());
		totalResult = result.getSize();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@Override
	protected IDataSet getDataSet() throws Exception {
		return new FlatXmlDataSet(TestSyndFeedFactory.class.getResourceAsStream("/TestSyndFeedFactory-input.xml"));
	}

	@SuppressWarnings("unchecked")
	public void testGetFeed() throws Exception{				
		SyndFeed syndFeed = SyndFeedFactory.create(bmarks,author,title,link,pubDate,feedUri);
		
		assertEquals(author,syndFeed.getAuthor());
		assertEquals(title,syndFeed.getTitle());
		assertEquals(pubDate,syndFeed.getPublishedDate());
		assertEquals(feedUri,syndFeed.getUri());
		assertEquals(link,syndFeed.getLink());
		
		List<SyndEntry> entries = syndFeed.getEntries();
		assertEquals(1,entries.size());
		
		SyndEntry entry = entries.get(0);
		assertEquals("http://zirr.us/",entry.getLink());
		assertEquals("zirrus",entry.getTitle());
		assertEquals("gnizr",entry.getAuthor());
		SyndContent cont = entry.getDescription();
		assertNotNull(cont);
		assertEquals("website",cont.getValue());
		assertEquals("text/html",cont.getType());
		
		List<SyndCategory> cats = entry.getCategories();
		assertFalse(cats.isEmpty());
		SyndCategory cat = cats.get(0);
		assertEquals("cnn",cat.getName());
	}
	
	@SuppressWarnings("unchecked")
	public void testAddOpenSearchModule() throws Exception{
		SyndFeed syndFeed = SyndFeedFactory.create(bmarks,author,title,link,pubDate,feedUri);
		assertNotNull(syndFeed);
		
		syndFeed = SyndFeedFactory.addOpenSearchModule(syndFeed, itemsPerPage, 
				startIndex, totalResult, searchDescriptionUrl);
	
		OpenSearchModule osm = (OpenSearchModule) syndFeed.getModule(OpenSearchModule.URI);
		assertEquals(itemsPerPage,osm.getItemsPerPage());
		assertEquals(totalResult,osm.getTotalResults());
		assertEquals(startIndex,osm.getStartIndex());
		assertEquals(searchDescriptionUrl,osm.getLink().getHref());
	}
	
	public void testCreateFromBookmarkDoc() throws Exception {
		List<BookmarkDoc> docs = new ArrayList<BookmarkDoc>();
		BookmarkDoc doc1 = new BookmarkDoc();
		doc1.setTitle("bookmark 1 title");
		doc1.setNotes("<p>some html description</p>");
		doc1.setBookmarkId(123);
		doc1.setUrl("http://foo.com/doc?id=102&p=334");
		doc1.setUsername("johna");
		doc1.setUrlHash("383774jd9383232");
		docs.add(doc1);
		
		String feedUrl = "http://foo.com/feed?id=1&p=3";
		Date pubDate = GregorianCalendar.getInstance().getTime();
		String title = "a feed of bookmark docs";
		String feedAuthor = "john s";
		
		SyndFeed syndFeed = SyndFeedFactory.createFromBookmarkDoc(docs,feedAuthor, title, feedUrl, pubDate, null);
		assertNotNull(syndFeed);
		assertEquals(1,syndFeed.getEntries().size());
	
		syndFeed.setFeedType("atom_1.0");
		
		SyndFeedOutput output = new SyndFeedOutput();
		String feedData = output.outputString(syndFeed);		
		
		StringReader sreader = new StringReader(feedData);
		SyndFeedInput input = new SyndFeedInput(true);
		SyndFeed copyFeed = input.build(sreader);
		assertNotNull(copyFeed);
	}
	

	
}

#### Version 2.3.0 Final (27 March 2008) ####

Changelog
  * [Issue 42](http://code.google.com/p/gnizr/issues/detail?id=42) : Error accessing /home of user who username contains '.' (fixed, verified)
  * [Issue 47](http://code.google.com/p/gnizr/issues/detail?id=47):  	ConcurrentModificationException on logout using RC1 (fixed, need testing)
  * Fixed HTML character escape issues in the generation of RSS feed.

#### Version 2.3.0-RC2 (14 March 2008) ####

Changelog
  * [Issue 45](http://code.google.com/p/gnizr/issues/detail?id=45): Dangling thread is detected after gnizr is shutdown (fixed, need testing)
  * [Issue 46](http://code.google.com/p/gnizr/issues/detail?id=46): JUnit tests failed when building gnizr-robot package (fixed, verified)
  * Updated doc that describes the [User Authentication](DevUserManageAndAuth.md) implementation
  * Updated gnizr Java API documentation

#### Version 2.3.0-RC1 (11 February 2008) ####

Changelog
  * [Issue 35](http://code.google.com/p/gnizr/issues/detail?id=35): OpenSearch result page shows a non-functional result tile separator (fixed, need testing)
  * [Issue 41](http://code.google.com/p/gnizr/issues/detail?id=41): Potential XSS attach via OpenSearch query (fixed, need testing)
  * Documented how to enable gnizr to support UTF-8 in the HowToInstall wiki page.

#### Version 2.3.0-M4 (24 January 2008) ####

Changelog
  * Moved shared Freemarker template files from `gnzir-web` to `gnizr-webapp`
    * SVN [Revision 547](https://code.google.com/p/gnizr/source/detail?r=547), 12/27/2007
  * Fixed hyberlinks to user RSS and RDF doc.
    * SVN [Revision 560](https://code.google.com/p/gnizr/source/detail?r=560), 1/24/2008
  * Updated copyright text in the page footer.
    * SVN [Revision 560](https://code.google.com/p/gnizr/source/detail?r=560), 1/24/2008
  * Added a hyberlink to Gnizr Google Code page in the page footer.
    * SVN [Revision 560](https://code.google.com/p/gnizr/source/detail?r=560), 1/24/2008
  * [Issue 33](http://code.google.com/p/gnizr/issues/detail?id=33): bookmarklet doesn't maintain state after required authentication (fixed, need testing)
  * [Issue 36](http://code.google.com/p/gnizr/issues/detail?id=36): Calling delete in the bookmarklet pop-up doesn't close the window (fixed, need testing)
  * [Issue 37](http://code.google.com/p/gnizr/issues/detail?id=37): Add bookmark operation doesn't automatically update the properties of an existing bookmark (fixed, need testing)
  * [Issue 38](http://code.google.com/p/gnizr/issues/detail?id=38): The requested resource (/gnizr/home) was not found (fixed, need testing)

#### Version 2.3.0-M3 (26 December 2007) ####

Changelog
  * POM files changed; renamed the `groupId` of all sub-project modules to `com.gnizr`
    * SVN [Revision 464](https://code.google.com/p/gnizr/source/detail?r=464), 12/18/2007
  * Renamed Java package name `com.gnizr.core.web` to `com.gnizr.web`
    * SVN [Revision 465](https://code.google.com/p/gnizr/source/detail?r=465)-467, 12/18/2007
  * [Issue 30](http://code.google.com/p/gnizr/issues/detail?id=30): Preview Pop-up can't be resized in IE (fixed, need testing)
  * [Issue 31](http://code.google.com/p/gnizr/issues/detail?id=31): User 'gnizr' tag cloud missing in the Edit Bookmark page (fixed, need testing)
  * [Issue 32](http://code.google.com/p/gnizr/issues/detail?id=32): Can't save bookmarks using bookmarklet (fixed, need testing)


#### Version 2.3.0-M2 (14 December 2007) ####

Download
  * Binrary: [gnizr-2.3.0-M2.zip](http://gnizr.googlecode.com/files/gnizr-2.3.0-M2.zip)
  * Source: http://gnizr.googlecode.com/svn/tags/gnizr-2.3.0-M2/

Changelog
  * [Issue 14](http://code.google.com/p/gnizr/issues/detail?id=14): Help page generates incorrect link URL for bookmarklets (fixed, tested)
  * [Issue 22](http://code.google.com/p/gnizr/issues/detail?id=22): Tagging auto-complete doesn't work if the text contains non-word char (fixed, tested)
  * [Issue 23](http://code.google.com/p/gnizr/issues/detail?id=23): Allow users to delete tag with 0 usage freq (fixed, tested)
  * [Issue 24](http://code.google.com/p/gnizr/issues/detail?id=24): Bookmarklet window pop-up is not focused (fixed, tested)
  * [Issue 25](http://code.google.com/p/gnizr/issues/detail?id=25): OpenSearch action redirects to a page that doesn't exist (fixed, tested)
  * [Issue 26](http://code.google.com/p/gnizr/issues/detail?id=26): OpenSearch page always starts search with default search engine selection (fixed, tested)
  * [Issue 28](http://code.google.com/p/gnizr/issues/detail?id=28): Can't save search results in the OpenSearch page (fixed, tested)
  * [Issue 29](http://code.google.com/p/gnizr/issues/detail?id=29): Provide search term suggestion based on tag relations (fixed, tested)
  * Re-arranged the java packaging: SVN [Revision 349](https://code.google.com/p/gnizr/source/detail?r=349)-366, 12/04/2007.
    * Renamed `com.gnizr.db.GnizrBasicDataSource` to `com.gnizr.db.dao.GnizrBasicDataSource` -- affects `gnizr-config.xml`

#### Version 2.3.0-M1 (20 November 2007) ####

Download
  * Binrary: [gnizr-2.3.0-M1.zip](http://gnizr.googlecode.com/files/gnizr-2.3.0-M1.zip)
  * Source: http://gnizr.googlecode.com/svn/tags/gnizr-2.3.0-M1/

Changelog
  * [Issue 5](http://code.google.com/p/gnizr/issues/detail?id=5): Provide clickable tag cloud in the Edit Tag Relations page  (fixed, tested)
  * [Issue 6](http://code.google.com/p/gnizr/issues/detail?id=6): Renaming tags to "gn:geonames=" don't trigger geocoding (fixed, tested)
  * [Issue 8](http://code.google.com/p/gnizr/issues/detail?id=8): Allow users to add 'gn:geonames=' without typing the whole string every time (fixed, tested)
  * [Issue 9](http://code.google.com/p/gnizr/issues/detail?id=9): Post-to-gnizr bookmarklet should open 'Edit Bookmark' in a new window (fixed, tested)
  * [Issue 10](http://code.google.com/p/gnizr/issues/detail?id=10): Provide auto-complete for editing bookmark tags
  * [Issue 14](http://code.google.com/p/gnizr/issues/detail?id=14): Help page generates incorrect link URL for bookmarklets (fixed, need testing)
  * [Issue 15](http://code.google.com/p/gnizr/issues/detail?id=15): Provide OpenSearch support
  * Link "define placemarks" in the Edit Bookmark page has been renamed to "edit placemarks"
  * User changing bookmark tags via keyboard inputs in the "Edit Bookmark" page is made more responsive.
  * Changed version number string in `macro-lib.ftl` to `2.3.0-M1`
  * Merged changes from the 2.2 branch: [R167](https://code.google.com/p/gnizr/source/detail?r=167)-[R235](https://code.google.com/p/gnizr/source/detail?r=235) (at [R239](https://code.google.com/p/gnizr/source/detail?r=239))
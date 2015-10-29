#### Version 2.4.0-RC1 (in progress) ####
  * [Issue 67](http://code.google.com/p/gnizr/issues/detail?id=67): Warn db overwrite when running the install script (fixed, need testing)
  * [Issue 68](http://code.google.com/p/gnizr/issues/detail?id=68): Publish RSS of bookmarks that are recently saved in a given folder (fixed, need testing)
  * [Issue 69](http://code.google.com/p/gnizr/issues/detail?id=69): Publish RSS of bookmarks of a specific tag that are recently saved in a given folder (fixed, need testing)
  * [Issue 70](http://code.google.com/p/gnizr/issues/detail?id=70): Publish RSS of bookmarks that are recently saved in the bookmark archive stream (fixed, need testing)
  * [Issue 71](http://code.google.com/p/gnizr/issues/detail?id=71): Publish RSS of bookmarks of a specific tag that are recently saved in the bookmark archive stream (fixed, need testing)


#### Version 2.4.0-M4 (27 May 2008) ####
  * [Issue 56](http://code.google.com/p/gnizr/issues/detail?id=56): Prevent users from saving bookmark title with HTML tags (fixed, tested)
  * [Issue 64](http://code.google.com/p/gnizr/issues/detail?id=64): Bookmarklet failed to work in IE6 (fixed, tested)
  * Renamed "RSS Subscription" to "link collect"; improved user instructions.
  * Created `FormatUtil` methods to tidy and remove unsafe HTML tags.
  * Editing User folder description prohibits the use of HTML tags.
  * Editing bookmark notes prohibits the use of unsafe HTML tags (e.g., `script`, `iframe`);
  * Editing bookmark geo-markers prohibits the use of HTML tags.
  * Upgraded to Freemarker 2.3.8.
  * Added more OpenSearch engines (del.icio.us, nytimes, google blog search, yahoo news and wikipedia).
  * DB added: TagAssertionDao.findRelatedTags
  * Users can export saved bookmarks to a file `bookmarks.html`.


#### Version 2.4.0-M3 (2 May 2008) ####

Changelog

  * [Issue 58](http://code.google.com/p/gnizr/issues/detail?id=58) : Allow users to recover their password (fixed, need testing)
  * [Issue 60](http://code.google.com/p/gnizr/issues/detail?id=60): HTTP session is not cleared after the user logout (fixed, verified)
  * [Issue 61](http://code.google.com/p/gnizr/issues/detail?id=61): Verify user email when creating the user account
  * [Issue 63](http://code.google.com/p/gnizr/issues/detail?id=63): Allow the administrator to explicitly approve new user registration
  * [Issue 64](http://code.google.com/p/gnizr/issues/detail?id=64): Bookmarklet failed to work in IE6 (in progress, need testing)
  * Changed `siteContactEmail` configuration property from OPTIONAL to REQUIRED.
  * Changed `siteContactEmail` default value to `help@localhost`.
  * Optimized the algorithm and reduced the amount of time required for rebuilding search index.


#### Version 2.4.0-M2 (18 April 2008) ####

Changelog
  * Changed my bookmark search engine description from "Bookmarked by me" to "Bookmarked by you".
  * [Issue 49](http://code.google.com/p/gnizr/issues/detail?id=49): Provide a link for adding search plugin to Firefox and IE7 (fixed, verified)
  * [Issue 50](http://code.google.com/p/gnizr/issues/detail?id=50): Provide search suggestions as the user types in the search box (fixed, verified)
  * [Issue 51](http://code.google.com/p/gnizr/issues/detail?id=51): Re-building search index should overwrite the existing search index database (fixed, verified)
  * [Issue 52](http://code.google.com/p/gnizr/issues/detail?id=52): Avoid loading the full HTML bookmark description in the HOME page (fixed, verified)
  * [Issue 53](http://code.google.com/p/gnizr/issues/detail?id=53): Use of 'registerationPolicy' in gnizr-config.xml is broken (fixed, need testing)
  * [Issue 54](http://code.google.com/p/gnizr/issues/detail?id=54): Re-redirect new users to a more informative front page. (fixed, need testing)
  * [Issue 56](http://code.google.com/p/gnizr/issues/detail?id=56): Prevent users from saving bookmark title with HTML tags (partially fixed)
  * [Issue 3](http://code.google.com/p/gnizr/issues/detail?id=3): Ability to easily search community tags. (partially fixed)
  * [Issue 44](http://code.google.com/p/gnizr/issues/detail?id=44): OutOfMemory exception when search in community bookmarks (fixed, need testing)
  * Improved the UI for listing user bookmarks
  * Text descriptions have been added to various "settings" functions
  * Updated the UI of "login" and "registration" page.
  * Added new configuration property: `siteContactEmail`
  * Changed configuration property: `directoryPath` to `searchIndexDirectory`

#### Version 2.4.0-M1 (04 April 2008) ####

Changelog
  * [Issue 35](http://code.google.com/p/gnizr/issues/detail?id=35): OpenSearch result page shows a non-functional result tile separator (fixed, need testing)
  * [Issue 41](http://code.google.com/p/gnizr/issues/detail?id=41): Potential XSS attach via OpenSearch query (fixed, need testing)
  * Changed the location of `log4j.properties` -- moved from `WEB-INF` to `WEB-INF/classes`
    * SVN Revision: 579
  * Merged `OpenSearchDirectory` configuration into `gnizr-config.xml`
    * SVN Revision: 583
  * Fixed !BookmarkDBDao.pageBookmarks(Link,int,int) to instantiate the full result object.
  * Deprecated LinkManager.getLinkHistory and created pageListHistory
  * Changed the URI format for identifying bookmark in an OpenSearch result feed
  * Replaced SQL-driven search with Lucene search.
  * Combined all searches under a single OpenSearch function.
  * Replaced the old Link History page with a new View Bookmark Detail page.
  * Enabled Firefox to discover gnizr opensearch service. Gnizr search engine can be added to the Browser search bar.
  * Implemented the use of `gn:icon` machine tag and its Machine Tag Helper UI
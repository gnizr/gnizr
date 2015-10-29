[![](http://farm3.static.flickr.com/2220/2003281435_b6f10a4ff1_o.png)](http://www.flickr.com/photos/14804582@N08/2003281435/)

gnizr™ (gə-nīzər) is an open source application for **social bookmarking** and **web mashup**. It is easy to use gnizr to create a personalized [del.icio.us](http://del.icio.us)-like portal for a group of friends and colleagues to store, classify and share information, and to mash-it-up with information about location. **It's free**.

[Image Matters LLC](http://www.imagemattersllc.com) contributed gnizr to the Open Source community, and continues to support its development as part of its [gnizr Enterprise](http://www.imagemattersllc.com) software.

| **NEWS** (10/09/2008): We need your help .. [read this](http://gnizr.blogspot.com/)|
|:-----------------------------------------------------------------------------------|
| **NEWS** (07/15/2008): [Gnizr 2.4.0-RC1](http://gnizr.blogspot.com/2008/07/gnizr-240-rc1-release.html) is now available for download. Are you doing an upgrade? [Read this](http://gnizr.blogspot.com/2008/05/upgrade-issue-setting-user-account.html). |
| **NEWS** (05/09/2008):  Welcome to [gnizr.com](http://gnizr.blogspot.com/2008/05/welcome-to-gnizrcom.html) -- a free gnizr service.|
| **NEWS** (03/27/2008): [Gnizr 2.3.0 Final Release](http://gnizr.blogspot.com/2008/03/gnizr-230-final-release.html) is now available for download. |

## Feature Highlights ##
  * Archive saved bookmarks and organize bookmarks using tags and folders.
  * Edit notes using WYSIWYG bookmark editor.
  * Assign geographical location values to bookmarks and view them on a map.
  * Define relationships between bookmark tags -- broader, narrower and member-of.
  * Tag bookmarks using Machine Tags.
  * Super fast bookmark search using [Lucene](http://lucene.apache.org/java/docs/index.html).
  * Search bookmarks and other sites simultaneously using [OpenSearch](http://en.wikipedia.org/wiki/OpenSearch).
  * View bookmarks in Clustermap and Timeline.
  * Import new bookmarks from user-defined RSS subscriptions -- RSS, Atom and GeoRSS.
  * Create new application behaviors using gnizr API. For example:
    * Add modules to support custom Machine Tags;
    * Add listeners to handle bookmark change events;
    * Develop custom RSS crawlers to perform automated bookmark imports; and
    * Create third-party mashups from data published by gnizr (RDF, RSS and JSON).

## Screenshots ##
  * See gnizr [screenshots on flickr](http://www.flickr.com/photos/14804582@N08/sets/72157602813163311/).

## Technology ##
  * Built-on Java Servlet technology ([WebWork](http://www.opensymphony.com/webwork) + [Spring](http://www.springframework.org/))
  * Front-end pages are written using the [Freemarker](http://freemarker.org/) template language
  * [MySQL](http://www.mysql.com) backed database. All database operations are implemented as stored procedures.
  * Semantic tag relationships are modeled based on the [SKOS](http://www.w3.org/2004/02/skos/) ontology.
  * Supports various metadata output format: GeoRSS, RDF ([SIOC](http://sioc-project.org/) + [Tag Ontology](http://www.holygoat.co.uk/projects/tags/)), RSS, Timeline XML and Clustermap XML.
  * Geocoding machine tag uses [Geonames](http://www.geonames.org) web service.
  * Full-text search engine built-on Apache [Lucene](http://lucene.apache.org/java/docs/index.html) API.
  * Open search interface and Firefox search plugin built-on the [OpenSearch](http://en.wikipedia.org/wiki/OpenSearch) standards.

## Credit ##
Our [Atlassian FishEye](http://fisheye2.cenqua.com/browse/gnizr) hosting is kindly provided by [Atlassian](http://www.atlassian.com).

| **Need to gnize your Enterprise?** Ever wanted to bookmark and mashup your enterprise content as easy as gnizing the web? Then you need gnizr Enterprise. [IM](http://www.imagemattersllc.com) is the leading provider of social bookmarking and web mashup of Enterprise content. Founded by the creators of gnizr open source, the world's most reliable open source social bookmarking and web mashup platform, Image Matters delivers enterprise-class support and development to help you unlock your Enterprise silos. |
|:-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
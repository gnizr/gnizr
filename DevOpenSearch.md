# Gnizr OpenSearch #

Gnizr supports [OpenSearch](http://www.opensearch.org) in two different ways.

First, it provides a search engine aggregator that allows users to search over multiple OpenSearch search services simultaneously. Search over third-party applications (e.g., Google and Wikipedia) can be made available to gnizr users by registering new OpenSearch description files with gnizr.

|[![](http://farm4.static.flickr.com/3160/2357863109_4a93f4dea9_o.png)](http://www.flickr.com/photos/14804582@N08/2357863109/)|
|:----------------------------------------------------------------------------------------------------------------------------|

Second, gnizr provides OpenSearch for its bookmark search. Gnizr users can add 'gnizr search' as a search plugin to their Firefox browser. Developers can create new applications that query gnizr search via the OpenSearch standard.

| [![](http://farm4.static.flickr.com/3032/2358714878_bb07f71c86_o.png)](http://www.flickr.com/photos/14804582@N08/2358714878/) |
|:------------------------------------------------------------------------------------------------------------------------------|

## Gnizr Search Engine Aggregator ##

On starting the gnizr application, the search engine aggregator reads the list of defined [OpenSearch service description](http://www.opensearch.org/Specifications/OpenSearch/1.1#OpenSearch_description_document) files.

In gnizr 2.3.x releases, the list of description files are defined in `WEB-INF/classes/spring/core-search.xml`.

```
<bean id="openSearchDirectory" class="com.gnizr.core.search.OpenSearchDirectory" singleton="true">
  <constructor-arg>
    <list>
      <value>/settings/opensearch/mybookmarks.action</value>
      <value>/settings/opensearch/community.action</value>
      <value>/settings/opensearch/yahoo-searchdescription.xml</value>
      <value>http://www.pfaf.org/database/openSearch.rss</value>
    </list>
  </constructor-arg>		
</bean>
```

In gnizr 2.4.x releases, the list of description files are defined in `WEB-INF/classes/gnizr-config.xml`.

```
<bean id="gnizrConfiguration" class="com.gnizr.web.util.GnizrConfiguration"
		  singleton="true">
...
  <property name="openSearchServices">			
    <list>
      <value>/settings/opensearch/mybookmarks.action</value>
      <value>/settings/opensearch/community.action</value>
      <value>/settings/opensearch/yahoo-searchdescription.xml</value>
      <value>http://www.pfaf.org/database/openSearch.rss</value>
    </list>
  </property>
</bean>
```

When a path value (i.e., defined in `<value/>`) is a relative path, the search engine aggregator assumes the file is located on the same host as the gnizr installation. It concatenates the URL defined by [`webApplicationUrl`](GnizrConfigXML.md) with the relative path to form the full URL to access the search description file.

## Search Gnizr Bookmarks via OpenSearch ##

By default, gnizr provides two different types of bookmark search. One for searching within a user's bookmark collection, and the other for searching within the whole gnizr community. OpenSearch is implemented for each type of bookmark search. The service descriptions are published in two distinctive documents.

The URL to these documents are defined with the following convention:

| Type of Search| URL |
|:--------------|:----|
| User Bookmarks | `http://[path-2-gnizr]/settings/opensearch/mybookmarks.action`|
| Community Bookmarks | `http://[path-2-gnizr]/settings/opensearch/community.action`|

Here is an example of the description document for the User Bookmark search:

```
<?xml version="1.0" encoding="UTF-8"?>
<OpenSearchDescription 
  xmlns="http://a9.com/-/spec/opensearch/1.1/"
  xmlns:gn="http://gnizr.com/ont/opensearch/2007/11/">
  <ShortName>Bookmarked by me</ShortName>
  <Description>Search my saved bookmarks in gnizr 2.4</Description>
  <Tags>bookmarks gnizr</Tags>
  <gn:DefaultEnabled>true</gn:DefaultEnabled>
  <Url type="application/vnd.gn-opensearch+json" 
       template="http://localhost:8080/gnizr/data/json/user/searchBookmark.action?queryString={searchTerms}&amp;page={startPage}"/>
  <gn:LoginRequired>true</gn:LoginRequired>

</OpenSearchDescription>
```

## Firefox Search Plugin Support ##

This feature is only available in gnizr 2.4.x.

In all gnizr application Web pages, the HTML header is defined with a special `<link/>` tags that informs the Firefox browser about gnizr's OpenSearch implementation.

The page headers usually contain the following:
```
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" 
"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
...

<!-- GNIZR OPENSEARCH DESCRIPTION -->
<link rel="search" type="application/opensearchdescription+xml"
      href="http://localhost:8080/gnizr/settings/opensearch/description.action" 
      title="gnizr 2.4" />

...
</head>
...
</html>
```

Here is an example of the description file content:
```
<?xml version="1.0"?>
<OpenSearchDescription xmlns="http://a9.com/-/spec/opensearch/1.1/">

  <ShortName>gnizr 2.4</ShortName>
  <Description>organize</Description>

  <Image height="16" width="16"  
         type="image/x-icon">http://localhost:8080/gnizr/images/favicon.ico</Image>

  <Url type="text/html" method="get" 
       template="http://localhost:8080/gnizr/search/list.action?q={searchTerms}"/>

</OpenSearchDescription>
```
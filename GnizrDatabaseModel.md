# Database Model #

All persistent data in gnizr is stored in MySQL. This document describes the database model of gnizr.

This model is used in the following releases:

  * Gnizr 2.2.0 (view: [gnizr-db.sql](http://fisheye2.cenqua.com/browse/gnizr/tags/gnizr-2.2.0/gnizr-db/src/main/resources/sql/gnizr_db.sql?r=12))
  * Gnizr 2.3.0 (view: [gnizr-db.sql](http://fisheye2.cenqua.com/browse/gnizr/trunk/gnizr-db/src/main/resources/sql/gnizr_db.sql?r=12))

[![](http://farm3.static.flickr.com/2129/2111269218_950cf23a03.jpg)](http://www.flickr.com/photos/14804582@N08/2111269218/)

**Note**: _click 'All Sizes' on the flickr page to fetch a high-resolution of the diagram._

### Entity Tables ###

| **Table Name** | **Description** |
|:---------------|:----------------|
| `user`         | User account information |
| `link`         | Web resource links (i.e., URL) and link properties |
| `tag`          | Tags (keywords) used by the users to label bookmarks |
| `bookmark`     | Bookmarks saved by the users and bookmark properties |
| `folder`       | Folders that users created to organize bookmarks |

#### Table: `user` ####

```
CREATE TABLE `user` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `username` varchar(45) NOT NULL,
  `password` varchar(45) NOT NULL,
  `fullname` varchar(100) NOT NULL,
  `created_on` datetime NOT NULL,
  `email` varchar(50) NOT NULL,
  `acct_status` int(10) unsigned zerofill NOT NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `idx_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;
```

  * `username` must be unique.
  * `password` should be inserted with MD5 hash of a plain text password string -- e.g., using MySQL function `md5()`.

When gnizr database is initialized for the first time, a special user account is created. The username of this account is `gnizr`. This is the superuser of a gnizr installation. **User account `gnizr` should never be removed or renamed.**

#### Table: `link` ####
```
CREATE TABLE `link` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `mime_type_id` int(10) unsigned default NULL,
  `url` text NOT NULL,
  `url_hash` varchar(45) NOT NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `url_hash_idx` USING BTREE (`url_hash`),
  KEY `FK_link_1` (`mime_type_id`),
  CONSTRAINT `FK_link_1` FOREIGN KEY (`mime_type_id`) 
    REFERENCES `mime_type_admin` (`id`) 
    ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;
```

  * `url_hash` is a MD5 hash of `url`
  * `mime_type_id` must exists in table `mime_type_id.id`

A `link` record describes a URL resource on the Web. Any given URL resource can only exists once in the `link` table. For example, if a URL `http://www.google.com` exists in the table, any subsequent SQL operation that tries to insert a record of the same URL will cause a DB exception to be thrown. The "uniqueness" of a URL is maintained via the column `url_hash`, which is the MD5 hash of column value `url`.

#### Table: `tag` ####

```
CREATE TABLE `tag` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `tag` varchar(45) collate utf8_bin NOT NULL,
  `count` int(10) unsigned zerofill NOT NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `idx_tag` (`tag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=DYNAMIC;
```

  * `tag` must be unique

This table stores the list of tags that have been used to label bookmarks. This is the central place to store all tag strings. Tables that require references to a tag should define a database column to point to `tag.id`.

The column `count` stores the total number of bookmarks that have been labeled `tag.tag`. This count reflects the number of times used _currently_.

#### Table: `bookmark` ####
```
CREATE TABLE `bookmark` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `user_id` int(10) unsigned NOT NULL,
  `link_id` int(10) unsigned NOT NULL,
  `title` text collate utf8_bin NOT NULL,
  `notes` text collate utf8_bin NOT NULL,
  `created_on` datetime NOT NULL,
  `last_updated` datetime NOT NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `bookmark_user_id_link_id_idx` USING BTREE (`user_id`,`link_id`),
  KEY `bookmark_user_id_idx` (`user_id`),
  KEY `bookmark_link_id` USING BTREE (`link_id`),
  KEY `bookmark_created_on_idx` (`created_on`),
  KEY `bookmark_lastup_idx` (`last_updated`),
  CONSTRAINT `FK_bookmark_1` FOREIGN KEY (`user_id`) 
    REFERENCES `user` (`id`) 
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_bookmark_2` FOREIGN KEY (`link_id`) 
    REFERENCES `link` (`id`) 
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=DYNAMIC;
```

  * Foreign key: `bookmark.user_id => user.id`
    * Deleting an existing user record will also delete all bookmarks saved by the user
    * Updating the ID of the user will also update the referenced user ID in bookmarks saved by the user.
  * Foreign key: `bookmark.link_id => link.id`
    * Deleting an existing link record will also delete all bookmarks that reference this link
    * Updating the ID of the link record will also update which referenced by the bookmarks.

Bookmarks are URL resources saved by the users. Unlike records in the `link` table, which only describe the intrinsic property of URL, records in the `bookmark` table adds a relationship between `user` and `link`.

A pair `bookmark.user_id` and `bookmark.link_id` defines a URL being saved by a user. A given user can only have one bookmark for a given URL. Different users can have their own bookmarks of the same URL.

#### Table: `folder` ####
```
CREATE TABLE `folder` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `folder_name` varchar(45) collate utf8_bin NOT NULL,
  `owner_id` int(10) unsigned NOT NULL,
  `description` text collate utf8_bin NOT NULL,
  `last_updated` datetime NOT NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `folder_owner_folder_idx` USING BTREE (`folder_name`,`owner_id`),
  KEY `FK_folder_admin_1` (`owner_id`),
  CONSTRAINT `FK_folder_admin_1` FOREIGN KEY (`owner_id`) 
    REFERENCES `user` (`id`) 
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
```

  * Pair `folder_name` and `owner_id` must be unique
  * Foreign key: `folder.owner_id => user.id`
    * Deleting an existing user record will also delete all folders created by this user.
    * Updating the ID of an existing user record will also update which referenced by the folders.

Users can organize their saved bookmarks using folders. This table stores information about user-created folders. Folder names (i.e., `folder.folder_name`) may be any non-empty strings. Some folder names have special purpose.

Special folder names:
| `_my_` | the default folder in which all new bookmarks will be saved. This folder corresponds to the "My Bookmarks" folder in the web application.|
|:-------|:-----------------------------------------------------------------------------------------------------------------------------------------|
| `_import_`| the default folder in which all new bookmarks saved via RSS crawling will be saved. This folder corresponds to the "My RSS Imports" folder in the web application.|

### Tagging Relationship Tables ###

| **Table Name** | **Description** |
|:---------------|:----------------|
| `user_tag_idx` | Relates users to their tag usage.|
| `link_tag_idx` | Relates links (URL) to tags.  |
| `bookmark_tag_idx` | Relates bookmarks to tags. |
| `folder_tag_idx` | Relates folders to tags. |

#### Table: `user_tag_idx` ####
```
CREATE TABLE `user_tag_idx` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `user_id` int(10) unsigned NOT NULL,
  `tag_id` int(10) unsigned NOT NULL,
  `count` int(10) unsigned zerofill NOT NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `idx_user_id_tag_id` (`user_id`,`tag_id`),
  KEY `FK_user_tag_idx_2` (`tag_id`),
  KEY `idx_user_tag_id_user_id` (`user_id`),
  CONSTRAINT `FK_user_tag_idx_1` FOREIGN KEY (`user_id`) 
    REFERENCES `user` (`id`) 
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_user_tag_idx_2` FOREIGN KEY (`tag_id`) 
    REFERENCES `tag` (`id`) 
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;
```

  * `id` must be unique
  * Foreign key: `user_tag_idx.user_id => user.id`
    * On deleting an existing user record, all `user_tag_idx` records of the same user will be deleted.
    * On updating the `user.id` of a user record, records of the same `user_tag_idx.user_id` will updated automatically.
  * Foreign key: `user_tag_idx.tag_id => tag.id`
    * On deleting an existing tag record, all `user_tag_idx` records of the same `tag.id` will be deleted.
    * On updating the `tag.id` of a tag record, records of the same `user_tag_idx.tag_id` will be updated automatically.

`user_tag_idx.id` uniquely identifies the relationship between a tag and the user who has used the tag. This `id` is used to form the subject and the object of a tag assertion.

The column `count` stores the total number of times a given user has used `user_tag_idx.tag_id` to label saved bookmarks. This count reflects the number of times used _currently_.

#### Table: `link_tag_idx` ####
```
CREATE TABLE `link_tag_idx` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `link_id` int(10) unsigned NOT NULL,
  `tag_id` int(10) unsigned NOT NULL,
  `count` int(10) unsigned zerofill NOT NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `idx_link_id_tag_id` (`link_id`,`tag_id`),
  KEY `FK_link_tag_idx_2` (`tag_id`),
  KEY `idx_link_id` (`link_id`),
  CONSTRAINT `FK_link_tag_idx_1` FOREIGN KEY (`link_id`) 
    REFERENCES `link` (`id`) 
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_link_tag_idx_2` FOREIGN KEY (`tag_id`) 
    REFERENCES `tag` (`id`) 
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;
```

  * `id` must be unique
  * Foreign key: `link_tag_idx.link_id => link.id`
    * On deleting an existing link record, all `link_tag_idx` records of the same link will be deleted.
    * On updating the `link.id` of a link record, records of the same `link_tag_idx.link_id` will updated automatically.
  * Foreign key: `link_tag_idx.tag_id => tag.id`
    * On deleting an existing tag record, all `link_tag_idx` records of the same `tag.id` will be deleted.
    * On updating the `tag.id` of a tag record, records of the same `link_tag_idx.tag_id` will be updated automatically.

The column `count` stores the total number of times a given link has been labeled by users using `link_tag_idx.tag_id`. This count reflects the number of times used _currently_.

#### Table: `bookmark_tag_idx` ####
```
CREATE TABLE `bookmark_tag_idx` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `bookmark_id` int(10) unsigned NOT NULL,
  `tag_id` int(10) unsigned NOT NULL,
  `count` int(10) unsigned NOT NULL,
  `position` smallint(5) unsigned NOT NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `Index_4` (`bookmark_id`,`tag_id`),
  KEY `Index_2` (`bookmark_id`),
  KEY `Index_3` (`tag_id`),
  CONSTRAINT `FK_bookmark_tag_idx_1` FOREIGN KEY (`bookmark_id`) 
    REFERENCES `bookmark` (`id`) 
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_bookmark_tag_idx_2` FOREIGN KEY (`tag_id`) 
    REFERENCES `tag` (`id`) 
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
```

  * `id` must be unique
  * Foreign key: `bookmark_tag_idx.link_id => bookmark.id`
    * On deleting an existing bookmark record, all `bookmark_tag_idx` records of the same bookmark will be deleted.
    * On updating the `bookmark.id` of a bookmark record, records of the same `bookmark_tag_idx.bookmark_id` will updated automatically.
  * Foreign key: `bookmark_tag_idx.tag_id => tag.id`
    * On deleting an existing tag record, all `bookmark_tag_idx` records of the same `bookmark.id` will be deleted.
    * On updating the `tag.id` of a tag record, records of the same `bookmark_tag_idx.tag_id` will be updated automatically.

The column `count` stores the total number of times a given bookmark has been labeled using `bookmark_tag_idx.tag_id`. This count reflects the number of times used _currently_.  Typically this `count` value is either `0` or `1`.

#### Table: `folder_tag_idx` ####
```
CREATE TABLE `folder_tag_idx` (
  `folder_id` int(10) unsigned NOT NULL,
  `tag_id` int(10) unsigned NOT NULL,
  `count` int(10) unsigned NOT NULL,
  PRIMARY KEY  (`folder_id`,`tag_id`),
  KEY `FK_folder_tag_idx_2` (`tag_id`),
  CONSTRAINT `FK_folder_tag_idx_1` FOREIGN KEY (`folder_id`) 
    REFERENCES `folder` (`id`) 
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_folder_tag_idx_2` FOREIGN KEY (`tag_id`) 
    REFERENCES `tag` (`id`) 
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
```

  * `id` must be unique
  * Foreign key: `folder_tag_idx.link_id => folder.id`
    * On deleting an existing folder record, all `folder_tag_idx` records of the same folder will be deleted.
    * On updating the `folder.id` of a folder record, records of the same `folder_tag_idx.folder_id` will updated automatically.
  * Foreign key: `folder_tag_idx.tag_id => tag.id`
    * On deleting an existing tag record, all `folder_tag_idx` records of the same `folder.id` will be deleted.
    * On updating the `tag.id` of a tag record, records of the same `bookmark_tag_idx.tag_id` will be updated automatically.

The column `count` stores the total number of times a tag appeared in all bookmarks that are saved in a given folder. This count reflects the number of times used _currently_.

### Tag Hierarchy Tables ###

| **Table Name** | **Description** |
|:---------------|:----------------|
| `tag_prpt`     | Defines RDF properties that can be used to make tag assertions|
| `tag_assertion`| Stores user-defined assertions that describe how tags are related|

#### Table: `tag_prpt` ####
```
CREATE TABLE `tag_prpt` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `name` varchar(45) NOT NULL,
  `ns_prefix` varchar(10) NOT NULL default 'gn',
  `description` varchar(255) default NULL,
  `prpt_type` enum('spatial','temporal','system','reference','default') NOT NULL default 'default',
  `cardinality` int(11) NOT NULL default '-1',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `tag_prpt_unique_prpt_idx` (`name`,`ns_prefix`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;
```

  * `name`: a non-empty string with no white-spaces.
  * `ns`: any valid XML namespace prefix
  * `description`: text description of this tag property
  * `prpt_type`: must be one of five ENUM types
    1. `spatial`: for describing spatial property
    1. `temporal`: for describing temporal property
    1. `system`: for describing gnizr system functional property
    1. `reference`: for describing relationships with other web resources
    1. `default`: for describing everything else

This table defines the set of valid property can be used to construct tag assertion. The semantic of a tag property follows the RDF property semantic. By default, gnizr comes with a set of tag property definitions.

| **Property Name** | **Namespace Prefix** | **Description** | **Type** |
|:------------------|:---------------------|:----------------|:---------|
| `related`         | `skos`               | SKOS related    | 'default' |
| `broader`         | `skos`               | SKOS broader    | 'default' |
| `narrower`        | `skos`               | SKOS narrower   | 'default' |
| `type`            | `rdf`                | RDF type        | 'default' |

#### Table: `tag_assertion` ####
```
CREATE TABLE `tag_assertion` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `subject_id` int(10) unsigned NOT NULL,
  `prpt_id` int(10) unsigned NOT NULL,
  `object_id` int(10) unsigned NOT NULL,
  `user_id` int(10) unsigned NOT NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `tag_asrt_uniq_asrt_idx` (`subject_id`,`prpt_id`,`object_id`,`user_id`),
  KEY `FK_tag_asrt_prpt_id` USING BTREE (`prpt_id`),
  KEY `FK_tag_asrt_subject_id` USING BTREE (`subject_id`),
  KEY `FK_tag_asrt_object_id` USING BTREE (`object_id`),
  KEY `FK_tag_asrt_user_id` USING BTREE (`user_id`),
  CONSTRAINT `FK_tag_asrt_object_id` FOREIGN KEY (`object_id`) 
    REFERENCES `user_tag_idx` (`id`) 
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_tag_asrt_prpt_id` FOREIGN KEY (`prpt_id`) 
    REFERENCES `tag_prpt` (`id`) 
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_tag_asrt_subject_id` FOREIGN KEY (`subject_id`) 
    REFERENCES `user_tag_idx` (`id`) 
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_tag_asrt_user_id` FOREIGN KEY (`user_id`) 
    REFERENCES `user` (`id`) 
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;
```

  * `id`: an unique ID of a tag assertion.
  * `subject_id`: the subject of a tag assertion.
  * `prpt_id`: the property of a tag assertion.
  * `object_id`: the object of a tag assertion.
  * `user_id`: the user who created this tag assertion.

A tag assertion is an RDF statement, which consists of three parts: subject, property (predicate) and object (value). In the `tag_assertion` table, `subject_id`, `prpt_id` and `object_id` represent those three parts, respectively. These columns link to the 'id' column of the `user_tag_idx` relationship. This allows different users to make different assertions about the same tag, and doesn't pollute the tag assertion space of each other.

### Bookmark Relationship Tables ###

| **Table Name** | **Description** |
|:---------------|:----------------|
| `bookmark_folder` | Relates bookmarks to folders in which they are saved|
| `feed_folder`  | Describes folders that bookmarks saved from RSS feed crawling should be saved in |
| `for_user`     | Describes the relationship that a user suggesting bookmarks to other users |

#### Table: `bookmark_folder` ####
```
CREATE TABLE `bookmark_folder` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `bookmark_id` int(10) unsigned NOT NULL,
  `folder_id` int(10) unsigned NOT NULL,
  `last_updated` datetime NOT NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `bookmark_folder_unique_bmark` (`bookmark_id`,`folder_id`),
  KEY `FK_bookmark_folder_1` (`bookmark_id`),
  KEY `FK_bookmark_folder_2` (`folder_id`),
  KEY `bookmark_folder_lastup_idx` (`last_updated`),
  CONSTRAINT `FK_bookmark_folder_1` FOREIGN KEY (`bookmark_id`) 
    REFERENCES `bookmark` (`id`) 
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_bookmark_folder_2` FOREIGN KEY (`folder_id`) 
    REFERENCES `folder` (`id`) 
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
```

  * `id`: an unique ID that identifies a given bookmark is saved in a particular folder.
  * a bookmark can't be saved in the same folder more than once.
  * Foreign key: `bookmark_folder.bookmark_id => bookmark.id`
    * On deleting an existing bookmark record, all `bookmark_folder` records whose `bookmark_id` are the same as `bookmark.id` will also be deleted.
    * On updating `bookmark.id` of an existing record, all `bookmark_folder` records whose `bookmark_id` are the same as `bookmark.id` will also be updated.
  * Foreign key: `bookmark_folder.folder_id => folder.id`
    * On deleting an existing folder record, all `bookmark_folder` records whose `folder_id` are the same as `folder.id` will also be deleted.
    * On updating `folder.id` of an existing record, all `bookmark_folder` records whose `folder_id` are the same as `folder.id` will also be updated.

### Geotagging Tables ###

| **Table Name** | **Description** |
|:---------------|:----------------|
| `point_marker` | Point objects can be used to describe the latitude/longitude location of things|
| `bookmark_point_marker_idx` | Relates bookmarks to objects in `point_marker` table|

#### Table: `point_marker` ####
```
CREATE TABLE `point_marker` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `geom` point NOT NULL,
  `notes` varchar(255) NOT NULL,
  `icon_id` tinyint(3) unsigned NOT NULL default '0',
  PRIMARY KEY  (`id`),
  SPATIAL KEY `geom_idx` (`geom`(32))
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
```
  * `id`: an unique ID that identifies a Point marker for describing the geographical location of a thing
  * `geom`: a `POINT` geometry for describing latitude and longitude. Format `POINT(X,Y)`, in which `X` is longitude and `Y` is latitude.
  * `notes`: short text description of this Point marker. This is usually defined by the user who has created this Point marker.
  * `icon_id`: _this column is not currently used_

This table stores a set of Point markers that can used to geotag bookmarks. Because MySQL MyISAM engine doesn't support the use of FOREIGN KEY, the application will be responsible to delete geotagged bookmarks whenever a referenced Point marker is deleted from this table.

#### Table: `bookmark_point_marker_idx` ####
```
CREATE TABLE `bookmark_point_marker_idx` (
  `bookmark_id` int(10) unsigned NOT NULL,
  `point_marker_id` int(10) unsigned NOT NULL,
  PRIMARY KEY  (`bookmark_id`,`point_marker_id`),
  CONSTRAINT `FK_bookmark_point_mark_idx_1` FOREIGN KEY (`bookmark_id`) 
    REFERENCES `bookmark` (`id`) 
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
```

  * Pair `bookmark_id` and `point_marker_id` represents a geotagged bookmark relation.
  * A bookmark can have more than one geotag.
  * Foreign key: `bookmark_point_marker_idx.bookmark_id => bookmark.id`
    * On deleting an existing bookmark of `bookmark.id`, all `bookmark_point_marker_idx` records whose `bookmark_id` is the samee as `bookmark.id` will also be deleted.
    * On updating the `bookmark.id` of an existing bookmark, all `bookmark_point_marker_idx` records whose `bookmark_id` is the same as `bookmark.id` will also be updated.

### Auxiliary Tables ###

| **Table Name** | **Description** |
|:---------------|:----------------|
| `search_idx`   | Cached text description of bookmarks, used for MySQL full-text search|
| `mime_type_admin` | Defines MIME types can be used to identify the MIME type of link (i.e., `link.mime_type_id`)|

#### Table: `search_idx` ####
```
CREATE TABLE `search_idx` (
  `bookmark_id` int(10) unsigned NOT NULL,
  `text` text NOT NULL,
  `tags` text NOT NULL,
  PRIMARY KEY  (`bookmark_id`),
  FULLTEXT KEY `search_idx_text` (`text`),
  FULLTEXT KEY `search_idx_tags` (`tags`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
```

  * `bookmark_id`: the ID of an existing bookmark in `bookmark`
  * `text`: the concatenation of `bookmark.title` and `bookmark.notes`
  * `tags`: the concatenation of all tags of a bookmark

This table is created for support full-text search in MySQL. Because MySQL MyISAM engine doesn't support the use of FOREIGN KEY, when a bookmark is deleted, the application must remove it's corresponding record in the `search_idx` table.

#### Table: `mime_type_admin` ####
```
CREATE TABLE `mime_type_admin` (
  `id` int(10) unsigned NOT NULL default '0',
  `mime_type` varchar(45) NOT NULL,
  `label` varchar(100) NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;
```

  * `id`: an unique ID that identifies a known MIME-type in gnizr
  * `mime_type`: mime type string
  * `label`: text description of the mime type

Predefine MIME type in gnizr:

| **MIME Type** | **Label** |
|:--------------|:----------|
| `Unknown`     | `Unknown` |
| `text/xml`    | `XML`     |
| `text/plain`  | `Plain Text` |
| `text/html`   | `HTML`    |
| `image/jpeg`  | `JPEG`    |
| `image/png`   | `PNG`     |
| `image/tiff`  | `TIFF`    |
| `image/gif`   | `GIF`     |
| `application/rss+xml` | `RSS`     |
| `application/rdf+xml` | `RDF`     |
| `application/owl+xml` | `OWL`     |

Define MIME types help to identify the type of resources that the users have bookmarked. Objects of `mime_type_admi` are referenced  by `link.mime_type_id`.
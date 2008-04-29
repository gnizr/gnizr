-- MySQL dump 10.10
--
-- Host: localhost    Database: gnizr_design
-- ------------------------------------------------------
-- Server version	5.0.27-community-nt

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `bookmark`
--

DROP TABLE IF EXISTS `bookmark`;
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
  CONSTRAINT `FK_bookmark_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_bookmark_2` FOREIGN KEY (`link_id`) REFERENCES `link` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=DYNAMIC;

--
-- Table structure for table `bookmark_folder`
--

DROP TABLE IF EXISTS `bookmark_folder`;
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
  CONSTRAINT `FK_bookmark_folder_1` FOREIGN KEY (`bookmark_id`) REFERENCES `bookmark` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_bookmark_folder_2` FOREIGN KEY (`folder_id`) REFERENCES `folder` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

--
-- Table structure for table `bookmark_point_marker_idx`
--

DROP TABLE IF EXISTS `bookmark_point_marker_idx`;
CREATE TABLE `bookmark_point_marker_idx` (
  `bookmark_id` int(10) unsigned NOT NULL,
  `point_marker_id` int(10) unsigned NOT NULL,
  PRIMARY KEY  (`bookmark_id`,`point_marker_id`),
  CONSTRAINT `FK_bookmark_point_mark_idx_1` FOREIGN KEY (`bookmark_id`) REFERENCES `bookmark` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table `bookmark_tag_idx`
--

DROP TABLE IF EXISTS `bookmark_tag_idx`;
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
  CONSTRAINT `FK_bookmark_tag_idx_1` FOREIGN KEY (`bookmark_id`) REFERENCES `bookmark` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_bookmark_tag_idx_2` FOREIGN KEY (`tag_id`) REFERENCES `tag` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

--
-- Table structure for table `feed_folder`
--

DROP TABLE IF EXISTS `feed_folder`;
CREATE TABLE `feed_folder` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `feed_id` int(10) unsigned NOT NULL,
  `folder_id` int(10) unsigned NOT NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `feed_folder_unique_entry` (`feed_id`,`folder_id`),
  KEY `FK_feed_folder_2` (`folder_id`),
  KEY `FK_feed_folder_1` USING BTREE (`feed_id`),
  CONSTRAINT `FK_feed_folder_1` FOREIGN KEY (`feed_id`) REFERENCES `feed_subscription` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_feed_folder_2` FOREIGN KEY (`folder_id`) REFERENCES `folder` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table `feed_subscription`
--

DROP TABLE IF EXISTS `feed_subscription`;
CREATE TABLE `feed_subscription` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `bookmark_id` int(10) unsigned NOT NULL COMMENT 'ref a bmark that defines a RSS feed',
  `last_sync` datetime default NULL COMMENT 'when was the last time that this feed was read',
  `match_text` text character set utf8 NOT NULL COMMENT 'space-delimited tag labels that a bmark importer will use to match feed entries that should imported as bookmarks',
  `auto_import` tinyint(1) NOT NULL default '1',
  `pub_date` datetime default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `feed_sub_unique_bm_id` (`bookmark_id`),
  KEY `feed_sub_last_sync_idx` (`last_sync`),
  CONSTRAINT `FK_feed_subscription_1` FOREIGN KEY (`bookmark_id`) REFERENCES `bookmark` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=DYNAMIC;

--
-- Table structure for table `folder`
--

DROP TABLE IF EXISTS `folder`;
CREATE TABLE `folder` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `folder_name` varchar(45) collate utf8_bin NOT NULL,
  `owner_id` int(10) unsigned NOT NULL,
  `description` text collate utf8_bin NOT NULL,
  `last_updated` datetime NOT NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `folder_owner_folder_idx` USING BTREE (`folder_name`,`owner_id`),
  KEY `FK_folder_admin_1` (`owner_id`),
  CONSTRAINT `FK_folder_admin_1` FOREIGN KEY (`owner_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

--
-- Table structure for table `folder_tag_idx`
--

DROP TABLE IF EXISTS `folder_tag_idx`;
CREATE TABLE `folder_tag_idx` (
  `folder_id` int(10) unsigned NOT NULL,
  `tag_id` int(10) unsigned NOT NULL,
  `count` int(10) unsigned NOT NULL,
  PRIMARY KEY  (`folder_id`,`tag_id`),
  KEY `FK_folder_tag_idx_2` (`tag_id`),
  CONSTRAINT `FK_folder_tag_idx_1` FOREIGN KEY (`folder_id`) REFERENCES `folder` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_folder_tag_idx_2` FOREIGN KEY (`tag_id`) REFERENCES `tag` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table `for_user`
--

DROP TABLE IF EXISTS `for_user`;
CREATE TABLE `for_user` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `for_user_id` int(10) unsigned NOT NULL,
  `bookmark_id` int(10) unsigned NOT NULL,
  `message` text NOT NULL,
  `created_on` datetime NOT NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `for_user_index_1` (`for_user_id`,`bookmark_id`),
  KEY `FK_for_user_1` (`for_user_id`),
  KEY `FK_for_user_2` (`bookmark_id`),
  CONSTRAINT `FK_for_user_1` FOREIGN KEY (`for_user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_for_user_2` FOREIGN KEY (`bookmark_id`) REFERENCES `bookmark` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `link`
--

DROP TABLE IF EXISTS `link`;
CREATE TABLE `link` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `mime_type_id` int(10) unsigned default NULL,
  `url` text NOT NULL,
  `url_hash` varchar(45) NOT NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `url_hash_idx` USING BTREE (`url_hash`),
  KEY `FK_link_1` (`mime_type_id`),
  CONSTRAINT `FK_link_1` FOREIGN KEY (`mime_type_id`) REFERENCES `mime_type_admin` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

--
-- Table structure for table `link_tag_idx`
--

DROP TABLE IF EXISTS `link_tag_idx`;
CREATE TABLE `link_tag_idx` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `link_id` int(10) unsigned NOT NULL,
  `tag_id` int(10) unsigned NOT NULL,
  `count` int(10) unsigned zerofill NOT NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `idx_link_id_tag_id` (`link_id`,`tag_id`),
  KEY `FK_link_tag_idx_2` (`tag_id`),
  KEY `idx_link_id` (`link_id`),
  CONSTRAINT `FK_link_tag_idx_1` FOREIGN KEY (`link_id`) REFERENCES `link` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_link_tag_idx_2` FOREIGN KEY (`tag_id`) REFERENCES `tag` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

--
-- Table structure for table `mime_type_admin`
--

DROP TABLE IF EXISTS `mime_type_admin`;
CREATE TABLE `mime_type_admin` (
  `id` int(10) unsigned NOT NULL default '0',
  `mime_type` varchar(45) NOT NULL,
  `label` varchar(100) NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

--
-- Table structure for table `point_marker`
--

DROP TABLE IF EXISTS `point_marker`;
CREATE TABLE `point_marker` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `geom` point NOT NULL,
  `notes` varchar(255) NOT NULL,
  `icon_id` tinyint(3) unsigned NOT NULL default '0',
  PRIMARY KEY  (`id`),
  SPATIAL KEY `geom_idx` (`geom`(32))
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Table structure for table `search_idx`
--

DROP TABLE IF EXISTS `search_idx`;
CREATE TABLE `search_idx` (
  `bookmark_id` int(10) unsigned NOT NULL,
  `text` text NOT NULL,
  `tags` text NOT NULL,
  PRIMARY KEY  (`bookmark_id`),
  FULLTEXT KEY `search_idx_text` (`text`),
  FULLTEXT KEY `search_idx_tags` (`tags`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Table structure for table `tag`
--

DROP TABLE IF EXISTS `tag`;
CREATE TABLE `tag` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `tag` varchar(45) collate utf8_bin NOT NULL,
  `count` int(10) unsigned zerofill NOT NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `idx_tag` (`tag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=DYNAMIC;

--
-- Table structure for table `tag_assertion`
--

DROP TABLE IF EXISTS `tag_assertion`;
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
  CONSTRAINT `FK_tag_asrt_object_id` FOREIGN KEY (`object_id`) REFERENCES `user_tag_idx` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_tag_asrt_prpt_id` FOREIGN KEY (`prpt_id`) REFERENCES `tag_prpt` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_tag_asrt_subject_id` FOREIGN KEY (`subject_id`) REFERENCES `user_tag_idx` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_tag_asrt_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

--
-- Table structure for table `tag_prpt`
--

DROP TABLE IF EXISTS `tag_prpt`;
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

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
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

--
-- Table structure for table `user_tag_idx`
--

DROP TABLE IF EXISTS `user_tag_idx`;
CREATE TABLE `user_tag_idx` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `user_id` int(10) unsigned NOT NULL,
  `tag_id` int(10) unsigned NOT NULL,
  `count` int(10) unsigned zerofill NOT NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `idx_user_id_tag_id` (`user_id`,`tag_id`),
  KEY `FK_user_tag_idx_2` (`tag_id`),
  KEY `idx_user_tag_id_user_id` (`user_id`),
  CONSTRAINT `FK_user_tag_idx_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_user_tag_idx_2` FOREIGN KEY (`tag_id`) REFERENCES `tag` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2007-10-02 18:59:05

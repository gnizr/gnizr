CREATE TABLE IF NOT EXISTS `point_marker` (
  `id` INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
  `geom` POINT NOT NULL,
  `notes` VARCHAR(255) NOT NULL,
  `icon_id` TINYINT UNSIGNED NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  SPATIAL INDEX geom_idx(`geom`)
)
ENGINE = MyISAM
CHARACTER SET utf8 COLLATE utf8_general_ci;
CREATE TABLE IF NOT EXISTS `bookmark_point_marker_idx` (
  `bookmark_id` INTEGER UNSIGNED NOT NULL,
  `point_marker_id` INTEGER UNSIGNED NOT NULL,
  PRIMARY KEY (`bookmark_id`, `point_marker_id`),
  CONSTRAINT `FK_bookmark_point_mark_idx_1` FOREIGN KEY `FK_bookmark_point_mark_idx_1` (`bookmark_id`)
    REFERENCES `bookmark` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE
)
ENGINE = InnoDB;
DROP VIEW IF EXISTS geo_bookmark;
DROP PROCEDURE IF EXISTS createGeoTag;
DROP PROCEDURE IF EXISTS getGeoTag;
DROP PROCEDURE IF EXISTS deleteGeoTag;
DROP PROCEDURE IF EXISTS deleteAllGeoTag;
DROP PROCEDURE IF EXISTS updateGeoTag;
DROP PROCEDURE IF EXISTS findGeoTagUserTagId;
DROP PROCEDURE IF EXISTS findAllGeoTag;
DROP PROCEDURE IF EXISTS findGeoTagUserId;



delimiter //
DROP PROCEDURE IF EXISTS migrateGeotags//
CREATE PROCEDURE migrateGeotags()
BEGIN   
DECLARE bmId, tagId INT;
DECLARE tag VARCHAR(255);
DECLARE geom POINT;
DECLARE done INT;
DECLARE cur1 CURSOR FOR SELECT * FROM bookmarkHasGeotags; 
DECLARE CONTINUE HANDLER FOR NOT FOUND 
  SET done = 1; 

CREATE TEMPORARY TABLE 
   bookmarkHasGeotags(bmId INT, tagId INT, tag VARCHAR(255), geom POINT);

REPLACE bookmarkHasGeotags(bmId,tagId,tag,geom)
SELECT bookmark.id as bmId, tag.id as tagId, tag.tag, geo_tag.geo
   FROM geo_tag, user_tag_idx, tag, bookmark, bookmark_tag_idx
   where geo_tag.user_tag_id = user_tag_idx.id and user_tag_idx.tag_id = tag.id AND
         bookmark_tag_idx.tag_id = user_tag_idx.tag_id AND
         bookmark_tag_idx.bookmark_id = bookmark.id AND
         bookmark.user_id = user_tag_idx.user_id;

OPEN cur1;
REPEAT 
  FETCH cur1 INTO bmId, tagId, tag, geom;
  INSERT INTO point_marker(geom,notes,icon_id) VALUES(geom,tag,0);
  INSERT INTO bookmark_point_marker_idx(bookmark_id,point_marker_id) 
    VALUES(bmId,LAST_INSERT_ID()); 
UNTIL done = 1
END REPEAT;
CLOSE cur1;
END//
CALL migrateGeotags()//
DROP PROCEDURE IF EXISTS migrateGeotags//  
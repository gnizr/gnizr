delimiter //
###############################################################
# PROCEDURE: createPointMarker
# INPUT: geomPoint, notes, markerIconId
# OUTPUT: id
DROP PROCEDURE IF EXISTS createPointMarker//
CREATE PROCEDURE createPointMarker(IN geomPoint POINT,
                          		   IN notes VARCHAR(255),
                          		   IN markerIconId INT,
                          		   OUT id INT)
BEGIN
  INSERT INTO point_marker(geom,notes,icon_id)
    VALUES (geomPoint,notes,markerIconId);
  SELECT LAST_INSERT_ID() INTO id;
END//
###############################################################
# PROCEDURE: deletePointMarker
# INPUT: id
# OUTPUT: NONE
DROP PROCEDURE IF EXISTS deletePointMarker//
CREATE PROCEDURE deletePointMarker(IN id INT)
BEGIN
  DELETE FROM point_marker WHERE point_marker.id = id;
END//
###############################################################
# PROCEDURE: getPointMaker
# INPUT: id
# OUTPUT: NONE
DROP PROCEDURE IF EXISTS getPointMaker//
CREATE PROCEDURE getPointMaker(id INTEGER)
BEGIN
  SELECT point_marker.id, asWKB(point_marker.geom) as geomWKB,
         point_marker.notes, point_marker.icon_id FROM point_marker
    WHERE point_marker.id = id; 
END//
###############################################################
# PROCEDURE: updatePointMarker
# INPUT: id, geomPoint, notes, markerIconId
# OUTPUT: NONE
DROP PROCEDURE IF EXISTS updatePointMarker//
CREATE PROCEDURE updatePointMarker(IN id INT,
					               IN geomPoint POINT,
					               IN notes VARCHAR(255),
					               IN markerIconId INT)
BEGIN
  UPDATE point_marker AS pmTbl SET
    pmTbl.geom = geomPoint,
    pmTbl.notes = notes,
    pmTbl.icon_id = markerIconId
  WHERE pmTbl.id = id;
END//
###############################################################
# PROCEDURE: addPointMarker
# INPUT: bmId, pmId
# OUTPUT: NONE
DROP PROCEDURE IF EXISTS addPointMarker//
CREATE PROCEDURE addPointMarker(IN bmId INT, 
                                IN pmId INT)
BEGIN
 INSERT IGNORE INTO bookmark_point_marker_idx(bookmark_id,point_marker_id) 
   VALUES (bmId,pmId);
END//
###############################################################
# PROCEDURE: removePointMarker 
# INPUT: bmId, pmId
# OUTPUT: NONE
DROP PROCEDURE IF EXISTS removePointMarker//
CREATE PROCEDURE removePointMarker(IN bmId INT, IN pmId INT)
BEGIN
  DELETE point_marker, bookmark_point_marker_idx
    FROM point_marker, bookmark_point_marker_idx
    WHERE point_marker.id = pmId AND
          bookmark_point_marker_idx.bookmark_id = bmId AND
          bookmark_point_marker_idx.point_marker_id = pmId;
END//
###############################################################
# PROCEDURE: findPointMarkers
# INPUT: bmId
# OUTPUT: NONE
DROP PROCEDURE IF EXISTS findPointMarkers//
CREATE PROCEDURE findPointMarkers(IN bmId INT)
BEGIN
  SELECT point_marker.id, point_marker.notes, point_marker.icon_id,
         asWKB(point_marker.geom) as geomWKB 
    FROM point_marker WHERE point_marker.id
    IN (SELECT t.point_marker_id FROM bookmark_point_marker_idx AS t
          WHERE t.bookmark_id = bmId);
END//
###############################################################
# PROCEDURE: pageFlrBookmarkHasGeomMarker
# INPUT: folderId, offset, count
# OUTPUT: totalCount
DROP PROCEDURE IF EXISTS pageFlrBookmarkHasGeomMarker//
CREATE PROCEDURE pageFlrBookmarkHasGeomMarker(IN folderId INT,
                                              IN offset INT,
                                              IN count INT,
                                              OUT totalCount INT)
BEGIN
  SET @folderId = folderId;
  SET @offset = offset;
  SET @count = count;
  SET @qry = CONCAT("
    SELECT SQL_CALC_FOUND_ROWS *, bookmarkTags(bookmark.id) as tags, 
           bookmarkInFolders(bookmark.id) AS folders,
           linkCount(link.id) as count
    FROM user, bookmark, link
    WHERE bookmark.user_id=user.id AND
          bookmark.link_id=link.id AND
          bookmark.id IN (
            SELECT bookmark_folder.bookmark_id FROM bookmark_folder
            WHERE bookmark_folder.folder_id = ? ) AND EXISTS ( 
            SELECT * FROM bookmark_point_marker_idx 
            WHERE bookmark_point_marker_idx.bookmark_id = bookmark.id)
    ORDER BY bookmark.title ASC LIMIT ?,?            
  ");
  PREPARE STMT FROM @qry;
  EXECUTE STMT USING @folderId, @offset, @count;
  SELECT FOUND_ROWS() INTO totalCount;
END//
###############################################################
# PROCEDURE: pageArcBookmarkHasGeomMarker
# INPUT: userId, offset, count
# OUTPUT: totalCount
DROP PROCEDURE IF EXISTS pageArcBookmarkHasGeomMarker//
CREATE PROCEDURE pageArcBookmarkHasGeomMarker(IN userId INT,
                                              IN offset INT,
                                              IN count INT,
                                              OUT totalCount INT)
BEGIN
  SET @userId = userId;
  SET @offset = offset;
  SET @count = count;
  SET @qry = CONCAT("
    SELECT SQL_CALC_FOUND_ROWS *, bookmarkTags(bookmark.id) as tags, 
           bookmarkInFolders(bookmark.id) AS folders,
           linkCount(link.id) as count
    FROM user, bookmark, link
    WHERE bookmark.user_id=? AND
          bookmark.user_id=user.id AND
          bookmark.link_id=link.id AND EXISTS ( 
            SELECT * FROM bookmark_point_marker_idx 
            WHERE bookmark_point_marker_idx.bookmark_id = bookmark.id)
    ORDER BY bookmark.title ASC LIMIT ?,?            
  ");
  PREPARE STMT FROM @qry;
  EXECUTE STMT USING @userId, @offset, @count;
  SELECT FOUND_ROWS() INTO totalCount;
END//
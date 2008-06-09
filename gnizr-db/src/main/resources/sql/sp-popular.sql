delimiter //
###############################################################
# PROCEDURE: getPopularBookmarks
# INPUT: inPastDays, count
# OUTPUT: NONE
DROP PROCEDURE IF EXISTS getPopularBookmarks//
CREATE PROCEDURE getPopularBookmarks(IN start DATETIME, 
                                     IN end DATETIME,
                                     IN maxCount INT)
BEGIN
  SET @start = start;
  SET @end = end;
  SET @maxCount = maxCount;
  PREPARE STMT FROM "
  SELECT *, bookmarkTags(bookmark.id) as tags, null as folders
    FROM link, user,
    (SELECT *, count(*) as count FROM bookmark
      WHERE ? <= bookmark.last_updated 
            AND bookmark.last_updated < ?
            GROUP BY bookmark.link_id
            ORDER BY count DESC LIMIT ?) AS bookmark
    WHERE link.id = bookmark.link_id 
          AND user.id = bookmark.user_id";
   EXECUTE STMT USING @start, @end, @maxCount;
END//
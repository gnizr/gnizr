delimiter //
###############################################################
# PROCEDURE: getPopularBookmarks
# INPUT: inPastDays, count
# OUTPUT: NONE
DROP PROCEDURE IF EXISTS getPopularBookmarks//
CREATE PROCEDURE getPopularBookmarks(IN inPastDays INT, 
                                     IN maxCount INT)
BEGIN
  SET @inPastDays = inPastDays;
  SET @maxCount = maxCount;
  PREPARE STMT FROM "
  SELECT *, bookmarkTags(bookmark.id) as tags, null as folders
    FROM link, user,
    (SELECT *, count(*) as count FROM bookmark
      WHERE bookmark.last_updated > SUBDATE(NOW(), INTERVAL ? DAY)
            AND bookmark.last_updated < NOW()
            GROUP BY bookmark.link_id
            ORDER BY count DESC LIMIT ?) AS bookmark
    WHERE link.id = bookmark.link_id 
          AND user.id = bookmark.user_id";
   EXECUTE STMT USING @inPastDays, @maxCount;
END//
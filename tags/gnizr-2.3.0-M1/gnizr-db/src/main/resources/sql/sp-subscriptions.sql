delimiter //
###############################################################
# FUNCTION: feedImportFolders
# INPUT: feedId
# OUTPUT: NONE
DROP FUNCTION IF EXISTS feedImportFolders//
CREATE FUNCTION feedImportFolders(feedId INT) 
RETURNS TEXT
READS SQL DATA
BEGIN
  DECLARE folders TEXT;
  SELECT GROUP_CONCAT(DISTINCT folder.folder_name SEPARATOR '/') INTO folders
    FROM feed_folder, folder
    WHERE feed_folder.feed_id=feedId 
    AND feed_folder.folder_id = folder.id;
  RETURN folders;
END//
###############################################################
# PROCEDURE: createSubscription
# INPUT: bookmarkId, lastSync, matchText, autoImport
# OUTPUT: id
DROP PROCEDURE IF EXISTS createSubscription//
CREATE PROCEDURE createSubscription(IN bookmarkId INT,
                                    IN lastSync DATETIME,
                                    IN matchText TEXT,
                                    IN autoImport BOOLEAN,
                                    IN pubDate DATETIME,
                                    OUT id INT)
BEGIN
 DECLARE EXIT HANDLER FOR 1062 
    SELECT feed_subscription.id INTO id FROM feed_subscription
      WHERE feed_subscription.bookmark_id = bookmarkId;         
            
  INSERT INTO feed_subscription(bookmark_id,last_sync,match_text,auto_import,pub_date) 
    VALUES (bookmarkId,lastSync,matchText,autoImport,pubDate);
  SELECT LAST_INSERT_ID() INTO id;      
END//
###############################################################
# PROCEDURE: getSubscriptionById
# INPUT: bookmarkId
# OUTPUT: NONE
DROP PROCEDURE IF EXISTS getSubscriptionById//
CREATE PROCEDURE getSubscriptionById (IN id INT)
BEGIN
 SELECT *, bookmarkTags(bookmark.id) AS tags,
   bookmarkInFolders(bookmark.id) AS folders,  
   feedImportFolders(feed_subscription.id) AS importFolders,
   linkCount(link.id) AS count
   FROM bookmark, user, link,
       (SELECT * FROM feed_subscription 
          WHERE feed_subscription.id = id) as feed_subscription
   WHERE bookmark.id = feed_subscription.bookmark_id AND
         bookmark.link_id = link.id AND
         bookmark.user_id = user.id;
END//
###############################################################
# PROCEDURE: getSubscriptionByUserIdFeedUrl
# INPUT: userId, feedUrl
# OUTPUT: NONE
DROP PROCEDURE IF EXISTS getSubscriptionByUserIdFeedUrl//
CREATE PROCEDURE getSubscriptionByUserIdFeedUrl (IN userId INT, 
                                                 IN feedUrl TEXT)
BEGIN
SELECT *, bookmarkTags(bookmark.id) AS tags, 
  bookmarkInFolders(bookmark.id) AS folders,  
  feedImportFolders(feed_subscription.id) AS importFolders,
  linkCount(link.id) AS count
  FROM bookmark, user, link,
  (SELECT * FROM feed_subscription 
   WHERE feed_subscription.bookmark_id = 
   (SELECT bm.id FROM bookmark as bm, link as ln
    WHERE ln.url_hash = md5(feedUrl) AND 
          bm.link_id = ln.id AND
          bm.user_id = userId)   
  ) as feed_subscription
  WHERE bookmark.id = feed_subscription.bookmark_id AND
       bookmark.link_id = link.id AND
       bookmark.user_id = user.id;
END//
###############################################################
# PROCEDURE: deleteSubscriptionByUserIdFeedUrl
# INPUT: userId, feedUrl
# OUTPUT: NONE
DROP PROCEDURE IF EXISTS deleteSubscriptionByUserIdFeedUrl//
CREATE PROCEDURE deleteSubscriptionByUserIdFeedUrl (IN ownerId INT,
                                                    IN feedUrl TEXT)
BEGIN
 DELETE feed_subscription FROM feed_subscription, bookmark, link
 WHERE link.url_hash = md5(feedUrl) AND
       link.id = bookmark.link_id AND
       bookmark.user_id = ownerId AND
       bookmark.id = feed_subscription.bookmark_id;
END//
###############################################################
# PROCEDURE: updateSubscription
# INPUT: id, bookmarkId, lastSync, matchText,autoImport
# OUTPUT: NONE
DROP PROCEDURE IF EXISTS updateSubscription//
CREATE PROCEDURE updateSubscription(IN id INT,
		                                IN bookmarkId INT,
          	   	                        IN lastSync DATETIME,
                     		            IN matchText TEXT,
                               			IN autoImport BOOLEAN,
                               			IN pubDate DATETIME)
BEGIN
  UPDATE feed_subscription f SET
   f.bookmark_id = bookmarkId,
   f.last_sync = lastSync,
   f.match_text = matchText,
   f.auto_import = autoImport,
   f.pub_date = pubDate
  WHERE f.id = id;
END//
###############################################################
# PROCEDURE: findImportFolders
# INPUT: feedId
# OUTPUT: NONE
DROP PROCEDURE IF EXISTS findImportFolders//
CREATE PROCEDURE findImportFolders(IN feedId INT)
BEGIN
  SELECT * FROM feed_folder, 
  (SELECT * FROM folder LEFT JOIN 
    (SELECT folder_id, count(*) as folder_size FROM bookmark_folder 
     GROUP BY bookmark_folder.folder_id) as bookmark_folder
    ON folder.id = bookmark_folder.folder_id) as folder, user as owner
  WHERE
    feed_folder.feed_id = feedId AND
    feed_folder.folder_id = folder.id AND
    folder.owner_id = owner.id;
END//
###############################################################
# PROCEDURE: addImportFolder
# INPUT: feedId, folderId
# OUTPUT: NONE
DROP PROCEDURE IF EXISTS addImportFolder//
CREATE PROCEDURE addImportFolder(IN feedId INT, 
                                 IN folderId INT) 
BEGIN
  INSERT IGNORE INTO feed_folder(feed_id,folder_id) 
    VALUES (feedId, folderId);
END//
###############################################################
# PROCEDURE: removeImportFolder
# INPUT: feedId, folderId
# OUTPUT: NONE
DROP PROCEDURE IF EXISTS removeImportFolder//
CREATE PROCEDURE removeImportFolder(IN feedId INT, 
                                    IN folderId INT) 
BEGIN
  DELETE FROM feed_folder WHERE 
    feed_folder.feed_id = feedId AND
    feed_folder.folder_id = folderId;
END//
###############################################################
# PROCEDURE: pageSubscriptionByOwnerId
# INPUT: userId, offset, count
# OUTPUT: totalCount
DROP PROCEDURE IF EXISTS pageSubscriptionByOwnerId//
CREATE PROCEDURE pageSubscriptionByOwnerId (IN userId INT,
                                            IN offset INT,
                                            IN count INT,
                                            OUT totalCount INT)
BEGIN
  SET @offset = offset;
  SET @count = count;
  SET @userId = userId;
  PREPARE STMT FROM "
  SELECT SQL_CALC_FOUND_ROWS *, 
    bookmarkTags(bookmark.id) AS tags,
    bookmarkInFolders(bookmark.id) AS folders,
    feedImportFolders(feed_subscription.id) AS importFolders,
    linkCount(link.id) AS count FROM 
    (SELECT bookmark.id, bookmark.user_id, bookmark.link_id, 
            bookmark.title, bookmark.notes,
            bookmark.created_on, bookmark.last_updated,
            bookmarkTags(bookmark.id) as tags
     FROM bookmark, feed_subscription 
     WHERE bookmark.user_id = ? AND
           bookmark.id = feed_subscription.bookmark_id) as bookmark, 
     link, user, feed_subscription
  WHERE 
     bookmark.id = feed_subscription.bookmark_id AND
     bookmark.link_id = link.id AND
     bookmark.user_id = user.id     
     LIMIT ?, ?;
  ";
  EXECUTE STMT USING @userId, @offset, @count;
  SELECT FOUND_ROWS() INTO totalCount;
END//
###############################################################
# PROCEDURE: findAutoImportSubscription
# INPUT: NONE
# OUTPUT: NONE
DROP PROCEDURE IF EXISTS findAutoImportSubscription//
CREATE PROCEDURE findAutoImportSubscription (IN ageHour INT)
BEGIN
  SELECT *, bookmarkTags(bookmark.id) AS tags, 
   bookmarkInFolders(bookmark.id) AS folders,  
   linkCount(link.id) AS count,
   feedImportFolders(feed_subscription.id) AS importFolders
   FROM bookmark, user, link, 
   (SELECT * FROM feed_subscription WHERE 
       feed_subscription.auto_import = TRUE AND 
       (HOUR(TIMEDIFF(NOW(),last_sync)) > ageHour OR last_sync IS null)    
    ) as feed_subscription
  WHERE feed_subscription.bookmark_id = bookmark.id AND
        bookmark.link_id = link.id AND
        bookmark.user_id = user.id;
END//
###############################################################
# PROCEDURE: 
# INPUT: 
# OUTPUT: NONE
#BEGIN
#END//

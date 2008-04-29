delimiter //
###############################################################
# FUNCTION: bookmarkTags
# INPUT: bookmarkId
# OUTPUT: NONE
DROP FUNCTION IF EXISTS bookmarkTags//
CREATE FUNCTION bookmarkTags(bookmarkId INT) 
RETURNS TEXT
READS SQL DATA
BEGIN
  DECLARE tags TEXT;
  SELECT GROUP_CONCAT(DISTINCT tag.tag 
                      ORDER BY bookmark_tag_idx.position 
                      SEPARATOR ' ') INTO tags 
    FROM tag, bookmark_tag_idx
    WHERE bookmark_tag_idx.bookmark_id = bookmarkId
    AND bookmark_tag_idx.count > 0
    AND bookmark_tag_idx.tag_id = tag.id;
  RETURN tags;
END//
###############################################################
# FUNCTION: bookmarkInFolders
# INPUT: bookmarkId
# OUTPUT: NONE
DROP FUNCTION IF EXISTS bookmarkInFolders//
CREATE FUNCTION bookmarkInFolders(bookmarkId INT) 
RETURNS TEXT
READS SQL DATA
BEGIN
  DECLARE fnames TEXT;
  SELECT GROUP_CONCAT(DISTINCT folder.folder_name 
                      ORDER BY folder.folder_name SEPARATOR ',') INTO fnames
    FROM folder, bookmark_folder
    WHERE bookmark_folder.bookmark_id = bookmarkId
    AND bookmark_folder.folder_id = folder.id;
  RETURN fnames;
END//
###############################################################
# PROCEDURE: createBookmark
# INPUT: userId,
#        linkId,
#        title,
#        notes,
#        createdOn,
#        lastUpdated,
#        tags
# OUTPUT: id
DROP PROCEDURE IF EXISTS createBookmark//
CREATE PROCEDURE createBookmark(IN userId INT,
                                IN linkId INT,
                                IN title TEXT,
                                IN notes TEXT,
                                IN createdOn DATETIME,
                                IN lastUpdated DATETIME,
                                IN tags TEXT,								
                                OUT id INT)
BEGIN
  DECLARE EXIT HANDLER FOR 1062 
    SELECT bookmark.id INTO id FROM bookmark 
      WHERE bookmark.link_id = linkId AND
            bookmark.user_id = userId;
            
  INSERT INTO bookmark(user_id,link_id,title,notes,created_on,last_updated) 
    VALUES (userId,linkId,title,notes,createdOn,lastUpdated);
    SELECT LAST_INSERT_ID() INTO id;
    
  # Add to search_idx table for FULLTEXT search
  INSERT IGNORE INTO search_idx(bookmark_id,text,tags) VALUES (id,CONCAT(title,",",notes),tags);
END//

###############################################################
# PROCEDURE: deleteBookmark
# INPUT: id
# OUTPUT: NONE
DROP PROCEDURE IF EXISTS deleteBookmark//
CREATE PROCEDURE deleteBookmark(id INT)
BEGIN
  DELETE point_marker 
    FROM bookmark_point_marker_idx, point_marker
    WHERE bookmark_point_marker_idx.bookmark_id = id AND
          bookmark_point_marker_idx.point_marker_id = point_marker.id;

  # deletes bookmark records from bookmark table and search_idx at the same time
  DELETE bookmark, search_idx
    FROM bookmark LEFT JOIN search_idx
    ON bookmark.id = search_idx.bookmark_id 
    WHERE bookmark.id=id;
END//
###############################################################
# PROCEDURE: getBookmark
# INPUT: id
# OUTPUT: NONE
DROP PROCEDURE IF EXISTS getBookmark//
CREATE PROCEDURE getBookmark(id INT)
BEGIN
  SELECT *, bookmarkTags(bookmark.id) as tags, linkCount(link.id) as count,
         bookmarkInFolders(bookmark.id) as folders
     FROM bookmark, link, user
     WHERE bookmark.id=id AND
           bookmark.link_id = link.id AND
           bookmark.user_id = user.id;
END//
###############################################################
# PROCEDURE: pageBookmarksUserId
# INPUT: userId,
#        offset,
#        count
# OUTPUT: totalCount
DROP PROCEDURE IF EXISTS pageBookmarksUserId//
CREATE PROCEDURE pageBookmarksUserId(IN userId INT,
                                     IN offset INT,
                                     IN count INTEGER, 
                                     OUT totalCount INT,
                                     IN sortBy INT,
                                     IN orderFlag INT)
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
            bookmark.link_id=link.id
   ");

   IF (sortBy = 1) THEN
     SET @qry = CONCAT(@qry, " ORDER BY bookmark.created_on ");
   ELSE 
     SET @qry = CONCAT(@qry, " ORDER BY bookmark.last_updated ");
   END IF;
   
   IF (orderFlag = 1) THEN
     SET @qry = CONCAT(@qry, " DESC LIMIT ?,?");
   ELSE
     SET @qry = CONCAT(@qry, " ASC LIMIT ?,?");
   END IF;
  
  PREPARE STMT FROM @qry;  
  EXECUTE STMT USING @userId,@offset,@count;
  SELECT FOUND_ROWS() INTO totalCount;
END//
###############################################################
# PROCEDURE: pageBookmarksLinkId
# INPUT: linkId,
#        offset,
#        count
# OUTPUT: totalCount
DROP PROCEDURE IF EXISTS pageBookmarksLinkId//
CREATE PROCEDURE pageBookmarksLinkId(IN linkId INT,
                                     IN offset INT,
                                     IN count INT,
                                     OUT totalCount INT)
BEGIN
  SET @linkId = linkId;
  SET @offset = offset;
  SET @count = count;  
  PREPARE STMT FROM "
    SELECT SQL_CALC_FOUND_ROWS *, bookmarkTags(bookmark.id) as tags, 
      bookmarkInFolders(bookmark.id) AS folders,  
      linkCount(link.id) as count 
      FROM bookmark, link, user
      WHERE bookmark.link_id=? AND
            bookmark.user_id = user.id AND
            bookmark.link_id=link.id
      ORDER BY bookmark.created_on DESC LIMIT ?,?";
  EXECUTE STMT USING @linkId,@offset,@count;
  SELECT FOUND_ROWS() INTO totalCount;
END//
###############################################################
# PROCEDURE: pageBookmarksTagId
# INPUT: tagId,
#        offset,
#        count
# OUTPUT: totalCount
DROP PROCEDURE IF EXISTS pageBookmarksTagId//
CREATE PROCEDURE pageBookmarksTagId(IN tagId INT,
                                    IN offset INT,
                                    IN count INT,
                                    OUT totalCount INT)
BEGIN  
  SET @tagId = tagId;
  SET @offset = offset;
  SET @count = count;
  
  PREPARE STMT FROM "
   SELECT SQL_CALC_FOUND_ROWS *, bookmarkTags(bookmark.id) as tags, 
     bookmarkInFolders(bookmark.id) AS folders,  
     linkCount(link.id) as count
     FROM bookmark, link, user 
     WHERE bookmark.id IN 
     (SELECT bookmark_tag_idx.bookmark_id 
       FROM bookmark_tag_idx 
       WHERE bookmark_tag_idx.tag_id=? AND
             bookmark_tag_idx.count > 0) AND
     bookmark.link_id = link.id AND
     bookmark.user_id = user.id
   GROUP BY bookmark.link_id
   ORDER BY bookmark.created_on DESC LIMIT ?,?";
  EXECUTE STMT USING @tagId,@offset,@count;  
  SELECT FOUND_ROWS() INTO totalCount;
END//
###############################################################
# PROCEDURE: pageBookmarksUserIdTagId
# INPUT: userId,
#        tagId,
#        offset,
#        count
# OUTPUT: totalCount
DROP PROCEDURE IF EXISTS pageBookmarksUserIdTagId//
CREATE PROCEDURE pageBookmarksUserIdTagId(IN userId INT,
					 					  IN tagId INT,
                                          IN offset INT,
                                          IN count INT,
                                          OUT totalCount INT,
                                          IN sortBy INT,
                                          IN orderFlag INT)
BEGIN
 SET @userId = userId;
 SET @tagId = tagId;
 SET @offset = offset;
 SET @count = count; 

 SET @qry = CONCAT("
   SELECT SQL_CALC_FOUND_ROWS *, bookmarkTags(bookmark.id) as tags, 
      bookmarkInFolders(bookmark.id) AS folders,  
     linkCount(link.id) AS count 
     FROM bookmark, link, user 
     WHERE bookmark.id IN 
     (SELECT bookmark_tag_idx.bookmark_id 
       FROM bookmark_tag_idx 
       WHERE bookmark_tag_idx.tag_id=? AND
             bookmark_tag_idx.count > 0) AND
     bookmark.link_id = link.id AND
     bookmark.user_id = ? AND
     bookmark.user_id = user.id
   GROUP BY bookmark.link_id 
  ");

 IF (sortBy = 1) THEN
   SET @qry = CONCAT(@qry, " ORDER BY bookmark.created_on ");
 ELSE 
   SET @qry = CONCAT(@qry, " ORDER BY bookmark.last_updated ");
 END IF;

 IF (orderFlag = 1) THEN
   SET @qry = CONCAT(@qry, " DESC LIMIT ?,?");
 ELSE
   SET @qry = CONCAT(@qry, " ASC LIMIT ?,?"); 
 END IF;   
 
  PREPARE STMT FROM @qry;
  EXECUTE STMT USING @tagId,@userId,@offset,@count;   
 SELECT FOUND_ROWS() INTO totalCount;
END//
###############################################################
# PROCEDURE: findBookmark
# INPUT: userId, linkId
# OUTPUT: NONE
DROP PROCEDURE IF EXISTS findBookmark//
CREATE PROCEDURE findBookmark(userId INT,
                              linkId INT)
BEGIN
  SET @userId = userId;
  SET @linkId = linkId;
  PREPARE STMT FROM "
    SELECT *, bookmarkTags(bookmark.id) AS tags, 
      bookmarkInFolders(bookmark.id) AS folders,  
      linkCount(link.id) AS count
      FROM bookmark, link, user
      WHERE bookmark.user_id = ? 
          AND bookmark.link_id = ? 
          AND link.id = bookmark.link_id 
          AND user.id = bookmark.user_id
          ";
  EXECUTE STMT USING @userId,@linkId;    
END//
###############################################################
# PROCEDURE: updateBookmark
# INPUT: id,userId,linkId,title,notes,created_on,
#        last_updated
# OUTPUT: NONE
DROP PROCEDURE IF EXISTS updateBookmark//
CREATE PROCEDURE updateBookmark(id INT,
                                userId INT,
                                linkId INT,
                                title TEXT,
                                notes TEXT,
                                created_on DATETIME,
                                last_updated DATETIME,
                                tags TEXT)
BEGIN
   UPDATE bookmark, search_idx SET 
     bookmark.user_id = userId,
     bookmark.link_id = linkId,
     bookmark.title = title,
     bookmark.notes = notes,
     bookmark.created_on = created_on,
     bookmark.last_updated = last_updated,
	 search_idx.text = CONCAT(title,",",notes),
	 search_idx.tags = tags
   WHERE 
     bookmark.id = id AND
     bookmark.id = search_idx.bookmark_id;
END//
###############################################################
# PROCEDURE: pageCommunityBookmarkSearch
# INPUT: searchQuery, offset, count
# OUTPUT: totalCount
DROP PROCEDURE IF EXISTS pageCommunityBookmarkSearch//
CREATE PROCEDURE pageCommunityBookmarkSearch(IN searchQuery TEXT,
                                        IN offset INT,
                                        IN count INT,
                                        OUT totalCount INT)
BEGIN
  SET @searchQuery = searchQuery;
  SET @offset = offset;
  SET @count = count;
  PREPARE STMT FROM "
   SELECT SQL_CALC_FOUND_ROWS *, 
          bookmarkTags(bookmark.id) as tags, 
          bookmarkInFolders(bookmark.id) AS folders,  
          linkCount(link.id) as count 
     FROM bookmark, link, user 
     WHERE bookmark.id IN  
     (SELECT search_idx.bookmark_id FROM search_idx
       WHERE MATCH(search_idx.text, search_idx.tags) AGAINST (? IN BOOLEAN MODE)) AND
     bookmark.link_id = link.id AND
     bookmark.user_id = user.id
     GROUP BY bookmark.link_id
     LIMIT ?, ?;
  ";
#  PREPARE STMT FROM "
#   SELECT SQL_CALC_FOUND_ROWS * FROM bookmark, user, sav_link as link,
#   (SELECT search_idx.bookmark_id as matched_bm_id, MATCH(search_idx.text, search_idx.tags) AGAINST (? IN BOOLEAN MODE) as score
#    FROM search_idx
#    WHERE MATCH(search_idx.text, search_idx.tags) AGAINST (? IN BOOLEAN MODE)) as matched_bookmarks
#   WHERE bookmark.id = matched_bookmarks.matched_bm_id AND
#         bookmark.link_id = link.id AND
#         bookmark.user_id = user.id
#   GROUP BY bookmark.link_id
#   ORDER BY matched_bookmarks.score DESC LIMIT ?, ?;
#  ";        
  EXECUTE STMT USING @searchQuery,@offset,@count;
  SELECT FOUND_ROWS() INTO totalCount;
END//
###############################################################
# PROCEDURE: pageUserBookmarkSearch
# INPUT: searchQuery, userId, offset, count
# OUTPUT: totalCount
DROP PROCEDURE IF EXISTS pageUserBookmarkSearch//
CREATE PROCEDURE pageUserBookmarkSearch(IN searchQuery TEXT,
                                        IN userId INT,
                                        IN offset INT,
                                        IN count INT,
                                        OUT totalCount INT)
BEGIN
  SET @searchQuery = searchQuery;
  SET @offset = offset;
  SET @count = count;
  SET @userId = userId;
  PREPARE STMT FROM "
   SELECT SQL_CALC_FOUND_ROWS *, 
          bookmarkTags(bookmark.id) as tags, 
          bookmarkInFolders(bookmark.id) AS folders,            
          linkCount(link.id) as count 
     FROM bookmark, link, user 
     WHERE bookmark.id IN  
     (SELECT search_idx.bookmark_id FROM search_idx
       WHERE MATCH(search_idx.text, search_idx.tags) AGAINST (? IN BOOLEAN MODE)) AND
     bookmark.link_id = link.id AND
     bookmark.user_id = ? AND
     bookmark.user_id = user.id 
     GROUP BY bookmark.link_id
     LIMIT ?, ?;
  ";  
#  PREPARE STMT FROM "
#   SELECT SQL_CALC_FOUND_ROWS * FROM bookmark, user, sav_link as link,
#   (SELECT search_idx.bookmark_id as matched_bm_id, MATCH(search_idx.text, search_idx.tags) AGAINST (? IN BOOLEAN MODE) as score
#    FROM search_idx
#    WHERE MATCH(search_idx.text, search_idx.tags) AGAINST (? IN BOOLEAN MODE)) as matched_bookmarks
#   WHERE bookmark.id = matched_bookmarks.matched_bm_id AND
#         bookmark.link_id = link.id AND
#         bookmark.user_id = ? AND
#         bookmark.user_id = user.id
#   GROUP BY bookmark.link_id
#   ORDER BY matched_bookmarks.score DESC
#   LIMIT ?, ?; 
#  ";        
  EXECUTE STMT USING @searchQuery,@userId,@offset,@count;
  SELECT FOUND_ROWS() INTO totalCount;
END//
###############################################################
# PROCEDURE: pageGeotaggedBookmarkUserId
# INPUT: userId, offset, count
# OUTPUT: NONE
DROP PROCEDURE IF EXISTS pageGeotaggedBookmarkUserId//
CREATE PROCEDURE pageGeotaggedBookmarkUserId(IN userId INT,
                                             IN offset INT,
                                             IN count INT)
BEGIN
  SET @userId = userId;
  SET @offset = offset;
  SET @count = count;
  PREPARE STMT FROM
  "
  SELECT *, asWKB(geo) AS geoWKB,  
          bookmarkTags(bookmark.id) AS tags,  
          bookmarkInFolders(bookmark.id) AS folders,              			
          linkCount(link.id) AS count 
    FROM geo_bookmark as bookmark, link, user, tag
    WHERE bookmark.user_id = ? AND
          bookmark.link_id = link.id AND
          bookmark.user_id = user.id AND
          tag.id = bookmark.tag_id
    ORDER BY bookmark.created_on DESC LIMIT ?,?
  ";
  EXECUTE STMT USING @userId, @offset, @count;  
END//
###############################################################
# PROCEDURE: getGeotaggedBookmarkCountUserId
# INPUT: userId
# OUTPUT: count
DROP PROCEDURE IF EXISTS getGeotaggedBookmarkCountUserId//
CREATE PROCEDURE getGeotaggedBookmarkCountUserId(IN userId INT,                                             
                                                 OUT count INT)
BEGIN
SELECT count(*) INTO count
 FROM geo_bookmark as bookmark
 WHERE bookmark.user_id = userId;     
END//
###############################################################
# PROCEDURE: 
# INPUT: 
# OUTPUT: 
#BEGIN
#END//
###############################################################
# PROCEDURE: 
# INPUT: 
# OUTPUT: 
#BEGIN
#END//
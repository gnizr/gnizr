delimiter //
###############################################################
# PROCEDURE: createFolder
# INPUT: userId, folderName, description
# OUTPUT: id
DROP PROCEDURE IF EXISTS createFolder//
CREATE PROCEDURE createFolder(IN userId INT,
                              IN folderName VARCHAR(45),
                              IN description TEXT,
                              IN lastUpdated DATETIME,
                              OUT id INT)
BEGIN
 DECLARE EXIT HANDLER FOR 1062 
    SELECT folder.id INTO id FROM folder 
      WHERE folder.owner_id = userId AND
            folder.folder_name = folderName;
            
  INSERT INTO folder(owner_id,folder_name,description,last_updated) 
    VALUES (userId,folderName,description,lastUpdated);
  SELECT LAST_INSERT_ID() INTO id;
END//
###############################################################
# PROCEDURE: updateFolder
# INPUT: id, ownerId, folderName, description
# OUTPUT: NONE
DROP PROCEDURE IF EXISTS updateFolder//
CREATE PROCEDURE updateFolder(IN id INT,
                              IN ownerId INT,
                              IN folderName VARCHAR(45),
                              IN description TEXT,
                              IN lastUpdated DATETIME)
BEGIN
  UPDATE folder SET
   folder.owner_id = ownerId,
   folder.folder_name = folderName,
   folder.description = description,
   folder.last_updated = lastUpdated
  WHERE folder.id = id;
END//
###############################################################
# PROCEDURE: deleteFolderByOwnerIdFolderName
# INPUT: ownerId, folderName
# OUTPUT: NONE
DROP PROCEDURE IF EXISTS deleteFolderByOwnerIdFolderName//
CREATE PROCEDURE deleteFolderByOwnerIdFolderName (IN ownerId INT,
                                                  IN folderName VARCHAR(45))
BEGIN
 DELETE folder, bookmark_folder
    FROM folder LEFT JOIN bookmark_folder 
    ON folder.id = bookmark_folder.folder_id 
    WHERE folder.owner_id = ownerId AND folder.folder_name = folderName;
END//
###############################################################
# PROCEDURE: getFolderById
# INPUT: id
# OUTPUT: NONE
DROP PROCEDURE IF EXISTS getFolderById//
CREATE PROCEDURE getFolderById (IN id INT)
BEGIN
  SELECT *, folderSize(folder.id) as folder_size FROM user AS owner, folder
    WHERE folder.id = id AND folder.owner_id = owner.id; 
END//
###############################################################
# PROCEDURE: getFolderByOwnerIdFolderName
# INPUT: ownerId, folderName
# OUTPUT: NONE
DROP PROCEDURE IF EXISTS getFolderByOwnerIdFolderName//
CREATE PROCEDURE getFolderByOwnerIdFolderName (IN ownerId INT,
                                               IN folderName VARCHAR(45))
BEGIN
  SELECT *, folderSize(folder.id) as folder_size 
    FROM user AS owner, folder WHERE 
    folder.owner_id = ownerId AND
    folder.owner_id = owner.id AND
    folder.folder_name = folderName;
END//
###############################################################
# PROCEDURE: pageFoldersByOwnerId
# INPUT: ownerId, offset, count
# OUTPUT: totalCount 
DROP PROCEDURE IF EXISTS pageFoldersByOwnerId//
CREATE PROCEDURE pageFoldersByOwnerId (IN ownerId INT,
                                       IN offset INT,
                                       IN count INT,
                                       OUT totalCount INT)
BEGIN
  SET @offset = offset;
  SET @count = count;
  SET @ownerId = ownerId;
  PREPARE STMT FROM "
    SELECT SQL_CALC_FOUND_ROWS *, folderSize(folder.id) as folder_size
      FROM folder, user AS owner WHERE
      folder.owner_id = ? AND
      folder.owner_id = owner.id
    ORDER BY folder.folder_name
    LIMIT ?, ?;
  ";
  EXECUTE STMT USING @ownerId, @offset, @count;
  SELECT FOUND_ROWS() INTO totalCount;
END//
###############################################################
# PROCEDURE: pageBookmarksByFolderId
# INPUT: folderId, offset, count
# OUTPUT: totalCount
DROP PROCEDURE IF EXISTS pageBookmarksByFolderId//
CREATE PROCEDURE pageBookmarksByFolderId (IN folderId INT,
                                          IN offset INT,
                                          IN count INT,
                                          OUT totalCount INT,
                                          IN sortBy INT,
                                          IN orderFlag INT)
BEGIN
  SET @offset = offset;
  SET @count = count;
  SET @folderId = folderId;
  SET @qry = CONCAT("
   SELECT SQL_CALC_FOUND_ROWS *, bookmarkTags(bookmark.id) AS tags, 
     bookmarkInFolders(bookmark.id) AS folders,  
     linkCount(link.id) AS count
     FROM link, user, bookmark, bookmark_folder
     WHERE bookmark_folder.folder_id = ? AND
       bookmark_folder.bookmark_id = bookmark.id AND
       bookmark.link_id = link.id AND
       bookmark.user_id = user.id       
  ");

  IF (sortBy = 1) THEN
    SET @qry = CONCAT(@qry," ORDER BY bookmark.created_on ");
  ELSEIF (sortBy = 2) THEN
    SET @qry = CONCAT(@qry," ORDER BY bookmark.last_updated ");
  ELSE
    SET @qry = CONCAT(@qry," ORDER BY bookmark_folder.last_updated ");   
  END IF;
  
  IF(orderFlag = 1) THEN
    SET @qry = CONCAT(@qry, " DESC LIMIT ?,?");
  ELSE
    SET @qry = CONCAT(@qry, " ASC LIMIT ?,?");
  END IF;
  
  PREPARE STMT FROM @qry;
  EXECUTE STMT USING @folderId, @offset, @count;
  SELECT FOUND_ROWS() INTO totalCount;
END//
###############################################################
# PROCEDURE: pageBookmarksByFolderIdTagId
# INPUT: folderId, offset, count, sortBy, orderFlag
# OUTPUT: totalCount
DROP PROCEDURE IF EXISTS pageBookmarksByFolderIdTagId//
CREATE PROCEDURE pageBookmarksByFolderIdTagId(IN folderId INT,
										  IN tagId INT,
                                          IN offset INT,
                                          IN count INT,
                                          OUT totalCount INT,
                                          IN sortBy INT,
                                          IN orderFlag INT)
BEGIN
  SET @offset = offset;
  SET @count = count;
  SET @folderId = folderId;
  SET @tagId = tagId;
  SET @qry = CONCAT("
   SELECT SQL_CALC_FOUND_ROWS *, bookmarkTags(bookmark.id) AS tags, 
     bookmarkInFolders(bookmark.id) AS folders,  
     linkCount(link.id) AS count
     FROM link, user, bookmark, bookmark_folder, bookmark_tag_idx
     WHERE bookmark_folder.folder_id = ? AND
       bookmark_folder.bookmark_id = bookmark.id AND     
       bookmark_tag_idx.tag_id = ? AND
       bookmark_tag_idx.count > 0 AND
       bookmark_tag_idx.bookmark_id = bookmark.id AND
       bookmark.link_id = link.id AND
       bookmark.user_id = user.id
  ");

  IF (sortBy = 1) THEN
    SET @qry = CONCAT(@qry," ORDER BY bookmark.created_on ");
  ELSEIF (sortBy = 2) THEN
    SET @qry = CONCAT(@qry," ORDER BY bookmark.last_updated ");
  ELSE
    SET @qry = CONCAT(@qry," ORDER BY bookmark_folder.last_updated ");   
  END IF;
  
  IF(orderFlag = 1) THEN
    SET @qry = CONCAT(@qry, " DESC LIMIT ?,?");
  ELSE
    SET @qry = CONCAT(@qry, " ASC LIMIT ?,?");
  END IF;
  PREPARE STMT FROM @qry;
  EXECUTE STMT USING @folderId, @tagId, @offset, @count;
  SELECT FOUND_ROWS() INTO totalCount;
END//
###############################################################
# PROCEDURE: addBookmarkToFolder
# INPUT: folderId,bookmarkId,lastUpdated
# OUTPUT: NONE
DROP PROCEDURE IF EXISTS addBookmarkToFolder//
CREATE PROCEDURE addBookmarkToFolder(IN folderId INT,
                                     IN bookmarkId INT,
                                     IN lastUpdated DATETIME)
BEGIN 
 DECLARE `Constraint Violation` CONDITION FOR SQLSTATE '23000';
 DECLARE EXIT HANDLER FOR `Constraint Violation` ROLLBACK; 
 
 START TRANSACTION;  
  INSERT INTO bookmark_folder(folder_id,bookmark_id,last_updated) 
    VALUES (folderId,bookmarkId,lastUpdated);
    
  # initialize records that don't exsit in the table.
  INSERT INTO folder_tag_idx(folder_id,tag_id,count)
   SELECT folderId, bookmark_tag_idx.tag_id, 0 FROM bookmark_tag_idx
    WHERE bookmark_tag_idx.bookmark_id = bookmarkId AND
          bookmark_tag_idx.tag_id NOT IN (
            SELECT folder_tag_idx.tag_id FROM folder_tag_idx
              WHERE folder_tag_idx.folder_id = folderId);
  
  UPDATE folder_tag_idx 
    SET folder_tag_idx.count = folder_tag_idx.count + 1
    WHERE folder_tag_idx.folder_id = folderId AND
          folder_tag_idx.tag_id IN (
          SELECT bookmark_tag_idx.tag_id FROM bookmark_tag_idx
            WHERE bookmark_tag_idx.bookmark_id = bookmarkId AND
                  bookmark_tag_idx.count > 0);    
  COMMIT;
END//
###############################################################
# PROCEDURE: removeBookmarkFromFolder
# INPUT: folderId, bookmarkId
# OUTPUT: NONE
DROP PROCEDURE IF EXISTS removeBookmarkFromFolder//
CREATE PROCEDURE removeBookmarkFromFolder(IN folderId INT,
                                          IN bookmarkId INT)                                         
BEGIN
  START TRANSACTION;    
  DELETE FROM bookmark_folder WHERE 
    bookmark_folder.folder_id = folderId AND
    bookmark_folder.bookmark_id = bookmarkId;  
  
  IF (ROW_COUNT() > 0) THEN
    UPDATE folder_tag_idx 
      SET folder_tag_idx.count = folder_tag_idx.count - 1
      WHERE folder_tag_idx.folder_id = folderId AND
            folder_tag_idx.count > 0 AND
            folder_tag_idx.tag_id IN (
            SELECT bookmark_tag_idx.tag_id FROM bookmark_tag_idx
              WHERE bookmark_tag_idx.bookmark_id = bookmarkId AND
                    bookmark_tag_idx.count > 0 );              
  END IF;                  
  COMMIT;
END//
###############################################################
# PROCEDURE: removeAllBookmarksFromFolder
# INPUT: folderId
# OUTPUT: NONE
DROP PROCEDURE IF EXISTS removeAllBookmarksFromFolder//
CREATE PROCEDURE removeAllBookmarksFromFolder(IN folderId INT)
BEGIN
  START TRANSACTION;               
  DELETE FROM folder_tag_idx WHERE folder_tag_idx.folder_id = folderId;
  DELETE FROM bookmark_folder WHERE bookmark_folder.folder_id = folderId;
  SELECT ROW_COUNT();
  COMMIT;
END//
###############################################################
# PROCEDURE: pageContainedInFolders
# INPUT: bookmarkId,offset,count
# OUTPUT: totalCount
DROP PROCEDURE IF EXISTS pageContainedInFolders//
CREATE PROCEDURE pageContainedInFolders(IN bookmarkId INT,
                                        IN offset INT,
                                        IN count INT,
	                                    OUT totalCount INT)
BEGIN
  SET @offset = offset;
  SET @count = count;
  SET @bookmarkId = bookmarkId;
  PREPARE STMT FROM "
    SELECT SQL_CALC_FOUND_ROWS *, folderSize(folder.id) as folder_size 
      FROM folder, user as owner
      WHERE folder.id IN 
      (SELECT bookmark_folder.folder_id 
         FROM bookmark_folder WHERE 
         bookmark_folder.bookmark_id = ?) AND
      folder.owner_id = owner.id
    ORDER BY folder.folder_name    
    LIMIT ?, ?;
  ";
  EXECUTE STMT USING @bookmarkId, @offset, @count;
  SELECT FOUND_ROWS() INTO totalCount;
END//
###############################################################
# FUNCTION: folderSize 
# INPUT: folderId
# OUTPUT: NONE
DROP FUNCTION IF EXISTS folderSize//
CREATE FUNCTION folderSize(folderId INT) 
RETURNS INT
READS SQL DATA
BEGIN
  DECLARE size INT;
  SELECT count(*) INTO size FROM bookmark_folder 
    WHERE bookmark_folder.folder_id = folderId;
  RETURN size;      
END//
###############################################################
# PROCEDURE: hasFolderTag
# INPUT: folderId, tagId
# OUTPUT: 
DROP PROCEDURE IF EXISTS hasFolderTag//
CREATE PROCEDURE hasFolderTag(IN folderId INT,
                              IN tagId INT)
BEGIN
  SELECT * FROM folder_tag_idx WHERE 
    folder_tag_idx.folder_id = folderId AND
    folder_tag_idx.tag_id = tagId;
END//
###############################################################
# PROCEDURE: findAllTagsInFolder
# INPUT: folderId, sortBy, minFreq, orderFlag INT
# OUTPUT: 
DROP PROCEDURE IF EXISTS findAllTagsInFolder//
CREATE PROCEDURE findAllTagsInFolder(IN folderId INT,                                     
                                     IN minFreq INT, 
                                     IN sortBy INT,
                                     IN orderFlag INT)
BEGIN
  SET @folderId = folderId;
  SET @minFreq = minFreq;
  SET @qry = CONCAT("
    SELECT * FROM tag, folder_tag_idx
      WHERE folder_tag_idx.folder_id = ? AND
            folder_tag_idx.tag_id = tag.id ");
     
  SET @qry = CONCAT(@qry, " AND folder_tag_idx.count >= ? "); 
  
  # 1: sort alphabetically  
  # 2: sort by usage freq
  IF (sortBy = 1) THEN
    SET @qry = CONCAT(@qry, " ORDER BY tag.tag ");
  ELSE
    SET @qry = CONCAT(@qry, " ORDER BY folder_tag_idx.count ");
  END IF;
  
  IF(orderFlag = 1) THEN
    SET @qry = CONCAT(@qry, " DESC ");
  ELSE
    SET @qry = CONCAT(@qry, " ASC ");
  END IF;
  PREPARE STMT FROM @qry;
  EXECUTE STMT USING @folderId, @minFreq;
END//
###############################################################
# PROCEDURE: findTagGroupsInFolder
# INPUT: folderId, minFreq, sortBy, orderFlag
# OUTPUT: 
DROP PROCEDURE IF EXISTS findTagGroupsInFolder//
CREATE PROCEDURE findTagGroupsInFolder(IN folderId INT,                                     
                                       IN minFreq INT, 
                                       IN sortBy INT,
                                       IN orderFlag INT)
BEGIN
  SET @folderId = folderId;
  SET @minFreq = minFreq;
  SET @qry = CONCAT("
  (SELECT mem_tag.id AS mem_tag_id, mem_tag.tag AS mem_tag_tag, mem_tag.count AS mem_tag_count,
          grp_tag.id AS grp_tag_id, grp_tag.tag AS grp_tag_tag, grp_tag.count AS grp_tag_count, 
          fti.folder_id AS fti_folder_id, fti.tag_id AS fti_tag_id, fti.count AS fti_count
    FROM tag as mem_tag, tag as grp_tag, 
         user_tag_idx as mem_tag_uti, 
         user_tag_idx as grp_tag_uti, 
         tag_assertion, 
         folder_tag_idx AS fti
    WHERE tag_assertion.user_id=(SELECT folder.owner_id FROM folder where folder.id=?) AND 
          tag_assertion.prpt_id=4 AND 
          tag_assertion.object_id = grp_tag_uti.id AND 
          tag_assertion.subject_id = mem_tag_uti.id AND 
          grp_tag_uti.tag_id = grp_tag.id AND 
          mem_tag_uti.tag_id = mem_tag.id AND
          fti.folder_id = ? AND
          fti.tag_id = mem_tag.id AND
          fti.count >= ? ) ");

  SET @qry = CONCAT(@qry, " UNION ");
  
  SET @qry = CONCAT(@qry, "
  (SELECT mem_tag.id, mem_tag.tag, mem_tag.count, 
          grp_tag.id, grp_tag.tag, grp_tag.count, 
          fti.folder_id, fti.tag_id, fti.count
     FROM tag AS mem_tag,
          (SELECT 0 AS id, '' AS tag, 0 AS count) AS grp_tag,
          folder_tag_idx AS fti
     WHERE fti.folder_id = ? AND
           fti.tag_id=mem_tag.id AND
           fti.count >= ? AND
           mem_tag.id NOT IN (SELECT user_tag_idx.tag_id
            FROM user_tag_idx, tag_assertion, folder
            WHERE folder.id=fti.folder_id AND
                  folder.owner_id = tag_assertion.user_id AND
                  tag_assertion.subject_id = user_tag_idx.id AND
                  tag_assertion.prpt_id = 4)) ");
   
  # 1: sort alphabetically  
  # 2: sort by usage freq
  IF (sortBy = 1) THEN
    SET @qry = CONCAT(@qry, " ORDER BY grp_tag_tag ASC, mem_tag_tag ");
  ELSE
    SET @qry = CONCAT(@qry, " ORDER BY grp_tag_tag ASC, fti_count ");
  END IF; 

  IF(orderFlag = 1) THEN
    SET @qry = CONCAT(@qry, " DESC ");
  ELSE
    SET @qry = CONCAT(@qry, " ASC ");
  END IF;
  PREPARE STMT FROM @qry;
  EXECUTE STMT USING @folderId, @folderId, @minFreq, @folderId, @minFreq;
END//
###############################################################
# PROCEDURE: findTagsUsedInAnyFolder
# INPUT: userId, sortBy, minFreq
# OUTPUT: 
DROP PROCEDURE IF EXISTS findTagsUsedInAnyFolder//
CREATE PROCEDURE findTagsUsedInAnyFolder(IN userId INT,
                                         IN sortBy INT,
                                         IN minFreq INT)
BEGIN
  SELECT *, SUM(folder_tag_idx.count) as totalCount 
   FROM tag, folder_tag_idx
   WHERE folder_tag_idx.folder_id IN (
           SELECT folder.id FROM folder 
             WHERE folder.owner_id = userId ) AND
         folder_tag_idx.tag_id = tag.id
   GROUP BY folder_tag_idx.tag_id;         
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

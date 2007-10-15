delimiter //
##############################################################
# PROCEDURE: createTag
# INPUT: tag
#        count
# OUTPUT: id
DROP PROCEDURE IF EXISTS createTag//
CREATE PROCEDURE `createTag`(IN tag VARCHAR(45),
                             IN count INT,
                             OUT id INT)
BEGIN
  # if encounter duplicated key error,
  # return the current record id
  DECLARE EXIT HANDLER FOR 1062 
    SELECT tag.id INTO id FROM tag WHERE tag.tag=tag;
    
  INSERT INTO tag(tag,count) VALUES (tag,count);
  SELECT LAST_INSERT_ID() INTO id;
END//
###############################################################
# PROCEDURE: getTag
# INPUT: id
# OUTPUT: NONE
DROP PROCEDURE IF EXISTS getTag//
CREATE PROCEDURE `getTag`(id INT)
BEGIN
  SELECT * FROM tag WHERE tag.id=id;
END//
###############################################################
# PROCEDURE: deleteTag 
# INPUT: id
# OUTPUT: NONE
DROP PROCEDURE IF EXISTS deleteTag//
CREATE PROCEDURE deleteTag(id INT)
BEGIN
  DELETE FROM tag WHERE tag.id=id;
END//
###############################################################
# PROCEDURE: updateTag 
# INPUT: id,tag,count
# OUTPUT: NONE
DROP PROCEDURE IF EXISTS updateTag//
CREATE PROCEDURE updateTag(id INT,
                           tag VARCHAR(45),
                           count INT)
BEGIN
  UPDATE tag SET tag.tag = tag, tag.count = count 
    WHERE tag.id=id;
END//

##############################################################
# PROCEDURE: createUserTag
# INPUT: tag
#        count
# OUTPUT: id
DROP PROCEDURE IF EXISTS createUserTag//
CREATE PROCEDURE `createUserTag`(IN uid INT,
                                 IN tid INT,
                                 IN count INT,
                                 OUT id INT)
BEGIN
  DECLARE EXIT HANDLER FOR 1062 
    SELECT user_tag_idx.id INTO id FROM user_tag_idx 
      WHERE user_tag_idx.user_id = uid AND
            user_tag_idx.tag_id = tid;
  INSERT INTO user_tag_idx(user_id,tag_id,count) VALUES (uid,tid,count);
  SELECT LAST_INSERT_ID() INTO id;
END//
###############################################################
# PROCEDURE: getUserTag
# INPUT: id
# OUTPUT: NONE
DROP PROCEDURE IF EXISTS getUserTag//
CREATE PROCEDURE `getUserTag`(id INT)
BEGIN
  SELECT * FROM user_tag_idx, user, tag 
    WHERE user_tag_idx.id=id AND
          user_tag_idx.tag_id = tag.id AND
          user_tag_idx.user_id = user.id;
END//
###############################################################
# PROCEDURE: deleteUserTag 
# INPUT: id
# OUTPUT: NONE
DROP PROCEDURE IF EXISTS deleteUserTag//
CREATE PROCEDURE deleteUserTag(id INT)
BEGIN
  DELETE FROM user_tag_idx WHERE user_tag_idx.id=id;
END//
###############################################################
# PROCEDURE: updateUserTag 
# INPUT: id,userId,tagId,count
# OUTPUT: NONE
DROP PROCEDURE IF EXISTS updateUserTag//
CREATE PROCEDURE updateUserTag(id INT,
                               userId INT,
                               tagId INT,
                               count INT)
BEGIN
  UPDATE user_tag_idx 
    SET user_tag_idx.user_id=userId,
        user_tag_idx.count=count,
        user_tag_idx.tag_id=tagId
    WHERE user_tag_idx.id=id;           
END//
##############################################################
# PROCEDURE: createLinkTag
# INPUT: linkId
#        tagId
#        count
# OUTPUT: id
DROP PROCEDURE IF EXISTS createLinkTag//
CREATE PROCEDURE `createLinkTag`(IN lid INT,
                                 IN tid INT,
                                 IN count INT,
                                 OUT id INT)
BEGIN
  DECLARE EXIT HANDLER FOR 1062 
    SELECT link_tag_idx.id INTO id FROM link_tag_idx WHERE
      link_tag_idx.link_id = lid AND
      link_tag_idx.tag_id = tid;
  INSERT INTO link_tag_idx(link_id,tag_id,count) VALUES (lid,tid,count);
  SELECT LAST_INSERT_ID() INTO id;
END//
###############################################################
# PROCEDURE: getLinkTag
# INPUT: id
# OUTPUT: NONE
DROP PROCEDURE IF EXISTS getLinkTag//
CREATE PROCEDURE `getLinkTag`(id INT)
BEGIN
  SELECT *, linkCount(link.id) AS count
    FROM link_tag_idx, link, tag
    WHERE link_tag_idx.id=id AND
          link_tag_idx.link_id=link.id AND
          link_tag_idx.tag_id=tag.id;      
END//
###############################################################
# PROCEDURE: deleteLinkTag 
# INPUT: id
# OUTPUT: NONE
DROP PROCEDURE IF EXISTS deleteLinkTag//
CREATE PROCEDURE deleteLinkTag(id INT)
BEGIN
  DELETE FROM link_tag_idx WHERE link_tag_idx.id=id;
END//
###############################################################
# PROCEDURE: updateLinkTag 
# INPUT: id,linkId,tagId,count
# OUTPUT: NONE
DROP PROCEDURE IF EXISTS updateLinkTag//
CREATE PROCEDURE updateLinkTag(id INT,
                               linkId INT,
                               tagId INT,
                               count INT)
BEGIN
  UPDATE link_tag_idx 
    SET link_tag_idx.link_id=linkId,
        link_tag_idx.count=count,
        link_tag_idx.tag_id=tagId
    WHERE link_tag_idx.id=id;           
END//

###############################################################
# PROCEDURE: findTag
# INPUT: tag
# OUTPUT: NONE
DROP PROCEDURE IF EXISTS findTag//
CREATE PROCEDURE findTag(tag VARCHAR(45))
BEGIN
  SELECT * FROM tag WHERE tag.tag = tag;
END//
#################################################################
# PROCEDURE: findTagTopNSortByFreq
# INPUT: topN
# OUTPUT: NONE
DROP PROCEDURE IF EXISTS findTagTopNSortByFreq//
CREATE PROCEDURE findTagTopNSortByFreq(topN INTEGER)
BEGIN
  SET @topN=topN;
  PREPARE STMT FROM 
    'SELECT * FROM tag WHERE 
      tag.count > 0 AND tag.tag NOT REGEXP "[[:punct:]]+"
      ORDER BY tag.count DESC LIMIT ? OFFSET 0;';
  EXECUTE STMT USING @topN;  
END//
#################################################################
# PROCEDURE: findTagTopNSortByAlpha
# INPUT: topN
# OUTPUT: NONE
DROP PROCEDURE IF EXISTS findTagTopNSortByAlpha//
CREATE PROCEDURE findTagTopNSortByAlpha(topN INTEGER)
BEGIN
  SET @topN=topN;
  PREPARE STMT FROM 
    'SELECT * FROM 
      (SELECT * FROM tag WHERE 
         tag.count > 0 AND tag.tag NOT REGEXP "[[:punct:]]+"
         ORDER BY tag.count DESC LIMIT ? OFFSET 0) as tag
       ORDER BY tag.tag ASC;';
  EXECUTE STMT USING @topN;  
END//
###############################################################
# PROCEDURE: findLinkTag
# INPUT: linkId,
#        tagId
# OUTPUT: NONE
DROP PROCEDURE IF EXISTS findLinkTag//
CREATE PROCEDURE findLinkTag(linkId INT,
                             tagId INT)
BEGIN
  SELECT *, linkCount(link.id) AS count
    FROM link_tag_idx, link, tag WHERE 
         link_tag_idx.link_id=linkId AND
         link_tag_idx.tag_id=tagId AND
         link_tag_idx.link_id=link.id AND
         link_tag_idx.tag_id=tag.id;
END//
###############################################################
# PROCEDURE: findLinkTagMinFreq
# INPUT: linkId, minFreq        
# OUTPUT: NONE
DROP PROCEDURE IF EXISTS findLinkTagMinFreq//
CREATE PROCEDURE findLinkTagMinFreq(linkId INT,
                                    minFreq INT)
BEGIN
  SELECT *, linkCount(link.id) AS count 
    FROM link_tag_idx, link, tag 
    WHERE link_tag_idx.link_id=linkId AND
          link_tag_idx.count >= minFreq AND
          link_tag_idx.link_id = link.id AND
          link_tag_idx.tag_id = tag.id;
END//
###############################################################
# PROCEDURE: pageLinkTagSortByFreq
# INPUT: linkId      
# OUTPUT: NONE
DROP PROCEDURE IF EXISTS pageLinkTagSortByFreq//
CREATE PROCEDURE pageLinkTagSortByFreq(IN linkId INT, 
                                        IN offset INT,
                                        IN count INT,
                                        OUT totalCount INT)                                    
BEGIN
  SET @offset = offset;
  SET @count = count;
  SET @linkId = linkId;
  PREPARE STMT FROM "
   SELECT SQL_CALC_FOUND_ROWS * , linkCount(link.id) AS count 
     FROM link, tag, 
     (SELECT * FROM link_tag_idx WHERE 
      link_tag_idx.link_id = ? AND
      link_tag_idx.count > 0) as link_tag_idx
   WHERE link.id = link_tag_idx.link_id AND
         tag.id = link_tag_idx.tag_id
   ORDER BY link_tag_idx.count DESC LIMIT ?,?
  ";
  EXECUTE STMT USING @linkId, @offset, @count;   
  SELECT FOUND_ROWS() INTO totalCount;    
END//

###############################################################
# PROCEDURE: findUserTag
# INPUT: userId,
#        tagId
# OUTPUT: NONE
DROP PROCEDURE IF EXISTS findUserTag//
CREATE PROCEDURE findUserTag(userId INT,
                             tagId INT)
BEGIN
  SELECT * FROM user_tag_idx, tag, user WHERE 
    user_tag_idx.user_id=userId AND
    user_tag_idx.tag_id=tagId AND
    user_tag_idx.tag_id=tag.id AND 
    user_tag_idx.user_id=user.id;
END//
###############################################################
# PROCEDURE: findUserTagAll
# INPUT: userId
# OUTPUT: NONE
DROP PROCEDURE IF EXISTS findUserTagAll//
CREATE PROCEDURE findUserTagAll(userId INT)
BEGIN
  SELECT * FROM user_tag_idx, tag, user WHERE 
    user_tag_idx.user_id=userId AND
    user_tag_idx.tag_id=tag.id AND
    user_tag_idx.user_id=user.id 
    ORDER BY tag.tag ASC;
END//
######################################################
# PROCEDURE: findUserTagMinFreqSortAlph
# INPUT: userId,minFreq
# OUTPUT: NONE
DROP PROCEDURE IF EXISTS findUserTagMinFreqSortAlph//
CREATE PROCEDURE findUserTagMinFreqSortAlph(userId INTEGER,
                                    minFreq INTEGER)
BEGIN
  SELECT * FROM user_tag_idx, tag, user WHERE 
    user_tag_idx.user_id=userId AND
    user_tag_idx.count >= minFreq AND
    user_tag_idx.tag_id=tag.id AND
    user_tag_idx.user_id=user.id
    ORDER BY tag.tag ASC;
END//
######################################################
# PROCEDURE: findUserTagMinFreqSortFreq
# INPUT: userId,minFreq
# OUTPUT: NONE
DROP PROCEDURE IF EXISTS findUserTagMinFreqSortFreq//
CREATE PROCEDURE findUserTagMinFreqSortFreq(userId INTEGER,
                                    minFreq INTEGER)
BEGIN
  SELECT * FROM user_tag_idx, tag, user WHERE 
    user_tag_idx.user_id=userId AND
    user_tag_idx.count >= minFreq AND
    user_tag_idx.tag_id = tag.id AND 
    user_tag_idx.user_id=user.id
    ORDER BY user_tag_idx.count DESC;
END//
###############################################################
# PROCEDURE: addTagCountOne
# INPUT: tagId,userId,linkId,bookmarkId,addNum
# OUTPUT: 
DROP PROCEDURE IF EXISTS addTagCountOne//
CREATE PROCEDURE addTagCountOne(tagId INT,
                                userId INT,
                                linkId INT,
                                bookmarkId INT)
BEGIN
  START TRANSACTION;
                      
  UPDATE tag, bookmark_tag_idx, link_tag_idx, user_tag_idx
    SET tag.count = tag.count+1, 
        bookmark_tag_idx.count = bookmark_tag_idx.count+1, 
        link_tag_idx.count = link_tag_idx.count +1,
        user_tag_idx.count = user_tag_idx.count + 1
    WHERE tag.id = tagId AND 
          bookmark_tag_idx.bookmark_id = bookmarkId AND
          bookmark_tag_idx.tag_id = tagId AND
          bookmark_tag_idx.count = 0 AND
          link_tag_idx.link_id = linkId AND
          link_tag_idx.tag_id = tagId AND
          user_tag_idx.user_id = userId AND
          user_tag_idx.tag_id = tagId;        
              
  # initialize records that don't exsit in the table.
  INSERT INTO folder_tag_idx(folder_id,tag_id,count)
   SELECT bookmark_folder.folder_id, tagId, 0 FROM bookmark_folder
    WHERE bookmark_folder.bookmark_id = bookmarkId AND
          bookmark_folder.folder_id NOT IN (
          SELECT folder_tag_idx.folder_id FROM folder_tag_idx
            WHERE folder_tag_idx.tag_id = tagId AND
                  folder_tag_idx.folder_id = bookmark_folder.folder_id);   
                                   
  UPDATE folder_tag_idx 
    SET folder_tag_idx.count = folder_tag_idx.count + 1
    WHERE folder_tag_idx.tag_id = tagId AND
          folder_tag_idx.folder_id IN (
          SELECT bookmark_folder.folder_id FROM bookmark_folder
            WHERE bookmark_folder.bookmark_id = bookmarkId
          );    
 
  COMMIT;
END//
###############################################################
# PROCEDURE: subtractTagCountOne
# INPUT: tagId,userId,linkId,bookmarkId,addNum
# OUTPUT: 
DROP PROCEDURE IF EXISTS subtractTagCountOne//
CREATE PROCEDURE subtractTagCountOne(tagId INT,
                                     userId INT,
                                     linkId INT,
                                     bookmarkId INT)
BEGIN
  START TRANSACTION;
   UPDATE tag, bookmark_tag_idx, link_tag_idx, user_tag_idx
   SET tag.count = tag.count-1, 
       bookmark_tag_idx.count = bookmark_tag_idx.count-1, 
       link_tag_idx.count = link_tag_idx.count-1,
       user_tag_idx.count = user_tag_idx.count-1
   WHERE tag.id = tagId AND 
         bookmark_tag_idx.bookmark_id = bookmarkId AND
         bookmark_tag_idx.tag_id = tagId AND
         bookmark_tag_idx.count = 1 AND
         link_tag_idx.link_id = linkId AND
         link_tag_idx.tag_id = tagId AND
         user_tag_idx.user_id = userId AND
         user_tag_idx.tag_id = tagId; 
            
  UPDATE folder_tag_idx 
    SET folder_tag_idx.count = folder_tag_idx.count - 1
    WHERE folder_tag_idx.tag_id = tagId AND
          folder_tag_idx.folder_id IN (
          SELECT bookmark_folder.folder_id FROM bookmark_folder
            WHERE bookmark_folder.bookmark_id = bookmarkId
          );                 
  COMMIT;         
END//
###############################################################
# PROCEDURE: expandTag
# INPUT: fromTagId, toTagId, userId
# OUTPUT: NONE
DROP PROCEDURE IF EXISTS expandTag//
CREATE PROCEDURE expandTag(fromTagId INT,
                           toTagId INT,
                           userId INT)
BEGIN
  DECLARE CONTINUE HANDLER FOR SQLSTATE '01000' SELECT 'ignore inconsist accounting';
 
  CREATE TEMPORARY TABLE changed_bookmarks_t (bookmark_id INT);
  
  INSERT INTO changed_bookmarks_t(bookmark_id)
    SELECT bookmark_tag_idx.bookmark_id FROM bookmark_tag_idx, bookmark
    WHERE bookmark_tag_idx.tag_id = fromTagId AND
          bookmark_tag_idx.bookmark_id = bookmark.id AND
          bookmark.user_id = userId;
    
  START TRANSACTION;
   
  UPDATE bookmark_tag_idx t1, bookmark_tag_idx t2, bookmark
  SET t1.count = 1
  WHERE bookmark.user_id = userId AND
        bookmark.id = t2.bookmark_id AND
        t2.tag_id = fromTagId AND
        t1.bookmark_id = t2.bookmark_id AND
        t1.tag_id = toTagId;      

  UPDATE user_tag_idx 
  SET user_tag_idx.count = (
    SELECT SUM(bookmark_tag_idx.count)
      FROM bookmark_tag_idx, bookmark
      WHERE bookmark.user_id = userId AND
            bookmark.id = bookmark_tag_idx.bookmark_id AND
            bookmark_tag_idx.tag_id = toTagId
  )
  WHERE user_tag_idx.tag_id = toTagId AND
        user_tag_idx.user_id = userId;

  UPDATE link_tag_idx
  SET link_tag_idx.count = (
    SELECT SUM(bookmark_tag_idx.count)
      FROM bookmark_tag_idx, bookmark
      WHERE bookmark_tag_idx.tag_id = toTagId 
        AND bookmark_tag_idx.bookmark_id = bookmark.id 
        AND bookmark.link_id = link_tag_idx.link_id         
  )
  WHERE link_tag_idx.tag_id = toTagId;    
  
  UPDATE tag 
  SET tag.count = 
    (SELECT SUM(bookmark_tag_idx.count) 
       FROM bookmark_tag_idx
       WHERE bookmark_tag_idx.tag_id = toTagId)
  WHERE tag.id = toTagId;     

  REPLACE folder_tag_idx(folder_id,tag_id,count)
    SELECT folder.id, bookmark_tag_idx.tag_id, SUM(bookmark_tag_idx.count)
      FROM folder, bookmark_folder, bookmark_tag_idx
      WHERE folder.id = bookmark_folder.folder_id AND
            bookmark_folder.bookmark_id = bookmark_tag_idx.bookmark_id AND
            bookmark_tag_idx.tag_id = toTagId
    GROUP by folder.id;  
  
  COMMIT; 
         
  SELECT bookmark.*, user.*, link.*, bookmarkTags(bookmark.id) as tags, 
          bookmarkInFolders(bookmark.id) AS folders,
          linkCount(link.id) as count    
    FROM user, bookmark, link
    WHERE bookmark.user_id=user.id AND
          bookmark.link_id=link.id AND
          bookmark.id IN (SELECT bookmark_id FROM changed_bookmarks_t);      
END//   
###############################################################
# PROCEDURE: reduceTag
# INPUT: tagId, userId
# OUTPUT: none
DROP PROCEDURE IF EXISTS reduceTag//
CREATE PROCEDURE reduceTag(tagId INT,
                           userId INT)
BEGIN
  DECLARE CONTINUE HANDLER FOR SQLSTATE '01000' SELECT 'ignore inconsistant accounting';

  CREATE TEMPORARY TABLE changed_bookmarks_t (bookmark_id INT);
  
  INSERT INTO changed_bookmarks_t(bookmark_id)
    SELECT bookmark_tag_idx.bookmark_id FROM bookmark_tag_idx, bookmark
    WHERE bookmark_tag_idx.tag_id = tagId AND
          bookmark_tag_idx.bookmark_id = bookmark.id AND
          bookmark.user_id = userId;        

  START TRANSACTION;

  UPDATE bookmark_tag_idx  
  SET bookmark_tag_idx.count = 0,
      bookmark_tag_idx.position = 0
  WHERE bookmark_tag_idx.tag_id = tagId AND
        bookmark_tag_idx.bookmark_id IN 
        (SELECT bookmark.id FROM bookmark 
           WHERE bookmark.user_id = userId);

  UPDATE user_tag_idx 
  SET user_tag_idx.count = 0
  WHERE user_tag_idx.tag_id = tagId AND
        user_tag_idx.user_id = userId;

  UPDATE link_tag_idx
  SET link_tag_idx.count = (
    SELECT SUM(bookmark_tag_idx.count)
      FROM bookmark_tag_idx, bookmark
      WHERE bookmark_tag_idx.tag_id = tagId 
        AND bookmark_tag_idx.bookmark_id = bookmark.id 
        AND bookmark.link_id = link_tag_idx.link_id         
  )
  WHERE link_tag_idx.tag_id = tagId;    
  
  UPDATE tag 
  SET tag.count = 
    (SELECT SUM(bookmark_tag_idx.count) 
       FROM bookmark_tag_idx
       WHERE bookmark_tag_idx.tag_id = tagId)
  WHERE tag.id = tagId;       
  
  REPLACE folder_tag_idx(folder_id,tag_id,count)
    SELECT folder.id, bookmark_tag_idx.tag_id, SUM(bookmark_tag_idx.count)
      FROM folder, bookmark_folder, bookmark_tag_idx
      WHERE folder.id = bookmark_folder.folder_id AND
            bookmark_folder.bookmark_id = bookmark_tag_idx.bookmark_id AND
            bookmark_tag_idx.tag_id = tagId
    GROUP by folder.id;      
  COMMIT;  
  
  SELECT bookmark.*, user.*, link.*, bookmarkTags(bookmark.id) as tags, 
          bookmarkInFolders(bookmark.id) AS folders,
          linkCount(link.id) as count    
    FROM user, bookmark, link
    WHERE bookmark.user_id=user.id AND
          bookmark.link_id=link.id AND
          bookmark.id IN (SELECT bookmark_id FROM changed_bookmarks_t);   
END//                                                    
######################################################
# PROCEDURE: createBookmarkTag
# INPUT: bookmarkId, tagId
# OUTPUT: id
DROP PROCEDURE IF EXISTS createBookmarkTag//
CREATE PROCEDURE createBookmarkTag(IN bookmarkId INT,
                                   IN tagId INT,
                                   IN count INT,
                                   IN pos INT,
                                   OUT id INT)
BEGIN
  DECLARE EXIT HANDLER FOR 1062 
    SELECT bookmark_tag_idx.id INTO id FROM bookmark_tag_idx
      WHERE bookmark_tag_idx.bookmark_id = bookmarkId AND
            bookmark_tag_idx.tag_id = tagId;
     
  INSERT INTO bookmark_tag_idx(bookmark_id,tag_id,count,position) 
     VALUES (bookmarkId,tagId,count,pos);
  SELECT LAST_INSERT_ID() INTO id;
END//
###############################################################
# PROCEDURE: getBookmarkTag
# INPUT: id
# OUTPUT: NONE
DROP PROCEDURE IF EXISTS getBookmarkTag//
CREATE PROCEDURE getBookmarkTag(id INT)
BEGIN
  SELECT *, bookmarkTags(bookmark.id) AS tags, 
     bookmarkInFolders(bookmark.id) as folders,
    linkCount(link.id) AS count
    FROM bookmark_tag_idx, bookmark, tag, user, link 
    WHERE  bookmark_tag_idx.id = id AND
    bookmark_tag_idx.tag_id = tag.id AND
    bookmark_tag_idx.bookmark_id = bookmark.id AND
    bookmark.link_id = link.id AND
    bookmark.user_id = user.id;
END//
###############################################################
# PROCEDURE: deleteBookmarkTag 
# INPUT: id
# OUTPUT: NONE
DROP PROCEDURE IF EXISTS deleteBookmarkTag//
CREATE PROCEDURE deleteBookmarkTag(id INT)
BEGIN
  DELETE FROM bookmark_tag_idx WHERE bookmark_tag_idx.id=id;
END//
###############################################################
# PROCEDURE: updateBookmarkTag 
# INPUT: id,bookmarkId,tagId
# OUTPUT: NONE
DROP PROCEDURE IF EXISTS updateBookmarkTag//
CREATE PROCEDURE updateBookmarkTag(id INT,
                                   bookmarkId INT,
                                   tagId INT,                                   
                                   count INT,
                                   pos INT)                               
BEGIN
  UPDATE bookmark_tag_idx 
    SET bookmark_tag_idx.bookmark_id=bookmarkId,
        bookmark_tag_idx.tag_id=tagId,
        bookmark_tag_idx.count=count,
        bookmark_tag_idx.position=pos      
    WHERE bookmark_tag_idx.id=id;           
END//
###############################################################
# PROCEDURE: getBookmarkTagId
# INPUT: bookmarkId, tagId
# OUTPUT: id
DROP PROCEDURE IF EXISTS getBookmarkTagId//
CREATE PROCEDURE getBookmarkTagId(IN bookmarkId INT,
                                  IN tagId INT)
BEGIN
  SELECT bookmark_tag_idx.id 
    FROM bookmark_tag_idx
    WHERE bookmark_tag_idx.bookmark_id = bookmarkId AND
          bookmark_tag_idx.tag_id = tagId;
END//
###############################################################
# PROCEDURE: findBookmarkTagUserIdGrouped
# INPUT: userId
# OUTPUT: NONE
DROP PROCEDURE IF EXISTS findBookmarkTagUserIdGrouped//
CREATE PROCEDURE findBookmarkTagUserIdGrouped(IN userId INT)                                 
BEGIN
   SELECT *, bookmarkTags(bookmark.id) AS tags, 
    bookmarkInFolders(bookmark.id) as folders,
   linkCount(link.id) AS count
   FROM link, user, 
   bookmark LEFT JOIN bookmark_tag_idx 
   ON (bookmark.id=bookmark_tag_idx.bookmark_id AND bookmark_tag_idx.count > 0) 
   LEFT JOIN tag ON (bookmark_tag_idx.tag_id = tag.id)
   WHERE bookmark.user_id=userId AND 
         bookmark.user_id=user.id AND 
         link.id = bookmark.link_id
   ORDER BY tag.id;       
END//
###############################################################
# PROCEDURE: findBookmarkTagCommunitySearch
# INPUT: searchQuery
# OUTPUT: NONE
DROP PROCEDURE IF EXISTS findBookmarkTagCommunitySearch//
CREATE PROCEDURE findBookmarkTagCommunitySearch(IN searchQuery TEXT)                                       
BEGIN
 SELECT * , bookmarkTags(bookmark.id) AS tags, 
  bookmarkInFolders(bookmark.id) as folders,
  linkCount(link.id) AS count 
  FROM link, user, 
  (SELECT * FROM bookmark WHERE
    bookmark.id IN 
    (SELECT search_idx.bookmark_id FROM search_idx WHERE MATCH(search_idx.text, search_idx.tags) AGAINST (searchQuery IN BOOLEAN MODE))) as bookmark
   LEFT JOIN bookmark_tag_idx ON (bookmark.id=bookmark_tag_idx.bookmark_id AND bookmark_tag_idx.count > 0) 
   LEFT JOIN tag ON (bookmark_tag_idx.tag_id = tag.id)
   WHERE bookmark.user_id=user.id AND 
         link.id = bookmark.link_id
   ORDER BY tag.id;      
END//
###############################################################
# PROCEDURE: findBookmarkTagByFolderId
# INPUT: userId
# OUTPUT: NONE
DROP PROCEDURE IF EXISTS findBookmarkTagByFolderId//
CREATE PROCEDURE findBookmarkTagByFolderId(IN folderId INT)                                 
BEGIN
  SELECT * , bookmarkTags(bookmark.id) AS tags, 
    bookmarkInFolders(bookmark.id) as folders,
   linkCount(link.id) AS count 
   FROM link, user, bookmark,
  (SELECT * FROM bookmark_folder WHERE bookmark_folder.folder_id = folderId) AS bf
    LEFT JOIN bookmark_tag_idx ON (bf.bookmark_id = bookmark_tag_idx.bookmark_id AND bookmark_tag_idx.count > 0)
    LEFT JOIN tag ON (bookmark_tag_idx.tag_id = tag.id)
  WHERE bookmark.id = bf.bookmark_id AND
        bookmark.user_id = user.id AND
        bookmark.link_id = link.id
  ORDER BY tag.id;    
END//
###############################################################
# PROCEDURE: 
# INPUT: 
# OUTPUT: 
#BEGIN
#END//

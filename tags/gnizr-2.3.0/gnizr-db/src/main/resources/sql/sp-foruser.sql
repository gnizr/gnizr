delimiter //
###############################################################
# PROCEDURE: createForUser
# INPUT: bmid,
#        tags,
# OUTPUT: id
DROP PROCEDURE IF EXISTS createForUser//
CREATE PROCEDURE createForUser (IN userId INT, IN bmid INT, IN message TEXT, IN createdOn DATETIME, OUT id INT)
BEGIN
    DECLARE EXIT HANDLER FOR 1062 
      SELECT for_user.id INTO id FROM for_user 
         WHERE for_user.for_user_id = userId AND
               for_user.bookmark_id = bmid;
	INSERT INTO for_user (for_user_id, bookmark_id, message, created_on)
		VALUES(userId, bmid, message, createdOn);
  	SELECT LAST_INSERT_ID() INTO id;
END;
//
###############################################################
# PROCEDURE: deleteForUser
# INPUT: id
# OUTPUT: NONE
DROP PROCEDURE IF EXISTS deleteForUser//
CREATE PROCEDURE deleteForUser(id INT)
BEGIN
  DELETE FROM for_user WHERE for_user.id=id;
END//
###############################################################
# PROCEDURE: getForUser
# INPUT: id
# OUTPUT: NONE
DROP PROCEDURE IF EXISTS getForUser//
CREATE PROCEDURE getForUser(IN id INT)
BEGIN
  SELECT *, bookmarkTags(bookmark.id) AS tags,  
    bookmarkInFolders(bookmark.id) AS folders,  
    linkCount(link.id) AS count
    FROM for_user, bookmark, link, user as fuser, user as buser
    WHERE for_user.id=id AND
          for_user.bookmark_id = bookmark.id AND
          for_user.for_user_id = fuser.id AND
          bookmark.user_id = buser.id AND
          bookmark.link_id = link.id;
END//
###############################################################
# PROCEDURE: getForUserCount
# INPUT: id
# OUTPUT: NONE
DROP PROCEDURE IF EXISTS getForUserCount//
CREATE PROCEDURE getForUserCount(IN uid INT, OUT count INT)
BEGIN
  SELECT COUNT(*) INTO count FROM for_user WHERE 
    for_user.for_user_id=uid;
END//
###############################################################
# PROCEDURE: getForUserInTimeRangeCount
# INPUT: id, startTime, endTime
# OUTPUT: count INT
DROP PROCEDURE IF EXISTS getForUserInTimeRangeCount//
CREATE PROCEDURE getForUserInTimeRangeCount(IN uid INT, 
                                            IN startTime DATETIME,
                                            IN endTime DATETIME,
                                            OUT count INT)
BEGIN
  SELECT COUNT(*) INTO count FROM for_user WHERE 
    for_user.for_user_id=uid AND
     for_user.created_on BETWEEN startTime AND endTime;
END//
###############################################################
# PROCEDURE: updateForUser
DROP PROCEDURE IF EXISTS updateForUser//
CREATE PROCEDURE updateForUser (IN id INT, IN userId INT, IN bmid INT, IN message TEXT, IN createdOn DATETIME)
BEGIN
	UPDATE for_user SET for_user_id=userId,
						bookmark_id=bmid,
						message=message,
						created_on=createdOn
	WHERE for_user.id=id;
END//
###############################################################
# PROCEDURE: hasForUser
# INPUT: offset,
#        count
# OUTPUT: NONE
DROP PROCEDURE IF EXISTS hasForUser//
CREATE PROCEDURE hasForUser(IN bmid INT, IN userId INT, OUT count INTEGER)
BEGIN
	SELECT COUNT(*) INTO count FROM for_user WHERE 
	  for_user.for_user_id=userId AND for_user.bookmark_id=bmid;
END//
###############################################################
# PROCEDURE: pageForUser
# INPUT: offset,
#        count
# OUTPUT: NONE
DROP PROCEDURE IF EXISTS pageForUser//
CREATE PROCEDURE pageForUser(IN userId INT, IN offset INT, IN count INT)
BEGIN
  SET @userId = userId;
  SET @offset = offset;
  SET @count = count;
  PREPARE STMT FROM "
   SELECT *, bookmarkTags(bookmark.id) AS tags, 
     bookmarkInFolders(bookmark.id) AS folders,  
     linkCount(link.id) AS count
     FROM for_user, bookmark, link, user as fuser, user as buser
     WHERE bookmark.id=for_user.bookmark_id AND 
           for_user.for_user_id=? AND
           for_user.for_user_id=fuser.id AND
           bookmark.user_id=buser.id AND
           bookmark.link_id=link.id
   ORDER BY for_user.created_on DESC LIMIT ?,?";
  EXECUTE STMT USING @userId,@offset,@count;
END//
###############################################################
# PROCEDURE: listForUserFromBookmark
# INPUT: id
# OUTPUT: NONE
DROP PROCEDURE IF EXISTS listForUserFromBookmark//
CREATE PROCEDURE listForUserFromBookmark(bmid INT)
BEGIN
  SELECT *, bookmarkTags(bookmark.id) AS tags, 
    bookmarkInFolders(bookmark.id) AS folders,  
    linkCount(link.id) AS count
    FROM for_user, bookmark, link, user as fuser, user as buser
    WHERE for_user.bookmark_id=bmid AND
          for_user.bookmark_id=bookmark.id AND
          for_user.for_user_id=fuser.id AND
          bookmark.user_id=buser.id AND
          bookmark.link_id=link.id;
END//
###############################################################
# PROCEDURE: pageForUserInTimeRange
# INPUT: userId,
#        startTime,
#        endTime,
#        offset,
#        count
# OUTPUT: NONE
DROP PROCEDURE IF EXISTS pageForUserInTimeRange//
CREATE PROCEDURE pageForUserInTimeRange(userId INT, 
                                        startTime DATETIME,
                                        endTime DATETIME,
                                        offset INT,
                                        count INT)
BEGIN
  SET @userId = userId;
  SET @startTime = startTime;
  SET @endTime = endTime;
  SET @offset = offset;
  SET @count = count;
  PREPARE STMT FROM "
      SELECT *, bookmarkTags(bookmark.id) AS tags, 
         bookmarkInFolders(bookmark.id) AS folders,  
        linkCount(link.id) AS count
        FROM for_user, bookmark, link, user as fuser, user as buser 
        WHERE for_user.for_user_id=? AND 
              for_user.created_on BETWEEN ? AND ? AND
              bookmark.id = for_user.bookmark_id AND
 	          bookmark.link_id = link.id AND
 	          bookmark.user_id = buser.id AND
              for_user.for_user_id = fuser.id
      ORDER BY for_user.created_on DESC LIMIT ?,?";
  EXECUTE STMT USING @userId,@startTime,@endTime,@offset,@count;
END//
#####################################################################
# PROCEDURE: pageForUserBySenderId
# INPUT: userId, senderId, offset, count
# OUTPUT: totalCount
DROP PROCEDURE IF EXISTS pageForUserBySenderId//
CREATE PROCEDURE pageForUserBySenderId(IN userId INT,
                                       IN senderId INT,
                                       IN offset INT,
                                       IN count INT,
                                       OUT totalCount INT)
BEGIN
  SET @userId = userId;
  SET @senderId = senderId;
  SET @offset = offset;
  SET @count = count;
  PREPARE STMT FROM "
    SELECT SQL_CALC_FOUND_ROWS *, bookmarkTags(bookmark.id) AS tags, 
      bookmarkInFolders(bookmark.id) AS folders,  
      linkCount(link.id) AS count
      FROM for_user, bookmark, user as fuser, user as buser, link
      WHERE for_user.id IN
       (SELECT for_user.id FROM for_user, bookmark 
          WHERE for_user.for_user_id = ? AND
                for_user.bookmark_id = bookmark.id AND
                bookmark.user_id = ?) AND
           for_user.for_user_id = fuser.id AND
           for_user.bookmark_id = bookmark.id AND
	       bookmark.link_id = link.id AND
	       bookmark.user_id = buser.id 
    ORDER BY for_user.created_on DESC LIMIT ?, ?
  ";
  EXECUTE STMT USING @userId, @senderId, @offset, @count;
  SELECT FOUND_ROWS() INTO totalCount;  
END//
#####################################################################
# PROCEDURE: deleteForUserById
# INPUT: userId, id
# OUTPUT: NONE
DROP PROCEDURE IF EXISTS deleteForUserById//
CREATE PROCEDURE deleteForUserById(IN userId INT, 
                                   IN id INT)
BEGIN
  DELETE FROM for_user WHERE 
    for_user.for_user_id = userId AND for_user.id = id;
END//
#####################################################################
# PROCEDURE: deleteAllForUser
# INPUT: userId, id
# OUTPUT: NONE
DROP PROCEDURE IF EXISTS deleteAllForUser//
CREATE PROCEDURE deleteAllForUser(IN userId INT)
BEGIN
  DELETE FROM for_user WHERE for_user.for_user_id = userId;
END//
#####################################################################
# PROCEDURE: listForUserSenders
# INPUT: forUserId
# OUTPUT: NONE
DROP PROCEDURE IF EXISTS listForUserSenders//
CREATE PROCEDURE listForUserSenders(IN forUserId INT)
BEGIN
  SELECT * FROM user WHERE user.id IN 
   (SELECT DISTINCT bookmark.user_id FROM bookmark, for_user WHERE
    for_user.for_user_id = forUserId AND
    for_user.bookmark_id = bookmark.id);
END//
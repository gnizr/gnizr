DELIMITER //
##############################################################
# PROCEDURE: getUser(id)
# INPUT: id INT
# OUTPUT: NONE
DROP PROCEDURE IF EXISTS getUser//
CREATE PROCEDURE getUser(uid INT)
BEGIN
  SELECT * FROM user WHERE user.id=uid;  
END//
##############################################################
# PROCEDURE: findUserUsername
# INPUT: username
# OUTPUT: NONE
DROP PROCEDURE IF EXISTS findUserUsername//
CREATE PROCEDURE findUserUsername(username VARCHAR(45))
BEGIN
  SELECT * FROM user WHERE user.username=username; 
END//
##############################################################
# PROCEDURE: findAllUsers
# INPUT: NONE
# OUTPUT: NONE
DROP PROCEDURE IF EXISTS findAllUsers//
CREATE PROCEDURE findAllUsers()
BEGIN
  SELECT * FROM user WHERE user.username != "gnizr";
END//
##############################################################
# PROCEDURE: findUserUnamePwd
# INPUT: username
#        password
# OUTPUT: NONE
DROP PROCEDURE IF EXISTS findUserUnamePwd//
CREATE PROCEDURE findUserUnamePwd(username VARCHAR(45),
                                  password VARCHAR(45))
BEGIN
  SELECT * FROM user WHERE user.username=username AND 
                           user.password=MD5(password); 
END//
##############################################################
# PROCEDURE: deleteUser
# INPUT: uid
# OUTPUT NONE
DROP PROCEDURE IF EXISTS deleteUser//
CREATE PROCEDURE deleteUser(uid INT)
BEGIN
  DELETE user, bookmark, search_idx
   FROM user LEFT JOIN (bookmark, search_idx)
   ON (user.id = bookmark.user_id AND bookmark.id = search_idx.bookmark_id)
   WHERE user.id=uid;
END//
##############################################################
# PROCEDURE: createUser
# INPUT: username
#        password,
#        fullname,
#        email,
#        createdOn,
#        status
# OUTPUT: NONE
DROP PROCEDURE IF EXISTS createUser//
CREATE PROCEDURE `createUser`(IN username VARCHAR(45),
 			      IN password VARCHAR(45),
                              IN fullname VARCHAR(100),
                              IN email VARCHAR(50),
                              IN createdOn DATETIME,
                              IN status INTEGER, 
                              OUT id INT)
BEGIN
  DECLARE `USEREXISTS` CONDITION FOR SQLSTATE '23000';
  DECLARE EXIT HANDLER FOR `USEREXISTS` ROLLBACK;
  START TRANSACTION;    
    INSERT INTO user(username,password,fullname,email,created_on,acct_status)
      VALUES (username,MD5(password),fullname,email,createdOn,status);
    SELECT LAST_INSERT_ID() INTO id;
  COMMIT;
END//
##############################################################
# PROCEDURE: updateUser
# INPUT: username
#        password
#        fullname
#        email
#        createdOn
#        status
# OUTPUT: NONE
DROP PROCEDURE IF EXISTS updateUser//
CREATE PROCEDURE `updateUser`(IN id INT,
			                  IN username VARCHAR(45),
 			                  IN password VARCHAR(45),
                              IN fullname VARCHAR(100),
                              IN email VARCHAR(50),
                              IN createdOn DATETIME,
                              IN status INT)
                              
BEGIN
  IF CHAR_LENGTH(password) > 0 THEN
    UPDATE user U SET 
      U.username = username,
      U.password = MD5(password),
      U.fullname = fullname,
      U.email = email,
      U.created_on = created_on,
      U.acct_status = status WHERE U.id=id;
  ELSE 
   UPDATE user U SET 
      U.username = username,
      U.fullname = fullname,     
      U.email = email,
      U.created_on = createdOn,
      U.acct_status = status WHERE U.id=id;   
  END IF;
END//
##############################################################
# PROCEDURE: listUserStats
# INPUT: NONE
# OUTPUT: NONE
DROP PROCEDURE IF EXISTS listUserStats//
CREATE PROCEDURE listUserStats()
BEGIN
  SELECT *,  
  (SELECT count(*) FROM bookmark WHERE bookmark.user_id = user.id) as total_number_bookmark,
  (SELECT count(*) FROM user_tag_idx WHERE user_tag_idx.user_id = user.id) as total_number_tag
  FROM user WHERE user.username != "gnizr"
  ORDER BY total_number_bookmark DESC, total_number_tag DESC;
END//
###############################################################
# PROCEDURE: findTagGroupsOfUser
# INPUT: userId, minFreq, sortBy, orderFlag
# OUTPUT: 
DROP PROCEDURE IF EXISTS findTagGroupsOfUser//
CREATE PROCEDURE findTagGroupsOfUser(IN userId INT,                                     
                                     IN minFreq INT, 
                                     IN sortBy INT,
                                     IN orderFlag INT)
BEGIN
  SET @userId = userId;
  SET @minFreq = minFreq;
  SET @qry = CONCAT("
  (SELECT mem_tag.id AS mem_tag_id, mem_tag.tag AS mem_tag_tag, mem_tag.count AS mem_tag_count,
          mem_tag_uti.id AS mem_tag_uti_id, mem_tag_uti.user_id AS mem_tag_uti_user_id, 
          mem_tag_uti.tag_id AS mem_tag_uti_tag_id, mem_tag_uti.count AS mem_tag_uti_count,
          grp_tag.id AS grp_tag_id, grp_tag.tag AS grp_tag_tag, grp_tag.count AS grp_tag_count, 
          user.id AS user_id, user.username AS user_username, user.password AS user_password,
          user.fullname AS user_fullname, user.created_on AS user_created_on, 
          user.email AS user_email, user.acct_status AS user_acct_status
    FROM tag as mem_tag, tag as grp_tag, 
         user_tag_idx as mem_tag_uti, user_tag_idx as grp_tag_uti, 
         tag_assertion, user
    WHERE user.id = ? AND
          tag_assertion.user_id=user.id AND 
          tag_assertion.prpt_id=4 AND 
          tag_assertion.object_id = grp_tag_uti.id AND 
          tag_assertion.subject_id = mem_tag_uti.id AND 
          grp_tag_uti.tag_id = grp_tag.id AND 
          mem_tag_uti.tag_id = mem_tag.id AND
          mem_tag_uti.count >= ?) UNION ");
  
  SET @qry = CONCAT(@qry, "
  (SELECT mem_tag.id, mem_tag.tag, mem_tag.count,
          mem_tag_uti.id, mem_tag_uti.user_id, mem_tag_uti.tag_id, mem_tag_uti.count,
          grp_tag.id, grp_tag.tag, grp_tag.count, 
          user.id, user.username,user.password,user.fullname,
          user.created_on,user.email,user.acct_status      
    FROM tag AS mem_tag, user_tag_idx as mem_tag_uti, user,
         (SELECT 0 AS id, '' AS tag, 0 AS count) AS grp_tag
    WHERE user.id = ? AND mem_tag_uti.user_id=user.id AND 
          mem_tag_uti.tag_id = mem_tag.id AND
          mem_tag_uti.count >= ? AND
          mem_tag_uti.id NOT IN 
          (SELECT tag_assertion.subject_id FROM tag_assertion 
             WHERE tag_assertion.user_id = user.id AND tag_assertion.prpt_id=4)) ");

  # 1: sort alphabetically  
  # 2: sort by usage freq
  IF (sortBy = 1) THEN
    SET @qry = CONCAT(@qry, " ORDER BY grp_tag_tag ASC, mem_tag_tag ");
  ELSE
    SET @qry = CONCAT(@qry, " ORDER BY grp_tag_tag ASC, mem_tag_count ");
  END IF; 

  IF(orderFlag = 1) THEN
    SET @qry = CONCAT(@qry, " DESC ");
  ELSE
    SET @qry = CONCAT(@qry, " ASC ");
  END IF;
  
  PREPARE STMT FROM @qry;
  EXECUTE STMT USING @userId, @minFreq, @userId, @minFreq;
END//

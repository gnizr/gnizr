delimiter //
###############################################################
# FUNCTION: linkCount
# INPUT: linkId
# OUTPUT: NONE
DROP FUNCTION IF EXISTS linkCount//
CREATE FUNCTION linkCount(linkId INT) 
RETURNS INT
READS SQL DATA
BEGIN
  DECLARE size INT;
  SELECT count(*) INTO size FROM bookmark 
    WHERE bookmark.link_id = linkId;
  RETURN size;      
END//
###############################################################
# PROCEDURE: createLink 
# INPUT:  url,
#         mimeTypeId,
#         count
# OUTPUT: id
DROP PROCEDURE IF EXISTS createLink//
CREATE PROCEDURE createLink(IN url   TEXT,                         
                            IN mimeTypeId INT,                            
                            OUT id INT)
BEGIN
  INSERT INTO link(url,mime_type_id,url_hash)
    VALUES (url,mimeTypeId,md5(url));
  SELECT LAST_INSERT_ID() INTO id;
END//
###############################################################
# PROCEDURE: deleteLink
# INPUT: id
# OUTPUT: NONE
DROP PROCEDURE IF EXISTS deleteLink//
CREATE PROCEDURE deleteLink(id INT)
BEGIN
  DELETE FROM link WHERE link.id=id;
END//
###############################################################
# PROCEDURE: getLink
# INPUT: id
# OUTPUT: NONE
DROP PROCEDURE IF EXISTS getLink//
CREATE PROCEDURE getLink(id INT)
BEGIN
  SELECT *, count(bookmark.id) as count
     FROM link LEFT JOIN bookmark 
     ON link.id=bookmark.link_id WHERE
     link.id=id 
     GROUP BY bookmark.link_id;          
END//
###############################################################
# PROCEDURE: updateLink
# INPUT: id, url, mimeTypeId
# OUTPUT: NONE
DROP PROCEDURE IF EXISTS updateLink//
CREATE PROCEDURE updateLink(id INT, url TEXT, mimeTypeId INT)
BEGIN
  UPDATE link SET 
     link.url = url, 
     link.mime_type_id = mimeTypeId, 
     link.url_hash=md5(url)
  WHERE link.id = id;        
END//
###############################################################
# PROCEDURE: findLinkUrl
# INPUT: link
# OUTPUT: NONE
DROP PROCEDURE IF EXISTS findLinkUrl//
CREATE PROCEDURE findLinkUrl(url TEXT)
BEGIN
  call findLinkUrlHash(md5(url));
END//
###############################################################
# PROCEDURE: findLinkUrlHash
# INPUT: urlHash
# OUTPUT: NONE
DROP PROCEDURE IF EXISTS findLinkUrlHash//
CREATE PROCEDURE findLinkUrlHash(urlHash VARCHAR(45))
BEGIN
  SELECT *, count(bookmark.id) AS count FROM link LEFT JOIN bookmark
   ON link.id = bookmark.link_id
   WHERE link.url_hash=urlHash
   GROUP BY bookmark.link_id;
END//
###############################################################
###############################################################
# PROCEDURE: 
# INPUT: 
# OUTPUT: 
#BEGIN
#END//



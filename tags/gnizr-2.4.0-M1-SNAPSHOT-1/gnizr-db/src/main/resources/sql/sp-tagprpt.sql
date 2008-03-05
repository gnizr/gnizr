DELIMITER //
##############################################################
# PROCEDURE: getTagProperty
# INPUT: id INT
# OUTPUT: NONE
DROP PROCEDURE IF EXISTS getTagProperty//
CREATE PROCEDURE getTagProperty(id INT)
BEGIN
  SELECT * FROM tag_prpt WHERE tag_prpt.id=id;
END//
##############################################################
# PROCEDURE: createTagProperty
# INPUT: name, ns_prefix, description, prpt_type, cardinaltiy
# OUTPUT: id
DROP PROCEDURE IF EXISTS createTagProperty//
CREATE PROCEDURE createTagProperty(IN name VARCHAR(45),
                                   IN ns_prefix VARCHAR(10),
                                   IN description VARCHAR(255),
                                   IN prpt_type VARCHAR(255),                                   
                                   IN cardinality INT,
                                   OUT id INT) 
BEGIN
  INSERT INTO tag_prpt(name,ns_prefix,description,prpt_type,cardinality) 
     VALUES (name,ns_prefix,description,prpt_type,cardinality);
  SELECT LAST_INSERT_ID() INTO id;    
END//
##############################################################
# PROCEDURE: deleteTagProperty
# INPUT: id
# OUTPUT: NONE
DROP PROCEDURE IF EXISTS deleteTagProperty//
CREATE PROCEDURE deleteTagProperty(id INT)
BEGIN
  DELETE FROM tag_prpt WHERE tag_prpt.id=id;
END//
##############################################################
# PROCEDURE: updateTagProperty
# INPUT: id, name, ns_prefix, description, prpt_type, cardinaltiy
# OUTPUT: NONE
DROP PROCEDURE IF EXISTS updateTagProperty//
CREATE PROCEDURE updateTagProperty(id INT,
                                   name VARCHAR(45),
                                   ns_prefix VARCHAR(10),
                                   description VARCHAR(255),
                                   prpt_type VARCHAR(255),
                                   cardinality INT)
BEGIN
  UPDATE tag_prpt tp SET 
   tp.name = name,
   tp.ns_prefix = ns_prefix,
   tp.description = description,
   tp.prpt_type = prpt_type,
   tp.cardinality = cardinality
  WHERE tp.id=id; 
END//
##############################################################
# PROCEDURE: listAllTagProperty
# INPUT: NONE
# OUTPUT: NONE
DROP PROCEDURE IF EXISTS listAllTagProperty//
CREATE PROCEDURE listAllTagProperty()
BEGIN
  SELECT * FROM tag_prpt;
END//
##############################################################
# PROCEDURE: getTagPropertyNSPrefixName
# INPUT: nsPrefix, name
# OUTPUT: NONE
DROP PROCEDURE IF EXISTS getTagPropertyNSPrefixName//
CREATE PROCEDURE getTagPropertyNSPrefixName(nsPrefix VARCHAR(10),
                                             name VARCHAR(45))
BEGIN
  SELECT * FROM tag_prpt WHERE 
    tag_prpt.ns_prefix = nsPrefix AND
    tag_prpt.name = name;
END//
##############################################################
# PROCEDURE: listTagPropertyOfType
# INPUT: prptType
# OUTPUT: NONE
DROP PROCEDURE IF EXISTS listTagPropertyOfType//
CREATE PROCEDURE listTagPropertyOfType(prptType VARCHAR(255))                                           
BEGIN
  SELECT * FROM tag_prpt WHERE 
    tag_prpt.prpt_type = prptType;
END//









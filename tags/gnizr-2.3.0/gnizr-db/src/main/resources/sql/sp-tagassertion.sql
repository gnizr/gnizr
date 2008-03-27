DELIMITER //
##############################################################
# PROCEDURE: createTagAssertion
# INPUT: subjTagId,prptId,objcTagId,userId
# OUTPUT: id
DROP PROCEDURE IF EXISTS createTagAssertion//
CREATE PROCEDURE createTagAssertion(IN subjTagId INT,
                                    IN prptId INT,
                                    IN objcTagId INT,
                                    IN userId INT,
                                    OUT id INT)
BEGIN
  DECLARE EXIT HANDLER FOR 1062 
    SELECT tag_assertion.id INTO id FROM tag_assertion WHERE
      tag_assertion.user_id = userId AND
      tag_assertion.subject_id = subjTagId AND
      tag_assertion.prpt_id = prptId AND
      tag_assertion.object_id = objcTagId;
  INSERT INTO tag_assertion(subject_id,prpt_id,object_id,user_id)
    VALUES (subjTagId,prptId,objcTagId,userId);
  SELECT LAST_INSERT_ID() INTO id;
END//
##############################################################
# PROCEDURE: createTagAssertionNoOutput
# INPUT: subjTagId,prptId,objcTagId,userId
# OUTPUT: NONE
DROP PROCEDURE IF EXISTS createTagAssertionNoOutput//
CREATE PROCEDURE createTagAssertionNoOutput(IN subjTagId INT,
                                    IN prptId INT,
                                    IN objcTagId INT,
                                    IN userId INT)
BEGIN
  INSERT IGNORE INTO tag_assertion(subject_id,prpt_id,object_id,user_id)
    VALUES (subjTagId,prptId,objcTagId,userId);
END//
##############################################################
# PROCEDURE: deleteTagAssertion
# INPUT: id
# OUTPUT: NONE
DROP PROCEDURE IF EXISTS deleteTagAssertion//
CREATE PROCEDURE deleteTagAssertion(id INT)
BEGIN
  DELETE FROM tag_assertion WHERE tag_assertion.id=id;
END//
##############################################################
# PROCEDURE: deleteSKOSRelatedAssertion
# INPUT: userId, subjectId, objectId
# OUTPUT: NONE
DROP PROCEDURE IF EXISTS deleteSKOSRelatedAssertion//
CREATE PROCEDURE deleteSKOSRelatedAssertion(userId INT,
                                            subjectId INT,
                                            objectId INT)
BEGIN      
  DELETE FROM tag_assertion
  WHERE user_id = userId AND
        prpt_id = (SELECT id FROM tag_prpt WHERE tag_prpt.name='related' AND tag_prpt.ns_prefix='skos') AND
        ((subject_id = subjectId AND object_id=objectId) OR 
        (subject_id = objectId AND object_id=subjectId));           
END//
##############################################################
# PROCEDURE: deleteSKOSBroaderAssertion
# INPUT: userId, subjectId, objectId
# OUTPUT: NONE
DROP PROCEDURE IF EXISTS deleteSKOSBroaderAssertion//
CREATE PROCEDURE deleteSKOSBroaderAssertion(userId INT,
                                            subjectId INT,
                                            objectId INT)
BEGIN      
  DELETE FROM tag_assertion
  WHERE user_id = userId AND (
        (prpt_id = (SELECT id FROM tag_prpt WHERE tag_prpt.name='broader' AND tag_prpt.ns_prefix='skos') AND
         subject_id = subjectId AND object_id=objectId) OR 
        (prpt_id = (SELECT id FROM tag_prpt WHERE tag_prpt.name='narrower' AND tag_prpt.ns_prefix='skos') AND
         subject_id = objectId AND object_id=subjectId)
  );           
END//
##############################################################
# PROCEDURE: deleteSKOSNarrowerAssertion
# INPUT: userId, subjectId, objectId
# OUTPUT: NONE
DROP PROCEDURE IF EXISTS deleteSKOSNarrowerAssertion//
CREATE PROCEDURE deleteSKOSNarrowerAssertion(userId INT,
                                             subjectId INT,
                                             objectId INT)
BEGIN      
  DELETE FROM tag_assertion
  WHERE user_id = userId AND (
        (prpt_id = (SELECT id FROM tag_prpt WHERE tag_prpt.name='narrower' AND tag_prpt.ns_prefix='skos') AND
         subject_id = subjectId AND object_id=objectId) OR 
        (prpt_id = (SELECT id FROM tag_prpt WHERE tag_prpt.name='broader' AND tag_prpt.ns_prefix='skos') AND
         subject_id = objectId AND object_id=subjectId)
  );           
END//
##############################################################
# PROCEDURE: deleteRDFTypeAssertion
# INPUT: userId, subjectId, objectId
# OUTPUT: NONE
DROP PROCEDURE IF EXISTS deleteRDFTypeAssertion//
CREATE PROCEDURE deleteRDFTypeAssertion(userId INT,
                                        subjectId INT,
                                        objectId INT)
BEGIN      
  DELETE FROM tag_assertion
  WHERE user_id = userId AND 
        prpt_id = (SELECT id FROM tag_prpt WHERE tag_prpt.name='type' AND tag_prpt.ns_prefix='rdf') AND
        subject_id = subjectId AND 
        object_id = objectId ;           
END//
##############################################################
# PROCEDURE: deleteRDFTypeClassAssertion
# INPUT: userId, objectId
# OUTPUT: NONE
DROP PROCEDURE IF EXISTS deleteRDFTypeClassAssertion//
CREATE PROCEDURE deleteRDFTypeClassAssertion(userId INT,
                                             objectId INT)
BEGIN      
  DELETE FROM tag_assertion
  WHERE user_id = userId AND 
        prpt_id = (SELECT id FROM tag_prpt WHERE tag_prpt.name='type' AND tag_prpt.ns_prefix='rdf') AND       
        object_id = objectId ;           
END//
##############################################################
# PROCEDURE: getTagAssertion
# INPUT: id
# OUTPUT: NONE
DROP PROCEDURE IF EXISTS getTagAssertion//
CREATE PROCEDURE getTagAssertion(id INT)
BEGIN
  SELECT * FROM tag_assertion, tag_prpt,
         user_tag_idx as s_user_tag,
         user_tag_idx as o_user_tag,
         user as tag_assertion_user,
         user as s_user,
         user as o_user,
         tag as s_tag,
         tag as o_tag
   WHERE tag_assertion.id=id AND 
         tag_assertion.subject_id = s_user_tag.id AND
         tag_assertion.object_id = o_user_tag.id AND
         tag_assertion.prpt_id = tag_prpt.id AND
         tag_assertion.user_id = tag_assertion_user.id AND
         s_user_tag.user_id = s_user.id AND
         o_user_tag.user_id = o_user.id AND
         s_user_tag.tag_id = s_tag.id AND
         o_user_tag.tag_id = o_tag.id; 
END//
##############################################################
# PROCEDURE: updateTagAssertion
# INPUT: id,subjTagId,prptId,objcTagId,userId
# OUTPUT: NONE
DROP PROCEDURE IF EXISTS updateTagAssertion//
CREATE PROCEDURE updateTagAssertion(id INT,
                                    subjTagId INT,
                                    prptId INT,
                                    objcTagId INT,
                                    userId INT)
BEGIN
  UPDATE tag_assertion ta SET 
    ta.subject_id = subjTagId,
    ta.prpt_id = prptId,
    ta.object_id = objcTagId
  WHERE ta.id=id;
END//
##############################################################
# PROCEDURE: findTagAssertionUserId
# INPUT: userId
# OUTPUT: NONE
DROP PROCEDURE IF EXISTS findTagAssertionUserId//
CREATE PROCEDURE findTagAssertionUserId(userId INT)
BEGIN
  SELECT *
    FROM tag_assertion, tag_prpt,
         user_tag_idx as s_user_tag,
         user_tag_idx as o_user_tag,
         user as tag_assertion_user,
         user as s_user,
         user as o_user,
         tag as s_tag,
         tag as o_tag
   WHERE tag_assertion.user_id=userId AND
         tag_assertion.subject_id = s_user_tag.id AND
         tag_assertion.object_id = o_user_tag.id AND
         tag_assertion.prpt_id = tag_prpt.id AND
         tag_assertion.user_id = tag_assertion_user.id AND
         s_user_tag.user_id = s_user.id AND
         o_user_tag.user_id = o_user.id AND
         s_user_tag.tag_id = s_tag.id AND
         o_user_tag.tag_id = o_tag.id;
END//
##############################################################
# PROCEDURE: findTagAssertionUserSubjectId
# INPUT: userId,subjTagId
# OUTPUT: NONE
DROP PROCEDURE IF EXISTS findTagAssertionUserSubjectId//
CREATE PROCEDURE findTagAssertionUserSubjectId(userId INT,
                                               subjTagId INT)
BEGIN
  SELECT * FROM tag_assertion, tag_prpt,
         user_tag_idx as s_user_tag,
         user_tag_idx as o_user_tag,
         user as tag_assertion_user,
         user as s_user,
         user as o_user,
         tag as s_tag,
         tag as o_tag
    WHERE tag_assertion.user_id = userId AND
          tag_assertion.subject_id = subjTagId AND
          tag_assertion.subject_id = s_user_tag.id AND
          tag_assertion.object_id = o_user_tag.id AND
          tag_assertion.prpt_id = tag_prpt.id AND
          tag_assertion.user_id = tag_assertion_user.id AND
          s_user_tag.user_id = s_user.id AND
          o_user_tag.user_id = o_user.id AND
          s_user_tag.tag_id = s_tag.id AND
          o_user_tag.tag_id = o_tag.id;          
END//
##############################################################
# PROCEDURE: findTagAssertionUserObjectId
# INPUT: userId,objcTagId
# OUTPUT: NONE
DROP PROCEDURE IF EXISTS findTagAssertionUserObjectId//
CREATE PROCEDURE findTagAssertionUserObjectId(userId INT,
                                              objcTagId INT)
BEGIN
  SELECT * FROM tag_assertion, tag_prpt,
         user_tag_idx as s_user_tag,
         user_tag_idx as o_user_tag,
         user as tag_assertion_user,
         user as s_user,
         user as o_user,
         tag as s_tag,
         tag as o_tag
    WHERE tag_assertion.user_id = userId AND
          tag_assertion.object_id = objcTagId AND
          tag_assertion.subject_id = s_user_tag.id AND
          tag_assertion.object_id = o_user_tag.id AND
          tag_assertion.prpt_id = tag_prpt.id AND
          tag_assertion.user_id = tag_assertion_user.id AND
          s_user_tag.user_id = s_user.id AND
          o_user_tag.user_id = o_user.id AND
          s_user_tag.tag_id = s_tag.id AND
          o_user_tag.tag_id = o_tag.id;
END//
##############################################################
# PROCEDURE: findTagAssertionUserPrptId
# INPUT: userId,tagPrptId
# OUTPUT: NONE
DROP PROCEDURE IF EXISTS findTagAssertionUserPrptId//
CREATE PROCEDURE findTagAssertionUserPrptId(userId INT,
                                            tagPrptId INT)
BEGIN
  SELECT * FROM tag_assertion, tag_prpt,
         user_tag_idx as s_user_tag,
         user_tag_idx as o_user_tag,
         user as tag_assertion_user,
         user as s_user,
         user as o_user,
         tag as s_tag,
         tag as o_tag 
    WHERE tag_assertion.user_id = userId AND
          tag_assertion.prpt_id = tagPrptId AND
          tag_assertion.subject_id = s_user_tag.id AND
          tag_assertion.object_id = o_user_tag.id AND
          tag_assertion.prpt_id = tag_prpt.id AND
          tag_assertion.user_id = tag_assertion_user.id AND
          s_user_tag.user_id = s_user.id AND
          o_user_tag.user_id = o_user.id AND
          s_user_tag.tag_id = s_tag.id AND
          o_user_tag.tag_id = o_tag.id; 
END//
##############################################################
# PROCEDURE: findTagAssertion
# INPUT: userId,subjTagId,tagPrptId,objcTagId
# OUTPUT: NONE
DROP PROCEDURE IF EXISTS findTagAssertion//
CREATE PROCEDURE findTagAssertion(userId INT,
                                  subjTagId INT,
                                  tagPrptId INT,
                                  objcTagId INT)
BEGIN
  SELECT * FROM tag_assertion, tag_prpt,
         user_tag_idx as s_user_tag,
         user_tag_idx as o_user_tag,
         user as tag_assertion_user,
         user as s_user,
         user as o_user,
         tag as s_tag,
         tag as o_tag  
   WHERE tag_assertion.user_id=userId AND
         tag_assertion.subject_id=subjTagId AND
         tag_assertion.prpt_id=tagPrptId AND
         tag_assertion.object_id=objcTagId AND
         tag_assertion.subject_id = s_user_tag.id AND
         tag_assertion.object_id = o_user_tag.id AND
         tag_assertion.prpt_id = tag_prpt.id AND
         tag_assertion.user_id = tag_assertion_user.id AND
         s_user_tag.user_id = s_user.id AND
         o_user_tag.user_id = o_user.id AND
         s_user_tag.tag_id = s_tag.id AND
         o_user_tag.tag_id = o_tag.id;
END//
##############################################################
# PROCEDURE: findTagAssertionUserSubjectPrptId
# INPUT: userId,subjTagId,PrptId
# OUTPUT: NONE
DROP PROCEDURE IF EXISTS findTagAssertionUserSubjectPrptId//
CREATE PROCEDURE findTagAssertionUserSubjectPrptId(userId INT,
                                                   subjTagId INT,
                                                   prptId INT)
BEGIN
  SELECT * FROM tag_assertion, tag_prpt,
         user_tag_idx as s_user_tag,
         user_tag_idx as o_user_tag,
         user as tag_assertion_user,
         user as s_user,
         user as o_user,
         tag as s_tag,
         tag as o_tag   
    WHERE tag_assertion.user_id = userId AND
          tag_assertion.subject_id = subjTagId AND
          tag_assertion.prpt_id = prptId AND
          tag_assertion.subject_id = s_user_tag.id AND
          tag_assertion.object_id = o_user_tag.id AND
          tag_assertion.prpt_id = tag_prpt.id AND
          tag_assertion.user_id = tag_assertion_user.id AND
          s_user_tag.user_id = s_user.id AND
          o_user_tag.user_id = o_user.id AND
          s_user_tag.tag_id = s_tag.id AND
          o_user_tag.tag_id = o_tag.id;
END//
##############################################################
# PROCEDURE: findTagAssertionUserPrptObjectId
# INPUT: userId,PrptId,objcTagId
# OUTPUT: NONE
DROP PROCEDURE IF EXISTS findTagAssertionUserPrptObjectId//
CREATE PROCEDURE findTagAssertionUserPrptObjectId(userId INT, 
                                                  prptId INT,
                                                  objcTagId INT)
BEGIN
  SELECT * FROM tag_assertion,  tag_prpt,
         user_tag_idx as s_user_tag,
         user_tag_idx as o_user_tag,
         user as tag_assertion_user,
         user as s_user,
         user as o_user,
         tag as s_tag,
         tag as o_tag    
    WHERE tag_assertion.user_id = userId AND
          tag_assertion.object_id = objcTagId AND
          tag_assertion.prpt_id = prptId AND
          tag_assertion.subject_id = s_user_tag.id AND
          tag_assertion.object_id = o_user_tag.id AND
          tag_assertion.prpt_id = tag_prpt.id AND
          tag_assertion.user_id = tag_assertion_user.id AND
          s_user_tag.user_id = s_user.id AND
          o_user_tag.user_id = o_user.id AND
          s_user_tag.tag_id = s_tag.id AND
          o_user_tag.tag_id = o_tag.id;
END//
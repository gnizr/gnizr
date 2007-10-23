delimiter //
DROP PROCEDURE IF EXISTS repairTagCounts//
CREATE PROCEDURE repairTagCounts()
BEGIN                

# calculate the number of times a given tag is used
UPDATE tag, bookmark_tag_idx
 SET tag.count = (
  SELECT sum(bookmark_tag_idx.count)
    FROM bookmark_tag_idx
    WHERE bookmark_tag_idx.tag_id = tag.id
    GROUP BY bookmark_tag_idx.tag_id)
 WHERE tag.id = bookmark_tag_idx.tag_id;
 
CREATE TEMPORARY TABLE calcTagCounts (id INT, count INT);

REPLACE calcTagCounts(id,count)
SELECT user_tag_idx.id, SUM(bookmark_tag_idx.count)
  FROM bookmark_tag_idx, bookmark, user_tag_idx
  WHERE bookmark_tag_idx.bookmark_id = bookmark.id AND
        bookmark.user_id = user_tag_idx.user_id AND
        bookmark_tag_idx.tag_id = user_tag_idx.tag_id
  GROUP BY user_tag_idx.id;

UPDATE user_tag_idx, calcTagCounts
  SET user_tag_idx.count = calcTagCounts.count
  WHERE user_tag_idx.id = calcTagCounts.id;

DELETE FROM calcTagCounts;

REPLACE calcTagCounts(id,count)
SELECT link_tag_idx.id, SUM(bookmark_tag_idx.count)
  FROM bookmark_tag_idx, bookmark, link_tag_idx
  WHERE bookmark_tag_idx.bookmark_id = bookmark.id AND
        bookmark.link_id = link_tag_idx.link_id AND
        bookmark_tag_idx.tag_id = link_tag_idx.tag_id
  GROUP BY link_tag_idx.id; 

UPDATE link_tag_idx, calcTagCounts
  SET link_tag_idx.count = calcTagCounts.count
  WHERE link_tag_idx.id = calcTagCounts.id;

DELETE FROM calcTagCounts;

CREATE TEMPORARY TABLE calcFldrTagCounts (fid INT, tid INT, count INT);

REPLACE calcFldrTagCounts(fid,tid,count)
SELECT bookmark_folder.folder_id, bookmark_tag_idx.tag_id, SUM(bookmark_tag_idx.count)
  FROM bookmark_folder, bookmark_tag_idx
  WHERE bookmark_folder.bookmark_id = bookmark_tag_idx.bookmark_id
  GROUP BY bookmark_folder.folder_id, bookmark_tag_idx.tag_id;

UPDATE folder_tag_idx, calcFldrTagCounts
  SET folder_tag_idx.count = calcFldrTagCounts.count
  WHERE folder_tag_idx.folder_id = calcFldrTagCounts.fid AND
        folder_tag_idx.tag_id = calcFldrTagCounts.tid; 
END//
CALL repairTagCounts()//
DROP PROCEDURE IF EXISTS repairTagCounts//  
  
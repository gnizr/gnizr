DELETE FROM user;
ALTER TABLE user AUTO_INCREMENT = 1;
INSERT INTO user(id,username,password,fullname,created_on,email,acct_status) VALUES(1,'gnizr',md5('gnizr'),'gnizr',now(),'',1);

DELETE FROM tag;
DELETE FROM user_tag_idx;
ALTER TABLE tag AUTO_INCREMENT = 1;
ALTER TABLE user_tag_idx AUTO_INCREMENT = 1;
INSERT INTO tag(id,tag,count) VALUES (1,'place',0),(2,'event',0),(3,'person',0);
INSERT INTO user_tag_idx(user_id,tag_id,count) VALUES (1,1,0),(1,2,0),(1,3,0);
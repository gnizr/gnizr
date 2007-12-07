ALTER TABLE `feed_subscription` ADD COLUMN `pub_date` DATETIME AFTER `auto_import`;
ALTER TABLE `feed_subscription` ADD INDEX feed_sub_last_sync_idx(`last_sync`);
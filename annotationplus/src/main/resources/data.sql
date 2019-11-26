REPLACE INTO `role` VALUES (1,'ADMIN');
alter table `source` modify `content` LONGTEXT;
alter table `source` modify `name` LONGTEXT;
alter table `annotation` modify `content` LONGTEXT;
alter table `feedback` modify `message` LONGTEXT;
SET GLOBAL max_allowed_packet = 1024*1024*14;
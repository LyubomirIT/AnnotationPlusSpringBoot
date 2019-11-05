REPLACE INTO `role` VALUES (1,'ADMIN');
alter table `note` modify `content` LONGTEXT;
alter table `note` modify `title` LONGTEXT;
alter table `annotation` modify `content` LONGTEXT;
alter table `feedback` modify `message` LONGTEXT;
SET GLOBAL max_allowed_packet = 1024*1024*14;
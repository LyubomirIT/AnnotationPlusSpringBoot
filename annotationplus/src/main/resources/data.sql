SET FOREIGN_KEY_CHECKS=0;
REPLACE INTO `role` VALUES (1,'ADMIN');
SET FOREIGN_KEY_CHECKS=1;
alter table `source` modify `content` LONGTEXT;
alter table `source` modify `name` LONGTEXT;
alter table `annotation` modify `content` LONGTEXT;
alter table `feedback` modify `message` LONGTEXT;
SET GLOBAL max_allowed_packet = 1000000000;
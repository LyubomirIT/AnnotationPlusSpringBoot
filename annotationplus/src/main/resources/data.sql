REPLACE INTO `role` VALUES (1,'ADMIN');
alter table `notes` modify `content` LONGTEXT;
alter table `annotation` modify `content` LONGTEXT;
SET GLOBAL max_allowed_packet = 1024*1024*14;

/*CREATE TABLE IF NOT EXISTS notes (
    id         	        BIGINT not null primary key,
    category_id        	BIGINT not null references category(id),
    title       	      VARCHAR(255),
    content             LONGBLOB,
    is_active           BOOLEAN,
    created_at         	DATETIME,
    updated_at         	DATETIME,
    user_id   	        BIGINT
);*/
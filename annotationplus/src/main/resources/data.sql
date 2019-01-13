REPLACE INTO `role` VALUES (1,'ADMIN');
alter table `notes` modify `content` LONGBLOB;

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
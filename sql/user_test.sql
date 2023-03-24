use jdbc;
CREATE TABLE
    USER
(
    id bigint NOT NULL AUTO_INCREMENT COMMENT '自增ID',
    userId VARCHAR(9) COMMENT '用户ID',
    userHead VARCHAR(16) COMMENT '用户头像',
    createTime TIMESTAMP NULL COMMENT '创建时间',
    updateTime TIMESTAMP NULL COMMENT '更新时间',
    userName VARCHAR(64),
    PRIMARY KEY (id)
)
    ENGINE=InnoDB DEFAULT CHARSET=utf8;

insert into user (id, userId, userHead, createTime, updateTime, userName) values (1, '10001', '1_04', '2022-04-13 00:00:00', '2022-04-13 00:00:00', '半糖');
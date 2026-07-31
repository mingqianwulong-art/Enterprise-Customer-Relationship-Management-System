-- 外勤签到表
CREATE TABLE IF NOT EXISTS `bus_sign_in` (
    `id`            BIGINT          NOT NULL AUTO_INCREMENT COMMENT '签到ID',
    `user_id`       BIGINT          NOT NULL                COMMENT '签到人ID',
    `customer_id`   BIGINT          DEFAULT NULL            COMMENT '关联客户ID（拜访签到时填写）',
    `customer_name` VARCHAR(128)    DEFAULT NULL            COMMENT '客户名称（冗余）',
    `sign_type`     TINYINT         NOT NULL                COMMENT '签到类型 1上午签到 2下午签退 3拜访签到',
    `latitude`      DECIMAL(10, 7)  DEFAULT NULL            COMMENT '纬度',
    `longitude`     DECIMAL(10, 7)  DEFAULT NULL            COMMENT '经度',
    `address`       VARCHAR(256)   DEFAULT NULL            COMMENT '签到地址',
    `remark`        VARCHAR(256)   DEFAULT NULL            COMMENT '备注',
    `sign_time`     DATETIME        NOT NULL                COMMENT '签到时间',
    `create_time`   DATETIME        DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_date` (`user_id`, `sign_time`),
    KEY `idx_customer` (`customer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='外勤签到表';

-- 菜单：外勤签到
INSERT INTO `sys_menu` (`title`, `path`, `icon`, `type`, `permission`, `sort`, `parent_id`, `visible`)
VALUES ('外勤签到', '/business/sign-in', 'Location', 2, 'business:sign:list', 30, 0, 1);

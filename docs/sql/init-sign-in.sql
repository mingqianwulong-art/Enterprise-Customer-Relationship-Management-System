-- ============================================================
-- 外勤签到表与菜单初始化
-- 说明：外勤签到功能依赖 bus_sign_in 表，此前仅在 docs 下，
--       本脚本并入 deploy/sql 部署流程，保证新环境完整可运行。
-- 适配 MySQL 8.0，可重复执行。
-- ============================================================

USE crm_db;

-- 1. 外勤签到表
CREATE TABLE IF NOT EXISTS `bus_sign_in` (
    `id`            BIGINT          NOT NULL AUTO_INCREMENT COMMENT '签到ID',
    `user_id`       BIGINT          NOT NULL                COMMENT '签到人ID',
    `customer_id`   BIGINT          DEFAULT NULL            COMMENT '关联客户ID（拜访签到时填写）',
    `customer_name` VARCHAR(128)    DEFAULT NULL            COMMENT '客户名称（冗余）',
    `sign_type`     TINYINT         NOT NULL                COMMENT '签到类型 1上午签到 2下午签退 3拜访签到',
    `latitude`      DECIMAL(10, 7)  DEFAULT NULL            COMMENT '纬度',
    `longitude`     DECIMAL(10, 7)  DEFAULT NULL            COMMENT '经度',
    `address`       VARCHAR(256)    DEFAULT NULL            COMMENT '签到地址',
    `remark`        VARCHAR(256)    DEFAULT NULL            COMMENT '备注',
    `sign_time`     DATETIME        NOT NULL                COMMENT '签到时间',
    `create_time`   DATETIME        DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_date` (`user_id`, `sign_time`),
    KEY `idx_customer` (`customer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='外勤签到表';

-- 2. 外勤签到菜单（挂在"商机销售"下，parent_id=4）
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `icon`, `type`, `perms`, `order_num`, `visible`, `status`)
SELECT 26, 4, '外勤签到', '/business/sign-in', 'Location', 2, 'business:sign-in:list', 30, 1, 1
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE id = 26 OR path = '/business/sign-in');

-- 3. 菜单授权给所有角色
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT r.id, 26 FROM sys_role r WHERE r.deleted = 0
AND NOT EXISTS (SELECT 1 FROM sys_role_menu rm WHERE rm.role_id = r.id AND rm.menu_id = 26);

-- 4. 验证
SELECT COUNT(*) AS sign_in_table FROM information_schema.TABLES
WHERE TABLE_SCHEMA = 'crm_db' AND TABLE_NAME = 'bus_sign_in';

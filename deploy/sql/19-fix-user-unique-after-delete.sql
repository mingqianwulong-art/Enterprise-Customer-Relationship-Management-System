-- ============================================================
-- 释放已逻辑删除用户占用的唯一索引
-- 说明：phone 为 NOT NULL，已删除用户统一置为 DEL-<id>；
--       email 可空，统一置为 NULL，避免占用唯一索引阻止复用。
-- 适配 MySQL 8.0，可重复执行
-- ============================================================

USE crm_db;

UPDATE sys_user SET phone = CONCAT('DEL-', id) WHERE deleted = 1;
UPDATE sys_user SET email = NULL WHERE deleted = 1;

-- 验证
SELECT id, username, phone, email, deleted FROM sys_user WHERE deleted = 1;

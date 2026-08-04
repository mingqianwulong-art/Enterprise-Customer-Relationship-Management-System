-- ============================================================
-- 用户名唯一约束调整
-- 说明：用户名唯一改为应用层按“部门 + 用户名”校验，
--       允许不同部门使用相同用户名，因此移除全局唯一索引 uk_username。
-- 适配 MySQL 8.0，可重复执行
-- ============================================================

USE crm_db;

SET @idx_exists = (SELECT COUNT(*) FROM information_schema.STATISTICS
    WHERE TABLE_SCHEMA = 'crm_db' AND TABLE_NAME = 'sys_user' AND INDEX_NAME = 'uk_username');
SET @sql = IF(@idx_exists > 0,
    'ALTER TABLE sys_user DROP INDEX uk_username',
    'SELECT ''uk_username already dropped''');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 验证
SELECT INDEX_NAME FROM information_schema.STATISTICS
WHERE TABLE_SCHEMA = 'crm_db' AND TABLE_NAME = 'sys_user' AND INDEX_NAME = 'uk_username';

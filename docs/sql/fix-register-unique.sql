-- ============================================================
-- 注册功能 - 手机号/邮箱唯一性约束
-- phone 改为 NOT NULL（必填）+ 唯一索引
-- email 改为 DEFAULT NULL（可选，空值用NULL避免冲突）+ 唯一索引
-- 适配 MySQL 8.0，可重复执行
-- ============================================================

USE crm_db;

-- 1. 先清理历史脏数据：将空字符串 email 改为 NULL
UPDATE sys_user SET email = NULL WHERE email = '';

-- 2. phone 改为 NOT NULL（已有数据都有手机号，安全修改）
ALTER TABLE sys_user MODIFY COLUMN phone VARCHAR(20) NOT NULL DEFAULT '' COMMENT '手机号';

-- 3. email 改为 DEFAULT NULL（空值用NULL，避免唯一索引冲突）
ALTER TABLE sys_user MODIFY COLUMN email VARCHAR(50) DEFAULT NULL COMMENT '邮箱';

-- 4. 添加 phone 唯一索引（如果不存在）
SET @idx_phone = (SELECT COUNT(*) FROM information_schema.STATISTICS
    WHERE TABLE_SCHEMA = 'crm_db' AND TABLE_NAME = 'sys_user' AND INDEX_NAME = 'uk_phone');
SET @sql1 = IF(@idx_phone = 0,
    'ALTER TABLE sys_user ADD UNIQUE KEY uk_phone (phone)',
    'SELECT ''uk_phone already exists''');
PREPARE stmt1 FROM @sql1;
EXECUTE stmt1;
DEALLOCATE PREPARE stmt1;

-- 5. 添加 email 唯一索引（如果不存在）
SET @idx_email = (SELECT COUNT(*) FROM information_schema.STATISTICS
    WHERE TABLE_SCHEMA = 'crm_db' AND TABLE_NAME = 'sys_user' AND INDEX_NAME = 'uk_email');
SET @sql2 = IF(@idx_email = 0,
    'ALTER TABLE sys_user ADD UNIQUE KEY uk_email (email)',
    'SELECT ''uk_email already exists''');
PREPARE stmt2 FROM @sql2;
EXECUTE stmt2;
DEALLOCATE PREPARE stmt2;

-- 6. 验证
SHOW INDEX FROM sys_user WHERE Key_name IN ('uk_phone', 'uk_email');

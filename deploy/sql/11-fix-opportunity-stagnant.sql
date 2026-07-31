-- ============================================================
-- P1-4 商机停滞预警 - 数据库变更脚本
-- 给 bus_opportunity 表添加 stage_change_time 字段
-- 适配 MySQL 8.0，可重复执行
-- ============================================================

USE crm_db;

-- 1. 添加阶段变更时间字段（如果不存在）
SET @col_exists = (SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = 'crm_db' AND TABLE_NAME = 'bus_opportunity' AND COLUMN_NAME = 'stage_change_time');
SET @sql = IF(@col_exists = 0,
    'ALTER TABLE bus_opportunity ADD COLUMN stage_change_time DATETIME DEFAULT NULL COMMENT ''阶段最后变更时间'' AFTER stage',
    'SELECT ''stage_change_time already exists''');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 2. 初始化现有数据的 stage_change_time 为 update_time
UPDATE bus_opportunity SET stage_change_time = update_time WHERE stage_change_time IS NULL;

-- 3. 添加索引以便定时任务高效查询
SET @idx_exists = (SELECT COUNT(*) FROM information_schema.STATISTICS
    WHERE TABLE_SCHEMA = 'crm_db' AND TABLE_NAME = 'bus_opportunity' AND INDEX_NAME = 'idx_stage_change');
SET @sql2 = IF(@idx_exists = 0,
    'CREATE INDEX idx_stage_change ON bus_opportunity(stage_change_time, stage)',
    'SELECT ''idx_stage_change already exists''');
PREPARE stmt2 FROM @sql2;
EXECUTE stmt2;
DEALLOCATE PREPARE stmt2;

-- 4. 验证
SELECT id, opp_name, stage, stage_change_time, update_time FROM bus_opportunity LIMIT 5;

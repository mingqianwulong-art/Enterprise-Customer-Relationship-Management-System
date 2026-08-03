-- ============================================================
-- 为业务表 owner_id 添加索引（优化数据权限过滤查询性能）
-- 数据权限按 owner_id IN (...) 过滤，无索引时全表扫描
-- 注：bus_opportunity / cus_customer / market_clue 已有 idx_owner
-- ============================================================

-- 合同表 owner_id 索引
ALTER TABLE bus_contract ADD INDEX idx_owner (owner_id);

-- 回款表 owner_id 索引
ALTER TABLE bus_payment ADD INDEX idx_owner (owner_id);

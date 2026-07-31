-- 修复各模块新增功能失效问题
-- 原因：多个表的字段使用了 DEFAULT '' + UNIQUE KEY，导致多次新增不填该字段时空字符串重复冲突
-- 修复：将 DEFAULT '' 改为 DEFAULT NULL，NULL 不参与 UNIQUE 约束比较

-- 1. 客户表 - credit_code
ALTER TABLE cus_customer MODIFY COLUMN credit_code VARCHAR(50) DEFAULT NULL COMMENT '统一社会信用代码（查重依据）';
-- 清理已有的空字符串记录，改为 NULL
UPDATE cus_customer SET credit_code = NULL WHERE credit_code = '';

-- 2. 合同表 - contract_no
ALTER TABLE bus_contract MODIFY COLUMN contract_no VARCHAR(50) DEFAULT NULL COMMENT '合同编号';
UPDATE bus_contract SET contract_no = NULL WHERE contract_no = '';

-- 3. 回款表 - payment_no
ALTER TABLE bus_payment MODIFY COLUMN payment_no VARCHAR(50) DEFAULT NULL COMMENT '回款编号';
UPDATE bus_payment SET payment_no = NULL WHERE payment_no = '';

-- 4. 工单表 - order_no
ALTER TABLE ser_order MODIFY COLUMN order_no VARCHAR(50) DEFAULT NULL COMMENT '工单编号';
UPDATE ser_order SET order_no = NULL WHERE order_no = '';

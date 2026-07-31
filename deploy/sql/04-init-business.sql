-- ============================================================
-- 商机销售模块数据库初始化脚本
-- 适配 MySQL 8.0
-- ============================================================

USE crm_db;

-- ============================================================
-- 1. 商机表
-- ============================================================
DROP TABLE IF EXISTS bus_opportunity;
CREATE TABLE bus_opportunity (
  id              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '商机ID',
  opp_name        VARCHAR(200) NOT NULL COMMENT '商机名称',
  customer_id     BIGINT       NOT NULL COMMENT '客户ID',
  customer_name   VARCHAR(200) DEFAULT '' COMMENT '客户名称（冗余字段，方便查询）',
  contact_id     BIGINT       DEFAULT NULL COMMENT '联系人ID',
  estimated_amount DECIMAL(14,2) DEFAULT 0 COMMENT '预计成交金额',
  stage           TINYINT      DEFAULT 1 COMMENT '商机阶段（1需求确认 2方案报价 3商务谈判 4合同签订 5已赢单 6已输单）',
  probability     INT          DEFAULT 10 COMMENT '成交概率（百分比）',
  expected_close_date DATE    DEFAULT NULL COMMENT '预计成交日期',
  owner_id        BIGINT       DEFAULT NULL COMMENT '负责人ID',
  source          VARCHAR(50)  DEFAULT '' COMMENT '商机来源',
  description     TEXT         COMMENT '商机描述',
  create_by       VARCHAR(50)  DEFAULT '' COMMENT '创建人',
  create_time     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_by       VARCHAR(50)  DEFAULT '' COMMENT '更新人',
  update_time     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted         TINYINT      DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (id),
  KEY idx_customer (customer_id),
  KEY idx_stage (stage),
  KEY idx_owner (owner_id)
) ENGINE=InnoDB COMMENT='商机表';

-- 初始测试数据
INSERT INTO bus_opportunity (opp_name, customer_id, customer_name, estimated_amount, stage, probability, expected_close_date, owner_id, source, description) VALUES
('CRM系统采购', 1, '测试客户A', 150000.00, 2, 40, '2026-08-15', 2, '展会', '客户需要一套CRM系统，正在评估方案'),
('机械臂升级方案', 2, '测试客户B', 80000.00, 3, 60, '2026-08-20', 2, '官网', '客户对机械臂升级有明确需求，正在谈判价格'),
('年度IT服务合同', 3, '测试客户C', 200000.00, 4, 80, '2026-08-10', 3, '微信', '已发送合同草稿，等待客户确认'),
('电商系统定制开发', 1, '测试客户A', 300000.00, 1, 20, '2026-09-01', 2, '转介绍', '客户有定制开发意向，需求待确认'),
('ERP系统集成', 4, '测试客户D', 120000.00, 5, 100, '2026-07-20', 3, '门店', '已签约，等待回款');

-- ============================================================
-- 2. 合同表
-- ============================================================
DROP TABLE IF EXISTS bus_contract;
CREATE TABLE bus_contract (
  id              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '合同ID',
  contract_no     VARCHAR(50)  NOT NULL COMMENT '合同编号',
  contract_name   VARCHAR(200) NOT NULL COMMENT '合同名称',
  customer_id     BIGINT       NOT NULL COMMENT '客户ID',
  customer_name   VARCHAR(200) DEFAULT '' COMMENT '客户名称',
  opp_id          BIGINT       DEFAULT NULL COMMENT '关联商机ID',
  amount          DECIMAL(14,2) NOT NULL DEFAULT 0 COMMENT '合同金额',
  signed_date     DATE         DEFAULT NULL COMMENT '签订日期',
  start_date      DATE         DEFAULT NULL COMMENT '开始日期',
  end_date        DATE         DEFAULT NULL COMMENT '结束日期',
  status          TINYINT      DEFAULT 0 COMMENT '状态（0待审批 1已审批 2已签订 3已作废）',
  approver_id     BIGINT       DEFAULT NULL COMMENT '审批人ID',
  approve_time    DATETIME     DEFAULT NULL COMMENT '审批时间',
  owner_id        BIGINT       DEFAULT NULL COMMENT '负责人ID',
  remark          VARCHAR(500) DEFAULT '' COMMENT '备注',
  create_by       VARCHAR(50)  DEFAULT '' COMMENT '创建人',
  create_time     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_by       VARCHAR(50)  DEFAULT '' COMMENT '更新人',
  update_time     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted         TINYINT      DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (id),
  UNIQUE KEY uk_contract_no (contract_no),
  KEY idx_customer (customer_id),
  KEY idx_opp (opp_id)
) ENGINE=InnoDB COMMENT='合同表';

-- 初始测试数据
INSERT INTO bus_contract (contract_no, contract_name, customer_id, customer_name, opp_id, amount, signed_date, start_date, end_date, status, owner_id, remark) VALUES
('HT-2026-001', 'CRM系统采购合同', 1, '测试客户A', 1, 150000.00, '2026-07-15', '2026-08-01', '2027-07-31', 2, 2, '按里程碑付款'),
('HT-2026-002', 'ERP系统集成合同', 4, '测试客户D', 5, 120000.00, '2026-07-20', '2026-08-01', '2027-07-31', 2, 3, '分三期付款'),
('HT-2026-003', '年度IT服务合同', 3, '测试客户C', 3, 200000.00, '2026-07-25', '2026-08-01', '2027-07-31', 1, 3, '待财务审批'),
('HT-2026-004', '机械臂升级方案合同', 2, '测试客户B', 2, 80000.00, NULL, NULL, NULL, 0, 2, '合同草稿中');

-- ============================================================
-- 3. 回款表
-- ============================================================
DROP TABLE IF EXISTS bus_payment;
CREATE TABLE bus_payment (
  id              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '回款ID',
  contract_id     BIGINT       NOT NULL COMMENT '合同ID',
  contract_no    VARCHAR(50)  DEFAULT '' COMMENT '合同编号（冗余）',
  customer_id    BIGINT       DEFAULT NULL COMMENT '客户ID',
  customer_name  VARCHAR(200) DEFAULT '' COMMENT '客户名称',
  payment_no     VARCHAR(50)  NOT NULL COMMENT '回款编号',
  plan_date      DATE         DEFAULT NULL COMMENT '计划回款日期',
  actual_date    DATE         DEFAULT NULL COMMENT '实际回款日期',
  plan_amount    DECIMAL(14,2) DEFAULT 0 COMMENT '计划回款金额',
  actual_amount  DECIMAL(14,2) DEFAULT 0 COMMENT '实际回款金额',
  payment_stage  VARCHAR(50)  DEFAULT '' COMMENT '回款阶段（首付款/进度款/尾款）',
  status         TINYINT      DEFAULT 0 COMMENT '状态（0待回款 1部分回款 2已回款 3已逾期）',
  owner_id       BIGINT       DEFAULT NULL COMMENT '负责人ID',
  remark         VARCHAR(500) DEFAULT '' COMMENT '备注',
  create_by      VARCHAR(50)  DEFAULT '' COMMENT '创建人',
  create_time    DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_by      VARCHAR(50)  DEFAULT '' COMMENT '更新人',
  update_time    DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted        TINYINT      DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (id),
  UNIQUE KEY uk_payment_no (payment_no),
  KEY idx_contract (contract_id),
  KEY idx_customer (customer_id),
  KEY idx_status (status)
) ENGINE=InnoDB COMMENT='回款表';

-- 初始测试数据
INSERT INTO bus_payment (contract_id, contract_no, customer_id, customer_name, payment_no, plan_date, actual_date, plan_amount, actual_amount, payment_stage, status, owner_id) VALUES
(1, 'HT-2026-001', 1, '测试客户A', 'PAY-2026-001', '2026-08-01', '2026-08-01', 45000.00, 45000.00, '首付款', 2, 2),
(1, 'HT-2026-001', 1, '测试客户A', 'PAY-2026-002', '2026-10-01', NULL, 60000.00, 0, '进度款', 0, 2),
(1, 'HT-2026-001', 1, '测试客户A', 'PAY-2026-003', '2026-12-01', NULL, 45000.00, 0, '尾款', 0, 2),
(2, 'HT-2026-002', 4, '测试客户D', 'PAY-2026-004', '2026-08-01', '2026-08-02', 40000.00, 40000.00, '首付款', 2, 3),
(2, 'HT-2026-002', 4, '测试客户D', 'PAY-2026-005', '2026-10-01', NULL, 40000.00, 0, '进度款', 0, 3),
(2, 'HT-2026-002', 4, '测试客户D', 'PAY-2026-006', '2026-12-01', NULL, 40000.00, 0, '尾款', 0, 3),
(3, 'HT-2026-003', 3, '测试客户C', 'PAY-2026-007', '2026-09-01', NULL, 100000.00, 0, '首付款', 0, 3);

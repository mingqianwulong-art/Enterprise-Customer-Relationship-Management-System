-- ============================================================
-- 市场获客模块数据库初始化脚本
-- 适配 MySQL 8.0
-- ============================================================

USE crm_db;

-- ============================================================
-- 1. 线索表
-- ============================================================
DROP TABLE IF EXISTS market_clue;
CREATE TABLE market_clue (
  id              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '线索ID',
  clue_name       VARCHAR(100) NOT NULL COMMENT '线索名称（客户/联系人姓名）',
  company        VARCHAR(200) DEFAULT '' COMMENT '公司名称',
  phone          VARCHAR(50)  DEFAULT '' COMMENT '联系电话',
  email          VARCHAR(100) DEFAULT '' COMMENT '邮箱',
  source         VARCHAR(50)  DEFAULT '' COMMENT '线索来源（展会/门店/抖音/微信/官网/其他）',
  channel_id     BIGINT       DEFAULT NULL COMMENT '渠道ID',
  industry       VARCHAR(50)  DEFAULT '' COMMENT '所属行业',
  region         VARCHAR(100) DEFAULT '' COMMENT '所在区域',
  level          TINYINT      DEFAULT 1 COMMENT '线索等级（1低 2中 3高）',
  status         TINYINT      DEFAULT 0 COMMENT '状态（0待分配 1已分配 2已转化 3已废弃）',
  owner_id       BIGINT       DEFAULT NULL COMMENT '负责人ID',
  customer_id    BIGINT       DEFAULT NULL COMMENT '转化后的客户ID',
  description    TEXT         COMMENT '需求描述',
  create_by      VARCHAR(50)  DEFAULT '' COMMENT '创建人',
  create_time    DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_by      VARCHAR(50)  DEFAULT '' COMMENT '更新人',
  update_time    DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted        TINYINT      DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (id),
  KEY idx_status (status),
  KEY idx_owner (owner_id),
  KEY idx_channel (channel_id)
) ENGINE=InnoDB COMMENT='线索表';

-- 初始测试数据
INSERT INTO market_clue (clue_name, company, phone, source, channel_id, industry, region, level, status, description) VALUES
('温州张总', '温州某机械有限公司', '13800001111', '展会', 1, '机械制造', '温州', 3, 0, '展会上收集的意向客户，需要机械臂方案'),
('李老板', '某电商科技公司', '13800002222', '抖音', 2, '电子商务', '杭州', 2, 0, '抖音广告来的线索，想了解CRM系统'),
('王经理', '某贸易进出口', '13800003333', '官网', 3, '贸易', '宁波', 2, 1, '官网表单提交，已分配销售跟进'),
('赵总', '某服装加工厂', '13800004444', '微信', 4, '服装纺织', '温州', 3, 2, '微信咨询，已转化为客户'),
('钱先生', '某餐饮连锁', '13800005555', '门店', 1, '餐饮服务', '温州', 1, 0, '线下门店推荐');

-- ============================================================
-- 2. 渠道表
-- ============================================================
DROP TABLE IF EXISTS market_channel;
CREATE TABLE market_channel (
  id              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '渠道ID',
  channel_name    VARCHAR(100) NOT NULL COMMENT '渠道名称',
  channel_type    VARCHAR(50)  DEFAULT '' COMMENT '渠道类型（线上/线下/展会/社交）',
  contact        VARCHAR(100) DEFAULT '' COMMENT '联系人',
  phone          VARCHAR(50)  DEFAULT '' COMMENT '联系电话',
  cost           DECIMAL(12,2) DEFAULT 0 COMMENT '获客成本',
  status         TINYINT      DEFAULT 1 COMMENT '状态（0停用 1启用）',
  remark         VARCHAR(500) DEFAULT '' COMMENT '备注',
  create_by      VARCHAR(50)  DEFAULT '' COMMENT '创建人',
  create_time    DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_by      VARCHAR(50)  DEFAULT '' COMMENT '更新人',
  update_time    DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted        TINYINT      DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (id)
) ENGINE=InnoDB COMMENT='渠道表';

-- 初始测试数据
INSERT INTO market_channel (channel_name, channel_type, contact, phone, cost, status, remark) VALUES
('温州工业博览会', '展会', '展会组委会', '0577-88888888', 50000.00, 1, '每年3月举办'),
('抖音推广', '社交', '字节跳动', '400-8888-888', 12000.00, 1, '按CPC计费'),
('企业官网', '线上', '技术部', '0577-87654321', 8000.00, 1, 'SEO+SEM'),
('企业微信', '社交', '运营部', '0577-81234567', 5000.00, 1, '社群运营'),
('线下门店', '线下', '门店经理', '0577-87651234', 15000.00, 1, '5家直营店');

-- ============================================================
-- 3. 知识库表
-- ============================================================
DROP TABLE IF EXISTS market_knowledge;
CREATE TABLE market_knowledge (
  id              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '知识库ID',
  title           VARCHAR(200) NOT NULL COMMENT '标题',
  category        VARCHAR(50)  DEFAULT '' COMMENT '分类（话术/方案/案例/培训）',
  content         TEXT         COMMENT '正文内容',
  tags            VARCHAR(200) DEFAULT '' COMMENT '标签（逗号分隔）',
  view_count      INT          DEFAULT 0 COMMENT '浏览次数',
  status          TINYINT      DEFAULT 1 COMMENT '状态（0下架 1上架）',
  create_by       VARCHAR(50)  DEFAULT '' COMMENT '创建人',
  create_time     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_by       VARCHAR(50)  DEFAULT '' COMMENT '更新人',
  update_time     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted         TINYINT      DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (id),
  KEY idx_category (category)
) ENGINE=InnoDB COMMENT='知识库表';

-- 初始测试数据
INSERT INTO market_knowledge (title, category, content, tags, view_count, status) VALUES
('电话销售开场白话术', '话术', '您好，我是XX公司的XX，我们专注于为企业提供CRM解决方案，最近看到贵公司在扩展业务，想和您交流一下如何提升客户管理效率...', '电话,开场白,销售', 128, 1),
('温州制造业CRM方案模板', '方案', '针对温州本地制造业的特点，本方案重点解决以下问题：1.客户资源分散 2.跟进记录缺失 3.商机流失...', '温州,制造业,方案', 89, 1),
('客户异议处理话术集', '话术', '常见异议：1.价格太贵 → 2.已经有系统了 → 3.暂时不需要 → 对应话术...', '异议,话术,销售', 256, 1),
('CRM系统功能介绍PPT', '培训', '本PPT涵盖CRM系统的六大模块：市场获客、客户管理、商机销售、售后服务、数据分析、系统管理...', '培训,PPT,介绍', 67, 1),
('成功案例：某机械企业CRM落地', '案例', '温州某机械有限公司通过部署CRM系统，3个月内客户转化率提升35%，跟进效率提升50%...', '案例,机械,温州', 145, 1);

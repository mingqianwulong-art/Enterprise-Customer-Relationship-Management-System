-- ============================================================
-- 客户管理模块 - 建表脚本
-- 在 Navicat 中选中 crm_db 库，新建查询执行即可
-- ============================================================

USE crm_db;

-- 1. 客户表
DROP TABLE IF EXISTS cus_customer;
CREATE TABLE cus_customer (
  id               BIGINT        NOT NULL AUTO_INCREMENT COMMENT '客户ID',
  name             VARCHAR(100)  NOT NULL COMMENT '客户名称',
  credit_code      VARCHAR(50)   DEFAULT '' COMMENT '统一社会信用代码（查重依据）',
  industry         VARCHAR(50)   DEFAULT '' COMMENT '所属行业',
  region           VARCHAR(50)   DEFAULT '' COMMENT '所在区域（温州各区县）',
  customer_level   TINYINT       DEFAULT 1 COMMENT '客户等级 1普通 2重要 3VIP',
  owner_id         BIGINT        DEFAULT NULL COMMENT '负责人ID（关联sys_user）',
  in_pool          TINYINT       DEFAULT 0 COMMENT '是否在公海 0否 1是',
  last_follow_time DATETIME      DEFAULT NULL COMMENT '最后跟进时间',
  total_amount     DECIMAL(14,2) DEFAULT 0 COMMENT '累计合作金额',
  remark           VARCHAR(500)  DEFAULT '' COMMENT '备注',
  create_by        VARCHAR(50)   DEFAULT '' COMMENT '创建人',
  create_time      DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_by        VARCHAR(50)   DEFAULT '' COMMENT '更新人',
  update_time      DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted          TINYINT       DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (id),
  UNIQUE KEY uk_credit_code (credit_code),
  KEY idx_owner (owner_id, in_pool),
  KEY idx_name (name),
  KEY idx_last_follow (last_follow_time)
) ENGINE=InnoDB COMMENT='客户表';

-- 2. 联系人表
DROP TABLE IF EXISTS cus_contact;
CREATE TABLE cus_contact (
  id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '联系人ID',
  customer_id BIGINT       NOT NULL COMMENT '客户ID',
  name        VARCHAR(50)  NOT NULL COMMENT '联系人姓名',
  position    VARCHAR(50)  DEFAULT '' COMMENT '职位',
  phone       VARCHAR(20)  DEFAULT '' COMMENT '联系电话',
  email       VARCHAR(50)  DEFAULT '' COMMENT '邮箱',
  is_primary  TINYINT      DEFAULT 0 COMMENT '是否主联系人 0否 1是',
  create_by   VARCHAR(50)  DEFAULT '' COMMENT '创建人',
  create_time DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_by   VARCHAR(50)  DEFAULT '' COMMENT '更新人',
  update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted     TINYINT      DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (id),
  KEY idx_customer (customer_id)
) ENGINE=InnoDB COMMENT='联系人表';

-- 3. 标签表
DROP TABLE IF EXISTS cus_tag;
CREATE TABLE cus_tag (
  id          BIGINT      NOT NULL AUTO_INCREMENT COMMENT '标签ID',
  tag_name    VARCHAR(50) NOT NULL COMMENT '标签名称',
  tag_color   VARCHAR(20) DEFAULT '#409EFF' COMMENT '标签颜色',
  tag_type    TINYINT     DEFAULT 2 COMMENT '1系统内置 2自定义',
  create_by   VARCHAR(50) DEFAULT '' COMMENT '创建人',
  create_time DATETIME    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_by   VARCHAR(50) DEFAULT '' COMMENT '更新人',
  update_time DATETIME    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted     TINYINT     DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (id),
  UNIQUE KEY uk_tag_name (tag_name)
) ENGINE=InnoDB COMMENT='客户标签表';

-- 4. 客户-标签关联表
DROP TABLE IF EXISTS cus_customer_tag;
CREATE TABLE cus_customer_tag (
  customer_id BIGINT NOT NULL COMMENT '客户ID',
  tag_id      BIGINT NOT NULL COMMENT '标签ID',
  PRIMARY KEY (customer_id, tag_id)
) ENGINE=InnoDB COMMENT='客户标签关联表';

-- 5. 跟进记录表
DROP TABLE IF EXISTS cus_follow_record;
CREATE TABLE cus_follow_record (
  id               BIGINT        NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  customer_id      BIGINT        NOT NULL COMMENT '客户ID',
  user_id          BIGINT        NOT NULL COMMENT '跟进人ID',
  follow_type      TINYINT       NOT NULL COMMENT '跟进方式 1电话 2上门拜访 3微信 4其他',
  content          TEXT          COMMENT '跟进内容',
  next_follow_time DATETIME      DEFAULT NULL COMMENT '下次跟进时间',
  attachments      VARCHAR(2000) DEFAULT '' COMMENT '附件URL（JSON数组）',
  create_by        VARCHAR(50)   DEFAULT '' COMMENT '创建人',
  create_time      DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_by        VARCHAR(50)   DEFAULT '' COMMENT '更新人',
  update_time      DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted          TINYINT       DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (id),
  KEY idx_customer (customer_id),
  KEY idx_user (user_id),
  KEY idx_next_follow (next_follow_time)
) ENGINE=InnoDB COMMENT='客户跟进记录表';

-- 6. 初始数据：预设标签
INSERT INTO cus_tag (tag_name, tag_color, tag_type) VALUES
('温州本地制造业',  '#E6A23C', 1),
('高复购意向',      '#67C23A', 1),
('3个月未互动',     '#F56C6C', 1),
('重要客户',        '#409EFF', 1),
('新客户',          '#909399', 1);

-- 验证
SELECT 'cus_customer' AS 表名, COUNT(*) AS 记录数 FROM cus_customer
UNION ALL SELECT 'cus_contact', COUNT(*) FROM cus_contact
UNION ALL SELECT 'cus_tag', COUNT(*) FROM cus_tag
UNION ALL SELECT 'cus_customer_tag', COUNT(*) FROM cus_customer_tag
UNION ALL SELECT 'cus_follow_record', COUNT(*) FROM cus_follow_record;

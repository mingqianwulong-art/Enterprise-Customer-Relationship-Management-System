-- ============================================================
-- 售后服务模块数据库初始化脚本
-- 适配 MySQL 8.0
-- 模块: 工单管理 + 售后记录
-- ============================================================

USE crm_db;

-- ============================================================
-- 1. 工单表
-- ============================================================
DROP TABLE IF EXISTS ser_order;
CREATE TABLE ser_order (
  id                  BIGINT       NOT NULL AUTO_INCREMENT COMMENT '工单ID',
  order_no            VARCHAR(32)  NOT NULL COMMENT '工单编号',
  customer_id         BIGINT       NOT NULL COMMENT '客户ID',
  customer_name       VARCHAR(200) DEFAULT '' COMMENT '客户名称（冗余）',
  contact_id          BIGINT       DEFAULT NULL COMMENT '联系人ID',
  contact_name        VARCHAR(64)  DEFAULT '' COMMENT '联系人姓名（冗余）',
  title               VARCHAR(128) NOT NULL COMMENT '工单标题',
  description         TEXT         COMMENT '问题描述',
  type                TINYINT      DEFAULT 1 COMMENT '工单类型（1售后咨询 2投诉 3维修 4安装 5退换货 6其他）',
  source              TINYINT      DEFAULT 1 COMMENT '来源（1电话 2微信 3门店 4邮件 5其他）',
  priority            TINYINT      DEFAULT 2 COMMENT '优先级（1紧急 2普通 3低）',
  status              TINYINT      DEFAULT 0 COMMENT '状态（0待处理 1处理中 2待反馈 3已完成 4已关闭 5已取消）',
  assignee_id         BIGINT       DEFAULT NULL COMMENT '处理人ID',
  assignee_name       VARCHAR(64)  DEFAULT '' COMMENT '处理人姓名（冗余）',
  resolve_time        DATETIME     DEFAULT NULL COMMENT '解决时间',
  satisfaction        TINYINT      DEFAULT NULL COMMENT '满意度评分（1-5）',
  satisfaction_comment VARCHAR(255) DEFAULT '' COMMENT '满意度评价内容',
  create_by           VARCHAR(50)  DEFAULT '' COMMENT '创建人',
  create_time         DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_by           VARCHAR(50)  DEFAULT '' COMMENT '更新人',
  update_time         DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted             TINYINT      DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (id),
  UNIQUE KEY uk_order_no (order_no),
  KEY idx_customer (customer_id),
  KEY idx_status (status),
  KEY idx_assignee (assignee_id)
) ENGINE=InnoDB COMMENT='工单表';

-- ============================================================
-- 2. 售后记录表
-- ============================================================
DROP TABLE IF EXISTS ser_record;
CREATE TABLE ser_record (
  id              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  order_id        BIGINT       DEFAULT NULL COMMENT '关联工单ID',
  order_no        VARCHAR(32)  DEFAULT '' COMMENT '关联工单编号（冗余）',
  customer_id     BIGINT       NOT NULL COMMENT '客户ID',
  customer_name   VARCHAR(200) DEFAULT '' COMMENT '客户名称（冗余）',
  type            TINYINT      DEFAULT 1 COMMENT '记录类型（1保修 2安装 3退换货 4维修 5咨询 6其他）',
  title           VARCHAR(128) NOT NULL COMMENT '记录标题',
  content         TEXT         COMMENT '记录内容',
  result          VARCHAR(255) DEFAULT '' COMMENT '处理结果',
  handler_id      BIGINT       DEFAULT NULL COMMENT '处理人ID',
  handler_name    VARCHAR(64)  DEFAULT '' COMMENT '处理人姓名（冗余）',
  handle_time     DATETIME     DEFAULT NULL COMMENT '处理时间',
  attachments      VARCHAR(512) DEFAULT '' COMMENT '附件URL（逗号分隔）',
  create_by       VARCHAR(50)  DEFAULT '' COMMENT '创建人',
  create_time     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_by       VARCHAR(50)  DEFAULT '' COMMENT '更新人',
  update_time     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted         TINYINT      DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (id),
  KEY idx_order (order_id),
  KEY idx_customer (customer_id),
  KEY idx_type (type)
) ENGINE=InnoDB COMMENT='售后记录表';

-- ============================================================
-- 3. 测试数据 - 工单
-- ============================================================
INSERT INTO ser_order (order_no, customer_id, customer_name, contact_name, title, description, type, source, priority, status, assignee_id, assignee_name, resolve_time, satisfaction, satisfaction_comment, create_by) VALUES
('WO20260720001', 1, '北京阿里巴巴科技有限公司', '张经理', '软件系统无法登录', '用户反馈系统登录页面报错500', 1, 1, 2, 0, 1, '管理员', NULL, NULL, '', 'admin'),
('WO20260720002', 2, '上海腾讯计算机系统有限公司', '李总监', '合同到期续费咨询', '客户咨询合同续费流程和优惠政策', 1, 2, 3, 1, 2, '销售员01', NULL, NULL, '', 'sale01'),
('WO20260720003', 3, '深圳华为技术有限公司', '王工', '服务器硬件故障', '客户服务器出现硬件故障需要维修', 3, 3, 1, 3, 1, '管理员', '2026-07-22 14:30:00', 5, '响应迅速，处理专业', 'admin'),
('WO20260720004', 1, '北京阿里巴巴科技有限公司', '张经理', '功能升级安装', '客户要求安装最新版本并配置新功能', 4, 4, 2, 3, 1, '管理员', '2026-07-25 10:00:00', 4, '服务不错，略有延迟', 'admin'),
('WO20260720005', 4, '杭州网易雷火科技有限公司', '赵总', '产品退货申请', '客户对产品不满意申请退货', 5, 1, 2, 4, 2, '销售员01', '2026-07-26 16:00:00', 3, '退货流程较慢', 'admin');

-- ============================================================
-- 4. 测试数据 - 售后记录
-- ============================================================
INSERT INTO ser_record (order_id, order_no, customer_id, customer_name, type, title, content, result, handler_name, handle_time, create_by) VALUES
(1, 'WO20260720001', 1, '北京阿里巴巴科技有限公司', 5, '远程技术支持', '通过远程桌面协助客户检查登录问题，发现是浏览器缓存导致', '清理缓存后恢复正常', '管理员', '2026-07-20 11:30:00', 'admin'),
(3, 'WO20260720003', 3, '深圳华为技术有限公司', 4, '硬件故障维修', '工程师上门检测，确认主板故障，更换主板', '主板更换完成，系统恢复', '管理员', '2026-07-22 14:00:00', 'admin'),
(3, 'WO20260720003', 3, '深圳华为技术有限公司', 1, '保修期内维修', '该设备在保修期内，免费更换主板', '保修申请通过，免费维修', '管理员', '2026-07-22 14:30:00', 'admin'),
(4, 'WO20260720004', 1, '北京阿里巴巴科技有限公司', 2, '系统安装部署', '为客户安装最新版本系统，配置新功能模块', '安装部署完成，测试通过', '管理员', '2026-07-25 10:00:00', 'admin'),
(NULL, '', 2, '上海腾讯计算机系统有限公司', 5, '合同续费咨询记录', '客户咨询下一年度合同续费方案，已发送报价单', '客户收到报价单，待确认', '销售员01', '2026-07-21 09:00:00', 'sale01'),
(NULL, '', 5, '广州字节跳动科技有限公司', 1, '设备保修登记', '客户购买设备3个月内出现故障，登记保修', '保修已登记，等待维修安排', '管理员', '2026-07-23 15:00:00', 'admin');

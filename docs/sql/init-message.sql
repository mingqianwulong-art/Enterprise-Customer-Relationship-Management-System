-- ============================================================
-- P1-5 跟进提醒推送 - 系统消息表
-- 适配 MySQL 8.0，可重复执行
-- ============================================================

USE crm_db;

-- 系统消息表
CREATE TABLE IF NOT EXISTS sys_message (
  id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '消息ID',
  user_id     BIGINT       NOT NULL COMMENT '接收人ID',
  title       VARCHAR(200) NOT NULL COMMENT '消息标题',
  content     TEXT         COMMENT '消息内容',
  type        TINYINT      DEFAULT 1 COMMENT '消息类型 1跟进提醒 2商机预警 3系统通知',
  ref_id      BIGINT       DEFAULT NULL COMMENT '关联业务ID',
  ref_type    VARCHAR(50)  DEFAULT '' COMMENT '关联业务类型',
  is_read     TINYINT      DEFAULT 0 COMMENT '是否已读 0未读 1已读',
  create_time DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (id),
  KEY idx_user_read (user_id, is_read),
  KEY idx_type (type)
) ENGINE=InnoDB COMMENT='系统消息表';

-- 验证
SELECT 'sys_message' AS table_name, COUNT(*) AS cnt FROM sys_message;

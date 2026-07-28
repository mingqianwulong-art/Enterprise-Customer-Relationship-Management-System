-- ============================================================
-- 企业客户关系管理系统（CRM）- 数据库初始化脚本
-- 适配 MySQL 8.0
-- 在 Navicat 中新建查询，全选本文件内容执行即可
-- ============================================================

-- 1. 建库
DROP DATABASE IF EXISTS crm_db;
CREATE DATABASE crm_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE crm_db;

-- ============================================================
-- 2. 系统基础模块表
-- ============================================================

-- 2.1 部门表
DROP TABLE IF EXISTS sys_dept;
CREATE TABLE sys_dept (
  id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '部门ID',
  parent_id   BIGINT       DEFAULT 0 COMMENT '父部门ID',
  ancestors   VARCHAR(500) DEFAULT '' COMMENT '祖级路径(用-分隔)',
  dept_name   VARCHAR(50)  NOT NULL COMMENT '部门名称',
  order_num   INT          DEFAULT 0 COMMENT '显示顺序',
  status      TINYINT      DEFAULT 1 COMMENT '0停用 1启用',
  create_by   VARCHAR(50)  DEFAULT '' COMMENT '创建人',
  create_time DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_by   VARCHAR(50)  DEFAULT '' COMMENT '更新人',
  update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted     TINYINT      DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (id)
) ENGINE=InnoDB COMMENT='部门表';

-- 2.2 用户表
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
  id              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  dept_id         BIGINT       DEFAULT NULL COMMENT '部门ID',
  username        VARCHAR(50)  NOT NULL COMMENT '登录名',
  password        VARCHAR(100) NOT NULL COMMENT '密码(BCrypt)',
  real_name       VARCHAR(50)  DEFAULT '' COMMENT '真实姓名',
  phone           VARCHAR(20)  DEFAULT '' COMMENT '手机号',
  email           VARCHAR(50)  DEFAULT '' COMMENT '邮箱',
  sex             TINYINT      DEFAULT 0 COMMENT '0未知 1男 2女',
  avatar          VARCHAR(200) DEFAULT '' COMMENT '头像URL',
  status          TINYINT      DEFAULT 1 COMMENT '0停用 1启用',
  last_login_time DATETIME     DEFAULT NULL COMMENT '最后登录时间',
  create_by       VARCHAR(50)  DEFAULT '' COMMENT '创建人',
  create_time     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_by       VARCHAR(50)  DEFAULT '' COMMENT '更新人',
  update_time     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted         TINYINT      DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (id),
  UNIQUE KEY uk_username (username)
) ENGINE=InnoDB COMMENT='用户表';

-- 2.3 角色表
DROP TABLE IF EXISTS sys_role;
CREATE TABLE sys_role (
  id         BIGINT      NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  role_code  VARCHAR(50) NOT NULL COMMENT '角色码',
  role_name  VARCHAR(50) NOT NULL COMMENT '角色名',
  data_scope TINYINT     DEFAULT 1 COMMENT '数据范围 1本人 2本部门 3本部门及下 4全部',
  status     TINYINT     DEFAULT 1 COMMENT '0停用 1启用',
  remark     VARCHAR(200) DEFAULT '' COMMENT '备注',
  create_by  VARCHAR(50) DEFAULT '' COMMENT '创建人',
  create_time DATETIME   DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_by  VARCHAR(50) DEFAULT '' COMMENT '更新人',
  update_time DATETIME   DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted    TINYINT     DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (id),
  UNIQUE KEY uk_role_code (role_code)
) ENGINE=InnoDB COMMENT='角色表';

-- 2.4 用户-角色关联表
DROP TABLE IF EXISTS sys_user_role;
CREATE TABLE sys_user_role (
  user_id BIGINT NOT NULL COMMENT '用户ID',
  role_id BIGINT NOT NULL COMMENT '角色ID',
  PRIMARY KEY (user_id, role_id)
) ENGINE=InnoDB COMMENT='用户角色关联表';

-- 2.5 菜单/权限表
DROP TABLE IF EXISTS sys_menu;
CREATE TABLE sys_menu (
  id        BIGINT       NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
  parent_id BIGINT       DEFAULT 0 COMMENT '父菜单ID',
  name      VARCHAR(50)  NOT NULL COMMENT '菜单名称',
  path      VARCHAR(200) DEFAULT '' COMMENT '路由路径',
  component VARCHAR(200) DEFAULT '' COMMENT '前端组件路径',
  perms     VARCHAR(100) DEFAULT '' COMMENT '权限标识(如 customer:add)',
  icon      VARCHAR(50)  DEFAULT '' COMMENT '图标',
  type      TINYINT      DEFAULT 2 COMMENT '1目录 2菜单 3按钮',
  order_num INT          DEFAULT 0 COMMENT '显示顺序',
  visible   TINYINT      DEFAULT 1 COMMENT '0隐藏 1显示',
  status    TINYINT      DEFAULT 1 COMMENT '0停用 1启用',
  create_by  VARCHAR(50) DEFAULT '' COMMENT '创建人',
  create_time DATETIME   DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_by  VARCHAR(50) DEFAULT '' COMMENT '更新人',
  update_time DATETIME   DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted    TINYINT     DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (id)
) ENGINE=InnoDB COMMENT='菜单权限表';

-- 2.6 角色-菜单关联表
DROP TABLE IF EXISTS sys_role_menu;
CREATE TABLE sys_role_menu (
  role_id BIGINT NOT NULL COMMENT '角色ID',
  menu_id BIGINT NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (role_id, menu_id)
) ENGINE=InnoDB COMMENT='角色菜单关联表';

-- 2.7 操作日志表
DROP TABLE IF EXISTS sys_log;
CREATE TABLE sys_log (
  id         BIGINT       NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  user_id    BIGINT       DEFAULT NULL COMMENT '操作人ID',
  username   VARCHAR(50)  DEFAULT '' COMMENT '操作人账号',
  operation  VARCHAR(200) DEFAULT '' COMMENT '操作描述',
  method     VARCHAR(200) DEFAULT '' COMMENT '方法名',
  request_url VARCHAR(200) DEFAULT '' COMMENT '请求URL',
  request_method VARCHAR(10) DEFAULT '' COMMENT 'HTTP方法',
  params     TEXT         COMMENT '请求参数',
  result     TEXT         COMMENT '返回结果',
  ip         VARCHAR(50) DEFAULT '' COMMENT 'IP地址',
  cost_time  BIGINT       DEFAULT 0 COMMENT '耗时(ms)',
  create_time DATETIME    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (id),
  KEY idx_user_time (user_id, create_time)
) ENGINE=InnoDB COMMENT='操作日志表';

-- ============================================================
-- 3. 初始化数据
-- ============================================================

-- 3.1 部门数据
INSERT INTO sys_dept (id, parent_id, ancestors, dept_name, order_num) VALUES
(1, 0, '0',     '温州CRM科技有限公司', 1),
(2, 1, '0,1',   '管理层', 1),
(3, 1, '0,1',   '销售部', 2),
(4, 1, '0,1',   '市场部', 3),
(5, 1, '0,1',   '售后部', 4),
(6, 1, '0,1',   '产品部', 5);

-- 3.2 角色数据
INSERT INTO sys_role (id, role_code, role_name, data_scope, remark) VALUES
(1, 'ADMIN',   '系统管理员', 4, '拥有全部权限'),
(2, 'MANAGER', '部门经理',   3, '查看本部门及下级数据'),
(3, 'SALE',    '销售',       1, '仅查看自己负责的客户'),
(4, 'MARKET',  '市场人员',   1, '线索录入与渠道管理'),
(5, 'SERVICE', '售后客服',   1, '工单处理'),
(6, 'PRODUCT', '产品部门',   4, '数据看板查看');

-- 3.3 用户数据（密码全部是 123456 的 BCrypt 加密）
-- BCrypt 加密串："123456" → $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy
INSERT INTO sys_user (id, dept_id, username, password, real_name, phone, sex, status) VALUES
(1, 2, 'admin',   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '系统管理员', '13800000001', 1, 1),
(2, 3, 'sale01',  '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '张销售',     '13800000002', 1, 1),
(3, 4, 'market01','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '李市场',     '13800000003', 2, 1),
(4, 5, 'service01','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','王售后',     '13800000004', 2, 1);

-- 3.4 用户-角色关联
INSERT INTO sys_user_role (user_id, role_id) VALUES
(1, 1),
(2, 3),
(3, 4),
(4, 5);

-- 3.5 菜单数据（先建基础菜单结构，对应前端路由）
INSERT INTO sys_menu (id, parent_id, name, path, component, perms, icon, type, order_num) VALUES
-- 一级目录
(1,  0, '工作台',   '/dashboard',  'dashboard/index',     '',                 'Odometer',   2, 1),
(2,  0, '市场获客', '/market',     'Layout',              '',                 'Promotion',  1, 2),
(3,  0, '客户管理', '/customer',   'Layout',              '',                 'User',       1, 3),
(4,  0, '商机销售', '/business',   'Layout',              '',                 'Shopping',   1, 4),
(5,  0, '售后服务', '/service',    'Layout',              '',                 'Tools',      1, 5),
(6,  0, '数据分析', '/report',     'Layout',              '',                 'TrendCharts',1, 6),
(7,  0, '系统管理', '/system',     'Layout',              '',                 'Setting',    1, 7),
-- 市场获客子菜单
(8,  2, '线索池',   '/market/clue',     'market/clue/index',     'market:clue:list',     'Aim',     2, 1),
(9,  2, '渠道管理', '/market/channel',  'market/channel/index',  'market:channel:list', 'Share',   2, 2),
(10, 2, '知识库',   '/market/knowledge','market/knowledge/index','market:knowledge:list','Document',2, 3),
-- 客户管理子菜单
(11, 3, '客户列表', '/customer/list',   'customer/list/index',   'customer:list:list',  'UserFilled',2, 1),
(12, 3, '公海池',   '/customer/pool',   'customer/pool/index',   'customer:pool:list', 'Box',       2, 2),
(13, 3, '标签管理', '/customer/tag',    'customer/tag/index',    'customer:tag:list',  'PriceTag',  2, 3),
-- 商机销售子菜单
(14, 4, '商机管理', '/business/opportunity','business/opportunity/index','business:opportunity:list','Trophy',2,1),
(15, 4, '合同管理', '/business/contract',   'business/contract/index',   'business:contract:list',   'Document',  2,2),
(16, 4, '回款管理', '/business/payment',     'business/payment/index',     'business:payment:list',    'Money',     2,3),
-- 售后服务子菜单
(17, 5, '工单管理', '/service/order',       'service/order/index',       'service:order:list',       'Ticket',   2,1),
(18, 5, '售后记录', '/service/record',      'service/record/index',      'service:record:list',      'Files',    2,2),
-- 数据分析子菜单
(19, 6, '数据看板', '/report/dashboard',    'report/dashboard/index',    'report:dashboard:list',    'DataAnalysis',2,1),
(20, 6, '自定义报表','/report/custom',       'report/custom/index',       'report:custom:list',      'Histogram',  2,2),
-- 系统管理子菜单
(21, 7, '用户管理', '/system/user',          'system/user/index',         'system:user:list',        'UserFilled', 2,1),
(22, 7, '角色管理', '/system/role',          'system/role/index',         'system:role:list',        'Avatar',     2,2),
(23, 7, '菜单管理', '/system/menu',          'system/menu/index',         'system:menu:list',        'Menu',       2,3),
(24, 7, '部门管理', '/system/dept',          'system/dept/index',         'system:dept:list',        'OfficeBuilding',2,4),
(25, 7, '操作日志', '/system/log',           'system/log/index',          'system:log:list',         'List',       2,5);

-- 3.6 角色-菜单关联（管理员拥有全部菜单权限）
INSERT INTO sys_role_menu (role_id, menu_id) VALUES
(1,1),(1,2),(1,3),(1,4),(1,5),(1,6),(1,7),
(1,8),(1,9),(1,10),(1,11),(1,12),(1,13),(1,14),(1,15),(1,16),
(1,17),(1,18),(1,19),(1,20),(1,21),(1,22),(1,23),(1,24),(1,25);

-- ============================================================
-- 4. 验证查询
-- ============================================================
SELECT '部门表' AS table_name, COUNT(*) AS cnt FROM sys_dept
UNION ALL
SELECT '用户表', COUNT(*) FROM sys_user
UNION ALL
SELECT '角色表', COUNT(*) FROM sys_role
UNION ALL
SELECT '菜单表', COUNT(*) FROM sys_menu
UNION ALL
SELECT '用户角色关联', COUNT(*) FROM sys_user_role
UNION ALL
SELECT '角色菜单关联', COUNT(*) FROM sys_role_menu;

-- 期望结果：
-- 部门表          | 6
-- 用户表          | 4
-- 角色表          | 6
-- 菜单表          | 25
-- 用户角色关联    | 4
-- 角色菜单关联    | 25

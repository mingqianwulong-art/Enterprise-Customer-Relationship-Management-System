-- ============================================================
-- CRM 补充"分配角色"按钮权限
-- 说明：用户管理新增"分配角色"功能，对应权限标识 system:user:assign
--      需新增 type=3 按钮菜单并授权给系统管理员（role_id=1）
-- 适配 MySQL 8.0，可重复执行
-- ============================================================

USE crm_db;

-- 1. 清理旧的分配角色按钮菜单（保证可重复执行）
DELETE FROM sys_role_menu WHERE menu_id = 105;
DELETE FROM sys_menu WHERE id = 105;

-- 2. 插入分配角色按钮菜单（parent_id=21 用户管理）
INSERT INTO sys_menu (id, parent_id, name, perms, type, order_num, status) VALUES
(105, 21, '分配角色', 'system:user:assign', 3, 6, 1);

-- 3. 授权给系统管理员（role_id=1）
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 105);

-- 4. 验证
SELECT m.id, m.name, m.perms
FROM sys_menu m
JOIN sys_role_menu rm ON rm.menu_id = m.id
WHERE rm.role_id = 1 AND m.parent_id = 21
ORDER BY m.order_num;

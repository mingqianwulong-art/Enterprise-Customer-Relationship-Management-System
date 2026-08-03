-- 修复被 PowerShell 编码破坏的菜单名称
USE crm_db;
UPDATE sys_menu SET name = '市场获客' WHERE id = 2;
SELECT id, name, HEX(name) AS hex FROM sys_menu WHERE id = 2;

-- ============================================================
-- CRM 权限标识补充脚本（全面版）
-- 说明：init.sql 中 sys_menu 仅定义了各模块的 :list 菜单权限（type=2），
--      缺少 :add / :edit / :delete 等按钮权限标识（type=3）。
--      后端 @PreAuthorize(hasAuthority('xxx')) 依赖这些权限标识，
--      故补充 type=3（按钮）菜单并分配给相应角色。
-- 覆盖范围：系统管理 / 市场获客 / 客户管理 / 商机销售 / 售后服务 / 数据分析
-- 适配 MySQL 8.0，可重复执行。
-- ============================================================

USE crm_db;

-- ------------------------------------------------------------
-- 1. 清理旧的按钮权限菜单（id 100~299 区间），保证可重复执行
-- ------------------------------------------------------------
DELETE FROM sys_role_menu WHERE menu_id BETWEEN 100 AND 299;
DELETE FROM sys_menu WHERE id BETWEEN 100 AND 299;

-- ------------------------------------------------------------
-- 2. 插入按钮权限菜单（type=3）
--    parent_id 对应 init.sql 中的菜单：
--    用户21 角色22 菜单23 部门24 操作日志25
--    线索8 渠道9 知识10
--    客户列表11 公海池12 标签13
--    商机14 合同15 回款16
--    工单17 售后记录18
--    数据看板19 自定义报表20
-- ------------------------------------------------------------
INSERT INTO sys_menu (id, parent_id, name, perms, type, order_num, status) VALUES
-- ========== 系统管理 ==========
-- 用户管理按钮（parent=21）
(100, 21, '用户新增', 'system:user:add',    3, 1, 1),
(101, 21, '用户修改', 'system:user:edit',   3, 2, 1),
(102, 21, '用户删除', 'system:user:delete', 3, 3, 1),
(103, 21, '重置密码', 'system:user:reset',  3, 4, 1),
(104, 21, '用户查询', 'system:user:list',   3, 5, 1),
(105, 21, '分配角色', 'system:user:assign', 3, 6, 1),
-- 角色管理按钮（parent=22）
(110, 22, '角色新增', 'system:role:add',    3, 1, 1),
(111, 22, '角色修改', 'system:role:edit',   3, 2, 1),
(112, 22, '角色删除', 'system:role:delete', 3, 3, 1),
(113, 22, '角色查询', 'system:role:list',   3, 4, 1),
-- 菜单管理按钮（parent=23）
(120, 23, '菜单新增', 'system:menu:add',    3, 1, 1),
(121, 23, '菜单修改', 'system:menu:edit',   3, 2, 1),
(122, 23, '菜单删除', 'system:menu:delete', 3, 3, 1),
(123, 23, '菜单查询', 'system:menu:list',   3, 4, 1),
-- 部门管理按钮（parent=24）
(130, 24, '部门新增', 'system:dept:add',    3, 1, 1),
(131, 24, '部门修改', 'system:dept:edit',   3, 2, 1),
(132, 24, '部门删除', 'system:dept:delete', 3, 3, 1),
(133, 24, '部门查询', 'system:dept:list',   3, 4, 1),
-- 操作日志按钮（parent=25）
(134, 25, '日志查询', 'system:log:list',    3, 1, 1),

-- ========== 市场获客 ==========
-- 线索管理按钮（parent=8）
-- 注意：id=149 已用于 customer:follow:add，此处 market:clue:list 改用 id=157 避免主键冲突
(157, 8, '线索查询', 'market:clue:list',          3, 0, 1),
(150, 8, '线索新增', 'market:clue:add',           3, 1, 1),
(151, 8, '线索修改', 'market:clue:edit',          3, 2, 1),
(152, 8, '线索删除', 'market:clue:delete',         3, 3, 1),
(153, 8, '线索分配', 'market:clue:assign',         3, 4, 1),
(154, 8, '线索抢单', 'market:clue:claim',          3, 5, 1),
(155, 8, '线索转化', 'market:clue:convert',        3, 6, 1),
(156, 8, '自动分配', 'market:clue:auto-assign',    3, 7, 1),
-- 渠道管理按钮（parent=9）
(160, 9, '渠道新增', 'market:channel:add',    3, 1, 1),
(161, 9, '渠道修改', 'market:channel:edit',   3, 2, 1),
(162, 9, '渠道删除', 'market:channel:delete', 3, 3, 1),
(163, 9, '渠道查询', 'market:channel:list',   3, 4, 1),
-- 知识库按钮（parent=10）
(170, 10, '知识新增', 'market:knowledge:add',    3, 1, 1),
(171, 10, '知识修改', 'market:knowledge:edit',   3, 2, 1),
(172, 10, '知识删除', 'market:knowledge:delete', 3, 3, 1),
(173, 10, '知识查询', 'market:knowledge:list',   3, 4, 1),

-- ========== 客户管理 ==========
-- 客户管理按钮（parent=11）
(140, 11, '客户新增', 'customer:customer:add',    3, 1, 1),
(141, 11, '客户修改', 'customer:customer:edit',   3, 2, 1),
(142, 11, '客户删除', 'customer:customer:delete', 3, 3, 1),
(143, 11, '客户查询', 'customer:customer:list',   3, 4, 1),
-- 联系人按钮（parent=11）
(144, 11, '联系人查询', 'customer:contact:list',   3, 5, 1),
(145, 11, '联系人新增', 'customer:contact:add',    3, 6, 1),
(146, 11, '联系人修改', 'customer:contact:edit',   3, 7, 1),
(147, 11, '联系人删除', 'customer:contact:delete', 3, 8, 1),
-- 跟进记录按钮（parent=11）
(148, 11, '跟进查询', 'customer:follow:list', 3, 9, 1),
(149, 11, '跟进新增', 'customer:follow:add',  3, 10, 1),
-- 标签管理按钮（parent=13）
(180, 13, '标签新增', 'customer:tag:add',    3, 1, 1),
(181, 13, '标签修改', 'customer:tag:edit',   3, 2, 1),
(182, 13, '标签删除', 'customer:tag:delete', 3, 3, 1),
(183, 13, '标签查询', 'customer:tag:list',   3, 4, 1),

-- ========== 商机销售 ==========
-- 商机管理按钮（parent=14）
(200, 14, '商机新增', 'business:opportunity:add',     3, 1, 1),
(201, 14, '商机修改', 'business:opportunity:edit',    3, 2, 1),
(202, 14, '商机删除', 'business:opportunity:delete',  3, 3, 1),
(203, 14, '阶段推进', 'business:opportunity:stage',    3, 4, 1),
(204, 14, '商机查询', 'business:opportunity:list',    3, 5, 1),
-- 合同管理按钮（parent=15）
(210, 15, '合同新增', 'business:contract:add',     3, 1, 1),
(211, 15, '合同修改', 'business:contract:edit',    3, 2, 1),
(212, 15, '合同删除', 'business:contract:delete',  3, 3, 1),
(213, 15, '合同审批', 'business:contract:approve',  3, 4, 1),
(214, 15, '合同查询', 'business:contract:list',    3, 5, 1),
-- 回款管理按钮（parent=16）
(220, 16, '回款新增', 'business:payment:add',     3, 1, 1),
(221, 16, '回款修改', 'business:payment:edit',    3, 2, 1),
(222, 16, '回款删除', 'business:payment:delete',  3, 3, 1),
(223, 16, '回款确认', 'business:payment:confirm', 3, 4, 1),
(224, 16, '回款查询', 'business:payment:list',    3, 5, 1),
-- 签到管理按钮（parent=16，外勤签到挂在回款同级菜单下，若无独立菜单则挂16）
(230, 16, '签到打卡', 'business:sign-in:add',  3, 6, 1),
(231, 16, '签到查询', 'business:sign-in:list', 3, 7, 1),

-- ========== 售后服务 ==========
-- 工单管理按钮（parent=17）
(240, 17, '工单新增', 'service:order:add',          3, 1, 1),
(241, 17, '工单修改', 'service:order:edit',         3, 2, 1),
(242, 17, '工单删除', 'service:order:delete',       3, 3, 1),
(243, 17, '工单分配', 'service:order:assign',       3, 4, 1),
(244, 17, '状态更新', 'service:order:status',       3, 5, 1),
(245, 17, '满意度评价', 'service:order:satisfaction', 3, 6, 1),
(246, 17, '高频问题', 'service:order:hot-problems',  3, 7, 1),
(247, 17, '工单查询', 'service:order:list',         3, 8, 1),
-- 售后记录按钮（parent=18）
(250, 18, '记录新增', 'service:record:add',    3, 1, 1),
(251, 18, '记录修改', 'service:record:edit',   3, 2, 1),
(252, 18, '记录删除', 'service:record:delete', 3, 3, 1),
(253, 18, '记录查询', 'service:record:list',   3, 4, 1),

-- ========== 数据分析 ==========
-- 数据看板按钮（parent=19）
(260, 19, '看板查询', 'report:dashboard:list', 3, 1, 1),
-- 自定义报表按钮（parent=20）
(261, 20, '报表查询', 'report:custom:list', 3, 1, 1),
-- 预测分析按钮（parent=20，预测分析无独立菜单，挂20）
(262, 20, '预测分析', 'report:forecast:list', 3, 2, 1);

-- ------------------------------------------------------------
-- 3. 角色-菜单授权
-- ------------------------------------------------------------
-- 3.1 系统管理员（role_id=1）：拥有全部按钮权限
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 1, id FROM sys_menu WHERE id BETWEEN 100 AND 299;

-- 3.2 部门经理（role_id=2，data_scope=3）：全部业务模块按钮权限
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 2, id FROM sys_menu
WHERE id BETWEEN 140 AND 262
  AND id NOT IN (100,101,102,103,104,105,110,111,112,113,120,121,122,123,130,131,132,133);

-- 3.3 销售（role_id=3，data_scope=1）：客户+商机+合同+回款+签到+工单查询
INSERT INTO sys_role_menu (role_id, menu_id) VALUES
-- 客户管理
(3, 140),(3, 141),(3, 142),(3, 143),
(3, 144),(3, 145),(3, 146),(3, 147),
(3, 148),(3, 149),
(3, 180),(3, 181),(3, 182),(3, 183),
-- 线索（查询/录入/抢单/转化）
(3, 157),(3, 150),(3, 151),(3, 154),(3, 155),
-- 商机销售
(3, 200),(3, 201),(3, 202),(3, 203),(3, 204),
(3, 210),(3, 211),(3, 214),
(3, 220),(3, 221),(3, 224),
(3, 230),(3, 231),
-- 售后工单（仅查询）
(3, 247);

-- ------------------------------------------------------------
-- 4. 验证查询
-- ------------------------------------------------------------
-- 管理员拥有的权限标识数量
SELECT COUNT(*) AS admin_perm_count
FROM sys_role_menu rm
JOIN sys_menu m ON m.id = rm.menu_id
WHERE rm.role_id = 1 AND m.perms IS NOT NULL AND m.perms <> '';

-- 各角色权限标识列表
SELECT rm.role_id, m.perms
FROM sys_role_menu rm
JOIN sys_menu m ON m.id = rm.menu_id
WHERE m.perms IS NOT NULL AND m.perms <> ''
ORDER BY rm.role_id, m.perms;

-- ============================================================
-- 注意：
-- 1. 修改权限后，已登录用户的权限缓存在 Redis（LoginUser JSON）中，
--    需要让相关用户重新登录以刷新权限。
-- 2. report:forecast:list 按钮挂在"自定义报表"菜单下（parent_id=20），
--    如需独立菜单请在 init.sql 中补充。
-- 3. business:sign-in 按钮挂在"回款管理"菜单下（parent_id=16），
--    如需独立"外勤签到"菜单请在 init.sql 中补充。
-- ============================================================

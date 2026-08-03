package com.crm.common.constant;

/**
 * 权限标识常量
 *
 * @author CRM
 */
public class Perms {
    private Perms() {
    }

    // 用户管理
    public static final String USER_LIST = "system:user:list";
    public static final String USER_ADD = "system:user:add";
    public static final String USER_EDIT = "system:user:edit";
    public static final String USER_DELETE = "system:user:delete";
    public static final String USER_RESET = "system:user:reset";
    public static final String USER_ASSIGN = "system:user:assign";

    // 角色管理
    public static final String ROLE_LIST = "system:role:list";
    public static final String ROLE_ADD = "system:role:add";
    public static final String ROLE_EDIT = "system:role:edit";
    public static final String ROLE_DELETE = "system:role:delete";

    // 菜单管理
    public static final String MENU_LIST = "system:menu:list";
    public static final String MENU_ADD = "system:menu:add";
    public static final String MENU_EDIT = "system:menu:edit";
    public static final String MENU_DELETE = "system:menu:delete";

    // 部门管理
    public static final String DEPT_LIST = "system:dept:list";
    public static final String DEPT_ADD = "system:dept:add";
    public static final String DEPT_EDIT = "system:dept:edit";
    public static final String DEPT_DELETE = "system:dept:delete";

    // 客户管理
    public static final String CUSTOMER_LIST = "customer:customer:list";
    public static final String CUSTOMER_ADD = "customer:customer:add";
    public static final String CUSTOMER_EDIT = "customer:customer:edit";
    public static final String CUSTOMER_DELETE = "customer:customer:delete";
}

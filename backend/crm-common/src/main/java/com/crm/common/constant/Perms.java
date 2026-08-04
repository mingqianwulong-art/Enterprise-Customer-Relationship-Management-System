package com.crm.common.constant;

/**
 * 权限标识常量
 * <p>
 * 值与数据库 sys_menu.perms 字段保持一致，用于 @PreAuthorize 注解。
 *
 * @author CRM
 */
public class Perms {
    private Perms() {
    }

    // ==================== 系统管理 ====================
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

    // 操作日志
    public static final String SYS_LOG_LIST = "system:log:list";

    // ==================== 市场获客 ====================
    // 线索管理
    public static final String CLUE_LIST = "market:clue:list";
    public static final String CLUE_ADD = "market:clue:add";
    public static final String CLUE_EDIT = "market:clue:edit";
    public static final String CLUE_DELETE = "market:clue:delete";
    public static final String CLUE_ASSIGN = "market:clue:assign";
    public static final String CLUE_CLAIM = "market:clue:claim";
    public static final String CLUE_CONVERT = "market:clue:convert";
    public static final String CLUE_AUTO_ASSIGN = "market:clue:auto-assign";

    // 渠道管理
    public static final String CHANNEL_LIST = "market:channel:list";
    public static final String CHANNEL_ADD = "market:channel:add";
    public static final String CHANNEL_EDIT = "market:channel:edit";
    public static final String CHANNEL_DELETE = "market:channel:delete";

    // 知识库
    public static final String KNOWLEDGE_LIST = "market:knowledge:list";
    public static final String KNOWLEDGE_ADD = "market:knowledge:add";
    public static final String KNOWLEDGE_EDIT = "market:knowledge:edit";
    public static final String KNOWLEDGE_DELETE = "market:knowledge:delete";

    // ==================== 客户管理 ====================
    // 客户管理
    public static final String CUSTOMER_LIST = "customer:customer:list";
    public static final String CUSTOMER_ADD = "customer:customer:add";
    public static final String CUSTOMER_EDIT = "customer:customer:edit";
    public static final String CUSTOMER_DELETE = "customer:customer:delete";

    // 联系人管理
    public static final String CUSTOMER_CONTACT_LIST = "customer:contact:list";
    public static final String CUSTOMER_CONTACT_ADD = "customer:contact:add";
    public static final String CUSTOMER_CONTACT_EDIT = "customer:contact:edit";
    public static final String CUSTOMER_CONTACT_DELETE = "customer:contact:delete";

    // 跟进记录
    public static final String CUSTOMER_FOLLOW_LIST = "customer:follow:list";
    public static final String CUSTOMER_FOLLOW_ADD = "customer:follow:add";

    // 标签管理
    public static final String CUSTOMER_TAG_LIST = "customer:tag:list";
    public static final String CUSTOMER_TAG_ADD = "customer:tag:add";
    public static final String CUSTOMER_TAG_EDIT = "customer:tag:edit";
    public static final String CUSTOMER_TAG_DELETE = "customer:tag:delete";

    // ==================== 商机销售 ====================
    // 商机管理
    public static final String OPPORTUNITY_LIST = "business:opportunity:list";
    public static final String OPPORTUNITY_ADD = "business:opportunity:add";
    public static final String OPPORTUNITY_EDIT = "business:opportunity:edit";
    public static final String OPPORTUNITY_DELETE = "business:opportunity:delete";
    public static final String OPPORTUNITY_STAGE = "business:opportunity:stage";

    // 合同管理
    public static final String CONTRACT_LIST = "business:contract:list";
    public static final String CONTRACT_ADD = "business:contract:add";
    public static final String CONTRACT_EDIT = "business:contract:edit";
    public static final String CONTRACT_DELETE = "business:contract:delete";
    public static final String CONTRACT_APPROVE = "business:contract:approve";

    // 回款管理
    public static final String PAYMENT_LIST = "business:payment:list";
    public static final String PAYMENT_ADD = "business:payment:add";
    public static final String PAYMENT_EDIT = "business:payment:edit";
    public static final String PAYMENT_DELETE = "business:payment:delete";
    public static final String PAYMENT_CONFIRM = "business:payment:confirm";

    // 签到管理
    public static final String SIGN_IN_ADD = "business:sign-in:add";
    public static final String SIGN_IN_LIST = "business:sign-in:list";

    // ==================== 售后服务 ====================
    // 工单管理
    public static final String SERVICE_ORDER_LIST = "service:order:list";
    public static final String SERVICE_ORDER_ADD = "service:order:add";
    public static final String SERVICE_ORDER_EDIT = "service:order:edit";
    public static final String SERVICE_ORDER_DELETE = "service:order:delete";
    public static final String SERVICE_ORDER_ASSIGN = "service:order:assign";
    public static final String SERVICE_ORDER_STATUS = "service:order:status";
    public static final String SERVICE_ORDER_SATISFACTION = "service:order:satisfaction";
    public static final String SERVICE_ORDER_HOT_PROBLEMS = "service:order:hot-problems";

    // 售后记录
    public static final String SERVICE_RECORD_LIST = "service:record:list";
    public static final String SERVICE_RECORD_ADD = "service:record:add";
    public static final String SERVICE_RECORD_EDIT = "service:record:edit";
    public static final String SERVICE_RECORD_DELETE = "service:record:delete";

    // ==================== 数据分析 ====================
    public static final String REPORT_DASHBOARD_LIST = "report:dashboard:list";
    public static final String REPORT_CUSTOM_LIST = "report:custom:list";
    public static final String REPORT_FORECAST_LIST = "report:forecast:list";
}

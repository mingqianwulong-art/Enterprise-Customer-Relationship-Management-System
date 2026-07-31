package com.crm.system.service;

import com.crm.common.security.LoginUser;
import com.crm.common.security.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * 数据权限服务（下沉至 crm-system 模块，供各业务模块复用）
 * <p>
 * 根据当前登录用户的 dataScope 返回可见的负责人ID集合（owner_id / assignee_id / handler_id 等）
 * - dataScope=1 本人：返回 [当前用户ID]
 * - dataScope=2 本部门：返回本部门所有用户ID
 * - dataScope=3 本部门及下：返回本部门及子部门所有用户ID
 * - dataScope=4 全部：返回 null（表示不过滤）
 *
 * @author CRM
 */
@Service
public class DataPermissionService {

    @Autowired
    private ISysUserService userService;

    @Autowired
    private ISysDeptService deptService;

    /**
     * 返回当前用户可见的负责人ID集合
     *
     * @return 负责人ID集合；返回 null 表示不过滤（全部数据权限）
     */
    public List<Long> getVisibleOwnerIds() {
        LoginUser user = SecurityUtils.getCurrentUser();
        if (user == null) {
            // 未登录返回不可能匹配的ID
            return Collections.singletonList(-1L);
        }
        Integer dataScope = user.getDataScope();
        if (dataScope == null || dataScope == 4) {
            // 全部数据
            return null;
        }
        if (dataScope == 1) {
            // 本人
            return Collections.singletonList(user.getUserId());
        }
        Long deptId = user.getDeptId();
        if (deptId == null) {
            // 无部门归属时仅能看本人
            return Collections.singletonList(user.getUserId());
        }
        List<Long> deptIds;
        if (dataScope == 2) {
            // 本部门
            deptIds = Collections.singletonList(deptId);
        } else {
            // dataScope == 3 本部门及子部门
            deptIds = deptService.getChildDeptIds(deptId);
        }
        List<Long> userIds = userService.getUserIdsByDeptIds(deptIds);
        if (userIds == null || userIds.isEmpty()) {
            return Collections.singletonList(-1L);
        }
        return userIds;
    }
}

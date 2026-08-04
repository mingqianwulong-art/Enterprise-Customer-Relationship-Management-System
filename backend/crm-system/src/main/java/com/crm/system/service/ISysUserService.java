package com.crm.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.crm.system.dto.ForgotPasswordDTO;
import com.crm.system.dto.RegisterDTO;
import com.crm.system.dto.UserPageDTO;
import com.crm.system.entity.SysUser;
import com.crm.system.vo.LoginUserVO;
import com.crm.system.vo.LoginVO;

/**
 * 用户服务接口
 *
 * @author CRM
 */
public interface ISysUserService extends IService<SysUser> {

    /**
     * 登录
     *
     * @param username 用户名
     * @param password 密码
     * @return 登录返回信息
     */
    LoginVO login(String username, String password);

    /**
     * 用户注册
     *
     * @param dto 注册信息
     */
    void register(RegisterDTO dto);

    /**
     * 忘记密码（通过手机号验证码重置）
     *
     * @param dto 手机号、验证码、新密码
     */
    void forgotPassword(ForgotPasswordDTO dto);

    /**
     * 获取登录用户信息（角色、权限、菜单树）
     *
     * @param userId 用户ID
     * @return 登录用户信息
     */
    LoginUserVO getInfo(Long userId);

    /**
     * 分页查询用户
     *
     * @param dto 查询条件
     * @return 分页结果
     */
    com.baomidou.mybatisplus.core.metadata.IPage<SysUser> page(UserPageDTO dto);

    /**
     * 新增用户
     *
     * @param user 用户信息
     * @return 是否成功
     */
    boolean addUser(SysUser user);

    /**
     * 修改用户
     *
     * @param user 用户信息
     * @return 是否成功
     */
    boolean updateUser(SysUser user);

    /**
     * 删除用户（逻辑删除）
     *
     * @param id 用户ID
     * @return 是否成功
     */
    boolean deleteUser(Long id);

    /**
     * 重置密码
     *
     * @param userId   用户ID
     * @param password 新密码（明文，内部加密）
     * @return 是否成功
     */
    boolean resetPassword(Long userId, String password);

    /**
     * 修改当前用户密码（验证旧密码）
     *
     * @param userId      用户ID
     * @param oldPassword 旧密码（明文）
     * @param newPassword 新密码（明文，内部加密）
     * @return 是否成功
     */
    boolean changePassword(Long userId, String oldPassword, String newPassword);

    /**
     * 修改用户状态
     *
     * @param userId 用户ID
     * @param status 状态
     * @return 是否成功
     */
    boolean changeStatus(Long userId, Integer status);

    /**
     * 查询部门ID集合下的所有用户ID
     *
     * @param deptIds 部门ID集合
     * @return 用户ID列表
     */
    java.util.List<Long> getUserIdsByDeptIds(java.util.List<Long> deptIds);

    /**
     * 查询用户已分配的角色ID列表
     *
     * @param userId 用户ID
     * @return 角色ID列表
     */
    java.util.List<Long> getUserRoleIds(Long userId);

    /**
     * 分配角色（先删后增，事务）
     *
     * @param userId  用户ID
     * @param roleIds 角色ID列表
     * @return 是否成功
     */
    boolean assignRoles(Long userId, java.util.List<Long> roleIds);
}

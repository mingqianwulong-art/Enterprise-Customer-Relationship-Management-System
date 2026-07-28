package com.crm.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
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
     * 修改用户状态
     *
     * @param userId 用户ID
     * @param status 状态
     * @return 是否成功
     */
    boolean changeStatus(Long userId, Integer status);
}

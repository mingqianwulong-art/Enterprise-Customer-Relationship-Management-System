package com.crm.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crm.common.constant.Constants;
import com.crm.common.exception.BusinessException;
import com.crm.common.utils.JwtUtils;
import com.crm.system.dto.UserPageDTO;
import com.crm.system.entity.SysUser;
import com.crm.system.entity.SysUserRole;
import com.crm.system.mapper.SysUserMapper;
import com.crm.system.mapper.SysUserRoleMapper;
import com.crm.system.service.ISysMenuService;
import com.crm.system.service.ISysRoleService;
import com.crm.system.service.ISysUserService;
import com.crm.system.vo.LoginUserVO;
import com.crm.system.vo.LoginVO;
import com.crm.system.vo.MenuTreeVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * 用户服务实现
 *
 * @author CRM
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements ISysUserService {

    @Autowired
    private SysUserRoleMapper userRoleMapper;

    @Autowired
    private ISysRoleService roleService;

    @Autowired
    private ISysMenuService menuService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private JwtUtils jwtUtils;

    /**
     * 登录
     */
    @Override
    public LoginVO login(String username, String password) {
        // 根据用户名查询用户
        SysUser user = baseMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, username));
        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }
        // BCrypt 校验密码
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        // 校验状态（0=停用 1=启用）
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new BusinessException("账号已停用，请联系管理员");
        }
        // 生成 JWT
        String token = jwtUtils.generateToken(user.getId(), user.getUsername());
        // 存入 Redis（key 为 LOGIN_TOKEN_KEY + userId，value 为 userId）
        redisTemplate.opsForValue().set(Constants.LOGIN_TOKEN_KEY + user.getId(),
                String.valueOf(user.getId()));
        // 更新最后登录时间
        SysUser update = new SysUser();
        update.setId(user.getId());
        update.setLastLoginTime(LocalDateTime.now());
        baseMapper.updateById(update);
        // 构建返回
        LoginVO vo = new LoginVO();
        vo.setToken(token);
        vo.setUserId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setRealName(user.getRealName());
        return vo;
    }

    /**
     * 获取登录用户信息
     */
    @Override
    public LoginUserVO getInfo(Long userId) {
        SysUser user = baseMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        // 清除密码
        user.setPassword(null);
        // 角色编码集合
        Set<String> roles = roleService.getRoleCodesByUserId(userId);
        // 权限标识集合
        Set<String> permissions = menuService.getPermissionsByUserId(userId);
        // 菜单树
        List<MenuTreeVO> menus = menuService.getMenuTreeByUserId(userId);
        // 组装返回
        LoginUserVO vo = new LoginUserVO();
        vo.setUser(user);
        vo.setRoles(roles);
        vo.setPermissions(permissions);
        vo.setMenus(menus);
        return vo;
    }

    /**
     * 分页查询用户
     */
    @Override
    public IPage<SysUser> page(UserPageDTO dto) {
        Page<SysUser> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<SysUser>()
                .like(dto.getUsername() != null && !dto.getUsername().isEmpty(),
                        SysUser::getUsername, dto.getUsername())
                .like(dto.getPhone() != null && !dto.getPhone().isEmpty(),
                        SysUser::getPhone, dto.getPhone())
                .eq(dto.getStatus() != null,
                        SysUser::getStatus, dto.getStatus())
                .orderByDesc(SysUser::getCreateTime);
        IPage<SysUser> result = baseMapper.selectPage(page, wrapper);
        // 清除密码
        result.getRecords().forEach(u -> u.setPassword(null));
        return result;
    }

    /**
     * 新增用户
     */
    @Override
    public boolean addUser(SysUser user) {
        // 校验用户名唯一
        Long count = baseMapper.selectCount(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, user.getUsername()));
        if (count > 0) {
            throw new BusinessException("用户名已存在");
        }
        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // 默认启用状态
        if (user.getStatus() == null) {
            user.setStatus(1);
        }
        return baseMapper.insert(user) > 0;
    }

    /**
     * 修改用户
     */
    @Override
    public boolean updateUser(SysUser user) {
        // 不允许通过此接口修改密码（密码置空，MyBatis-Plus 默认不更新空字段）
        user.setPassword(null);
        return baseMapper.updateById(user) > 0;
    }

    /**
     * 删除用户（逻辑删除）
     */
    @Override
    public boolean deleteUser(Long id) {
        // 删除用户角色关联
        userRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>()
                .eq(SysUserRole::getUserId, id));
        return baseMapper.deleteById(id) > 0;
    }

    /**
     * 重置密码
     */
    @Override
    public boolean resetPassword(Long userId, String password) {
        SysUser user = new SysUser();
        user.setId(userId);
        user.setPassword(passwordEncoder.encode(password));
        return baseMapper.updateById(user) > 0;
    }

    /**
     * 修改用户状态
     */
    @Override
    public boolean changeStatus(Long userId, Integer status) {
        SysUser user = new SysUser();
        user.setId(userId);
        user.setStatus(status);
        return baseMapper.updateById(user) > 0;
    }
}

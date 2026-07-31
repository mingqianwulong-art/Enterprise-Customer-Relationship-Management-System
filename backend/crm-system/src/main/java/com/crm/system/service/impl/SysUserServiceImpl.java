package com.crm.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crm.common.constant.Constants;
import com.crm.common.exception.BusinessException;
import com.crm.common.security.LoginUser;
import com.crm.common.security.SecurityUtils;
import com.crm.common.utils.JwtUtils;
import com.crm.system.dto.ForgotPasswordDTO;
import com.crm.system.dto.RegisterDTO;
import com.crm.system.dto.UserPageDTO;
import com.crm.system.entity.SysRole;
import com.crm.system.entity.SysUser;
import com.crm.system.entity.SysUserRole;
import com.crm.system.mapper.SysUserMapper;
import com.crm.system.mapper.SysUserRoleMapper;
import com.crm.system.service.ISysMenuService;
import com.crm.system.service.ISysRoleService;
import com.crm.system.service.ISysUserService;
import com.crm.system.service.SmsService;
import com.crm.system.vo.LoginUserVO;
import com.crm.system.vo.LoginVO;
import com.crm.system.vo.MenuTreeVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
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

    @Autowired
    private SmsService smsService;

    private final ObjectMapper objectMapper = new ObjectMapper();

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
        // 构建登录用户信息（含部门、数据范围、角色、权限），序列化为 JSON 存入 Redis
        LoginUser loginUser = buildLoginUser(user);
        try {
            redisTemplate.opsForValue().set(Constants.LOGIN_TOKEN_KEY + user.getId(),
                    objectMapper.writeValueAsString(loginUser));
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            throw new BusinessException("登录信息序列化失败");
        }
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
     * 用户注册
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(RegisterDTO dto) {
        // 校验短信验证码
        if (!smsService.verifyCode(dto.getPhone(), dto.getCode())) {
            throw new BusinessException("验证码错误或已失效");
        }
        // 校验用户名唯一
        Long usernameCount = baseMapper.selectCount(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, dto.getUsername()));
        if (usernameCount > 0) {
            throw new BusinessException("用户名已存在");
        }
        // 校验手机号唯一
        Long phoneCount = baseMapper.selectCount(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getPhone, dto.getPhone()));
        if (phoneCount > 0) {
            throw new BusinessException("该手机号已被注册");
        }
        // 校验邮箱唯一（邮箱非空时才校验）
        if (dto.getEmail() != null && !dto.getEmail().isEmpty()) {
            Long emailCount = baseMapper.selectCount(new LambdaQueryWrapper<SysUser>()
                    .eq(SysUser::getEmail, dto.getEmail()));
            if (emailCount > 0) {
                throw new BusinessException("该邮箱已被注册");
            }
        }
        // 构建用户并保存
        SysUser user = new SysUser();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setPhone(dto.getPhone());
        user.setEmail(dto.getEmail());
        user.setRealName(dto.getUsername());
        user.setStatus(1);
        baseMapper.insert(user);
    }

    /**
     * 忘记密码（通过手机号验证码重置）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void forgotPassword(ForgotPasswordDTO dto) {
        // 1. 校验短信验证码
        if (!smsService.verifyCode(dto.getPhone(), dto.getCode())) {
            throw new BusinessException("验证码错误或已失效");
        }
        // 2. 查询用户（手机号唯一）
        SysUser user = baseMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getPhone, dto.getPhone()));
        if (user == null) {
            throw new BusinessException("该手机号尚未注册");
        }
        // 3. 校验账号状态
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new BusinessException("账号已停用，请联系管理员");
        }
        // 4. 加密并更新密码
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        baseMapper.updateById(user);
    }

    /**
     * 构建登录用户信息（含部门、数据范围、角色、权限）
     *
     * @param user 已通过校验的 SysUser
     * @return LoginUser
     */
    private LoginUser buildLoginUser(SysUser user) {
        // 角色列表（用于计算数据范围与角色编码）
        List<SysRole> roles = roleService.getRolesByUserId(user.getId());
        // 计算最大数据范围（取角色中 dataScope 最大值，无角色默认 1 本人）
        Integer dataScope = 1;
        if (roles != null && !roles.isEmpty()) {
            dataScope = roles.stream()
                    .map(SysRole::getDataScope)
                    .filter(Objects::nonNull)
                    .max(Integer::compareTo)
                    .orElse(1);
        }
        // 角色编码集合
        Set<String> roleCodes = roleService.getRoleCodesByUserId(user.getId());
        // 权限标识集合
        Set<String> permissions = menuService.getPermissionsByUserId(user.getId());

        LoginUser loginUser = new LoginUser();
        loginUser.setUserId(user.getId());
        loginUser.setUsername(user.getUsername());
        loginUser.setDeptId(user.getDeptId());
        loginUser.setDataScope(dataScope);
        loginUser.setRoles(roleCodes);
        loginUser.setPermissions(permissions);
        return loginUser;
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
        // 从 SecurityContext 获取登录时已计算好的数据范围
        LoginUser loginUser = SecurityUtils.getCurrentUser();
        Integer dataScope = loginUser != null ? loginUser.getDataScope() : null;
        // 组装返回
        LoginUserVO vo = new LoginUserVO();
        vo.setUser(user);
        vo.setRoles(roles);
        vo.setPermissions(permissions);
        vo.setMenus(menus);
        vo.setDataScope(dataScope);
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

    /**
     * 查询部门ID集合下的所有用户ID
     */
    @Override
    public List<Long> getUserIdsByDeptIds(List<Long> deptIds) {
        if (deptIds == null || deptIds.isEmpty()) {
            return Collections.emptyList();
        }
        List<SysUser> users = baseMapper.selectList(new LambdaQueryWrapper<SysUser>()
                .select(SysUser::getId)
                .in(SysUser::getDeptId, deptIds));
        return users.stream().map(SysUser::getId).collect(java.util.stream.Collectors.toList());
    }
}

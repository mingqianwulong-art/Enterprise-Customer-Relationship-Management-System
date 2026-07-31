package com.crm.common.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;

/**
 * 登录用户 Principal 封装
 * <p>
 * 作为 SecurityContext 中 Authentication 的 principal，
 * 携带 userId、username、deptId、dataScope、roles 等信息供后续业务使用
 *
 * @author CRM
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoginUser implements UserDetails {

    private static final long serialVersionUID = 1L;

    /** 用户ID */
    private Long userId;

    /** 用户名 */
    private String username;

    /** 部门ID */
    private Long deptId;

    /** 数据范围（1本人 2本部门 3本部门及下 4全部） */
    private Integer dataScope;

    /** 角色编码集合 */
    private Set<String> roles;

    /** 权限标识集合（如 system:user:add） */
    private Set<String> permissions;

    /** 权限集合（运行期由 permissions 构造，不参与序列化） */
    @JsonIgnore
    private transient Collection<? extends GrantedAuthority> authorities;

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

package com.crm.system.config;

import com.crm.common.constant.Constants;
import com.crm.common.security.LoginUser;
import com.crm.common.utils.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * JWT 认证过滤器
 * <p>
 * 1. 从请求头获取 token
 * 2. 解析 token 获取 userId
 * 3. 从 Redis 查询登录状态
 * 4. 写入 SecurityContext
 * 5. 失败返回 401 JSON
 *
 * @author CRM
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private JwtUtils jwtUtils;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            // 从请求头获取 token
            String token = getTokenFromRequest(request);
            if (StringUtils.hasText(token)) {
                // 解析 token 获取 userId
                Long userId = jwtUtils.getUserIdFromToken(token);
                if (userId != null) {
                    // 从 Redis 查询登录状态（value 为 LoginUser 的 JSON）
                    String redisKey = Constants.LOGIN_TOKEN_KEY + userId;
                    String redisValue = redisTemplate.opsForValue().get(redisKey);
                    if (StringUtils.hasText(redisValue)) {
                        // 反序列化为 LoginUser 作为 Principal
                        LoginUser loginUser = objectMapper.readValue(redisValue, LoginUser.class);
                        if (loginUser.getUserId() == null) {
                            loginUser.setUserId(userId);
                        }
                        // 由权限标识构造 authorities，供 @PreAuthorize(hasAuthority) 校验
                        List<SimpleGrantedAuthority> authorities = loginUser.getPermissions() == null
                                ? Collections.emptyList()
                                : loginUser.getPermissions().stream()
                                        .map(SimpleGrantedAuthority::new)
                                        .collect(Collectors.toList());
                        // 写入 SecurityContext
                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(loginUser, null, authorities);
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            }
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            // 认证失败返回 401 JSON
            e.printStackTrace();
            returnUnauthorized(response, "认证失败，请重新登录: " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

    /**
     * 从请求头获取 token
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String header = request.getHeader(Constants.TOKEN_HEADER);
        if (StringUtils.hasText(header) && header.startsWith(Constants.TOKEN_PREFIX)) {
            // 去掉 Bearer 前缀
            return header.substring(Constants.TOKEN_PREFIX.length());
        }
        return null;
    }

    /**
     * 返回 401 未授权 JSON
     */
    private void returnUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        Map<String, Object> result = new HashMap<>();
        result.put("code", 401);
        result.put("msg", message);
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}

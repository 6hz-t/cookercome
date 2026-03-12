package com.hs.backend.security;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hs.backend.entity.User;
import com.hs.backend.service.UserService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * 自定义 UserDetailsService
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserService userService;

    public CustomUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        try {
            Long id = Long.parseLong(userId);
            User user = userService.getById(id);
            
            if (user == null) {
                throw new UsernameNotFoundException("用户不存在：" + userId);
            }
            
            // 根据角色分配权限（简化版，实际应该从数据库查询）
            String role = user.getRole() == 1 ? "ROLE_CHEF" : (user.getRole() == 2 ? "ROLE_ADMIN" : "ROLE_USER");
            
            return new org.springframework.security.core.userdetails.User(
                    user.getId().toString(),
                    user.getPassword(),
                    Collections.singletonList(new SimpleGrantedAuthority(role))
            );
        } catch (NumberFormatException e) {
            throw new UsernameNotFoundException("无效的用户 ID: " + userId);
        }
    }
}

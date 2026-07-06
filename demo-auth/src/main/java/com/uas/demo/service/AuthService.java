package com.uas.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.uas.demo.entity.SysUser;
import com.uas.demo.mapper.SysUserMapper;
import com.uas.demo.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 认证服务 — 登录、获取用户信息、修改密码、刷新权限。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final SysUserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 登录：根据 identifier（dlzh 或 sjh）+ password 验证，返回 token + 用户信息。
     */
    public Map<String, Object> login(String identifier, String password) {
        // 按 dlzh 或 sjh 查找
        SysUser user = userMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getDlzh, identifier)
                .or()
                .eq(SysUser::getSjh, identifier));
        if (user == null) {
            throw new AuthException("账户不存在");
        }
        if (user.getZhzt() == 0) {
            throw new AuthException("账户已停用");
        }
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new AuthException("密码错误");
        }

        String token = jwtTokenProvider.generateToken(
                user.getZhdlxxbs(), user.getDlzh(), user.getJs());

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("token", token);
        result.put("name", user.getXm());
        result.put("dlzh", user.getDlzh());
        result.put("js", user.getJs());
        result.put("scdl", user.getScdl());
        result.put("permissions", resolvePermissions(user));
        result.put("currentZoneId", user.getCurrentZoneId());
        return result;
    }

    /**
     * 获取当前登录用户信息。
     */
    public Map<String, Object> getUserInfo(SysUser user) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("name", user.getXm());
        result.put("dlzh", user.getDlzh());
        result.put("js", user.getJs());
        result.put("scdl", user.getScdl());
        result.put("permissions", resolvePermissions(user));
        result.put("currentZoneId", user.getCurrentZoneId());
        return result;
    }

    /**
     * 修改密码。
     */
    public void changePassword(SysUser user, String oldPassword, String newPassword) {
        if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
            throw new AuthException("原密码错误");
        }
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        if (user.getScdl() != null && user.getScdl() == 1) {
            user.setScdl(0); // 取消首次登录标记
        }
        userMapper.updateById(user);
        log.info("用户 {} 修改密码成功", user.getDlzh());
    }

    /**
     * 解析用户拥有的权限 ID 列表。
     * 暂返回示例权限；后续查询 sys_role_uac 表。
     */
    private List<Integer> resolvePermissions(SysUser user) {
        if (user.getJs() != null && user.getJs() == 9) {
            // 超级管理员：返回全部权限
            return List.of(100100, 100101, 100200, 100201, 100202, 100300,
                    102100, 102101, 102102, 102200, 102201, 102202,
                    103100, 103101, 104100, 104101, 104102,
                    200100, 200101, 200102, 201100, 201101, 201102, 201103, 201200);
        }
        // 普通用户：后续从 sys_role_uac 查询
        return List.of(200100, 200101);
    }

    // ---- 自定义异常 ----

    public static class AuthException extends RuntimeException {
        public AuthException(String message) {
            super(message);
        }
    }
}

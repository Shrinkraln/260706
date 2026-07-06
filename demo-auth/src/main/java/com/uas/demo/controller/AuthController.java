package com.uas.demo.controller;

import com.uas.demo.entity.SysUser;
import com.uas.demo.result.ApiResult;
import com.uas.demo.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 认证控制器 — 验证 JWT + BCrypt 认证链路。
 * 与主项目 AuthController 接口完全一致。
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /** POST /api/v1/auth/login */
    @PostMapping("/login")
    public ApiResult<Map<String, Object>> login(@RequestBody Map<String, Object> body) {
        try {
            String identifier = String.valueOf(body.get("identifier"));
            String password = String.valueOf(body.get("password"));
            return ApiResult.ok("登录成功", authService.login(identifier, password));
        } catch (AuthService.AuthException e) {
            return ApiResult.fail(401, e.getMessage());
        }
    }

    /** GET /api/v1/auth/getuserinfo */
    @GetMapping("/getuserinfo")
    public ApiResult<Map<String, Object>> getUserInfo(@AuthenticationPrincipal SysUser user) {
        if (user == null) {
            return ApiResult.fail(401, "未登录或登录已过期");
        }
        return ApiResult.ok(authService.getUserInfo(user));
    }

    /** POST /api/v1/auth/logout */
    @PostMapping("/logout")
    public ApiResult<Void> logout() {
        // 无状态 JWT：客户端删除 token 即可；服务端可记录黑名单（Redis）
        return ApiResult.ok(null);
    }

    /** PUT /api/v1/auth/change-password */
    @PutMapping("/change-password")
    public ApiResult<Void> changePassword(@AuthenticationPrincipal SysUser user,
                                          @RequestBody Map<String, String> body) {
        if (user == null) {
            return ApiResult.fail(401, "未登录或登录已过期");
        }
        try {
            authService.changePassword(user, body.get("oldPassword"), body.get("newPassword"));
            return ApiResult.ok(null);
        } catch (AuthService.AuthException e) {
            return ApiResult.fail(400, e.getMessage());
        }
    }

    /** GET /api/v1/auth/permissions */
    @GetMapping("/permissions")
    public ApiResult<Map<String, List<Integer>>> permissions(@AuthenticationPrincipal SysUser user) {
        if (user == null) {
            return ApiResult.fail(401, "未登录或登录已过期");
        }
        Map<String, Object> info = authService.getUserInfo(user);
        @SuppressWarnings("unchecked")
        List<Integer> perms = (List<Integer>) info.get("permissions");
        return ApiResult.ok(Map.of("permissions", perms));
    }
}

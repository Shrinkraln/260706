package com.uas.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Demo 启动类 — 独立于 uasservice，验证 JWT + BCrypt 认证链路。
 *
 * 验证接口：
 *   POST /api/v1/auth/login           — BCrypt 验密 + JWT 签发
 *   GET  /api/v1/auth/getuserinfo     — JWT 解析 + 用户信息
 *   POST /api/v1/auth/logout          — 登出
 *   PUT  /api/v1/auth/change-password — 修改密码
 *   GET  /api/v1/auth/permissions     — 权限列表
 *   GET  /api/v1/manager-user/list    — 带认证的分页查询
 */
@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}

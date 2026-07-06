package com.uas.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.uas.demo.dto.PageResult;
import com.uas.demo.entity.SysUser;
import com.uas.demo.mapper.SysUserMapper;
import com.uas.demo.result.ApiResult;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 用户管理控制器 — 验证带 JWT 认证的 CRUD 操作。
 * 对应主项目 /api/v1/manager-user/*。
 */
@RestController
@RequestMapping("/api/v1/manager-user")
@RequiredArgsConstructor
public class UserController {

    private final SysUserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    /** GET /api/v1/manager-user/list */
    @GetMapping("/list")
    public ApiResult<PageResult<SysUser>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isBlank()) {
            wrapper.and(w -> w.like(SysUser::getDlzh, keyword)
                    .or().like(SysUser::getXm, keyword)
                    .or().like(SysUser::getSjh, keyword));
        }
        wrapper.orderByDesc(SysUser::getCreatedAt);
        IPage<SysUser> page = userMapper.selectPage(
                new Page<>(pageNum, pageSize), wrapper);
        return ApiResult.ok(new PageResult<>(page.getTotal(), page.getRecords()));
    }

    /** POST /api/v1/manager-user */
    @PostMapping
    public ApiResult<SysUser> create(@RequestBody SysUser user) {
        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        // passwordHash 字段在此处接收的是明文，加密后替换
        userMapper.insert(user);
        return ApiResult.ok(user);
    }

    /** PUT /api/v1/manager-user */
    @PutMapping
    public ApiResult<SysUser> update(@RequestBody SysUser user) {
        // 不允许修改密码（密码走 reset-password）
        user.setPasswordHash(null);
        userMapper.updateById(user);
        return ApiResult.ok(userMapper.selectById(user.getZhdlxxbs()));
    }

    /** DELETE /api/v1/manager-user/{zhdlxxbs} */
    @DeleteMapping("/{zhdlxxbs}")
    public ApiResult<Void> delete(@PathVariable Long zhdlxxbs) {
        userMapper.deleteById(zhdlxxbs);
        return ApiResult.ok(null);
    }

    /** PUT /api/v1/manager-user/reset-password/{zhdlxxbs} */
    @PutMapping("/reset-password/{zhdlxxbs}")
    public ApiResult<Void> resetPassword(@PathVariable Long zhdlxxbs,
                                         @RequestBody Map<String, String> body) {
        SysUser user = userMapper.selectById(zhdlxxbs);
        if (user != null) {
            user.setPasswordHash(passwordEncoder.encode(body.get("password")));
            userMapper.updateById(user);
        }
        return ApiResult.ok(null);
    }
}

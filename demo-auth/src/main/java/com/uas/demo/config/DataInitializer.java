package com.uas.demo.config;

import com.uas.demo.entity.SysUser;
import com.uas.demo.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 运行时种子数据：默认管理员 admin / Admin@123
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final SysUserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userMapper.selectCount(null) == 0) {
            SysUser admin = new SysUser();
            admin.setDlzh("admin");
            admin.setXm("系统管理员");
            admin.setPasswordHash(passwordEncoder.encode("Admin@123"));
            admin.setSjh("13800138000");
            admin.setGroupId(1L);
            admin.setJs(9);
            admin.setScdl(0);
            userMapper.insert(admin);
            log.info("====== 已创建默认管理员 admin / Admin@123 ======");
        }
    }
}

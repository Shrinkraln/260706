package com.uas.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户实体 — 与主项目 com.uas.core.entity.SysUser 字段完全一致。
 */
@Data
@TableName("sys_user")
public class SysUser {

    @TableId(value = "zhdlxxbs", type = IdType.AUTO)
    private Long zhdlxxbs;
    private String dlzh;
    private String xm;
    private String passwordHash;
    private String sjh;
    private String dzyx;
    private Long groupId;
    private Integer js;
    private Integer zhzt;
    private Integer scdl;
    private String currentZoneId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

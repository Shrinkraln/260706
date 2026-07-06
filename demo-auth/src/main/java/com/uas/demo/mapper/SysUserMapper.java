package com.uas.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.uas.demo.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户 Mapper — 与主项目 SysUserMapper 模式一致。
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
}

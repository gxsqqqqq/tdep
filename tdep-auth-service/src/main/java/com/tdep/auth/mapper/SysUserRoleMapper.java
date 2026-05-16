package com.tdep.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tdep.auth.entity.SysUserRole;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户角色关联 Mapper。
 */
@Mapper
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {
}

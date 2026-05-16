package com.tdep.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tdep.auth.entity.SysPermission;
import com.tdep.auth.entity.SysRole;
import com.tdep.auth.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户 Mapper，封装用户基础查询与 RBAC 关联查询。
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

    /**
     * 查询用户已启用角色。
     *
     * @param userId 用户主键
     * @return 角色列表
     */
    List<SysRole> selectRolesByUserId(@Param("userId") Long userId);

    /**
     * 查询用户已启用权限。
     *
     * @param userId 用户主键
     * @return 权限列表
     */
    List<SysPermission> selectPermissionsByUserId(@Param("userId") Long userId);
}

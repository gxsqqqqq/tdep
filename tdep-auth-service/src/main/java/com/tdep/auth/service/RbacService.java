package com.tdep.auth.service;

import com.tdep.auth.entity.SysPermission;
import com.tdep.auth.entity.SysRole;

import java.util.List;

/**
 * RBAC 查询服务，集中封装用户角色和权限读取逻辑。
 */
public interface RbacService {

    /**
     * 查询用户角色。
     *
     * @param userId 用户主键
     * @return 角色列表
     */
    List<SysRole> listRolesByUserId(Long userId);

    /**
     * 查询用户权限。
     *
     * @param userId 用户主键
     * @return 权限列表
     */
    List<SysPermission> listPermissionsByUserId(Long userId);

    /**
     * 查询用户角色编码。
     *
     * @param userId 用户主键
     * @return 角色编码集合
     */
    List<String> listRoleCodesByUserId(Long userId);

    /**
     * 查询用户权限编码。
     *
     * @param userId 用户主键
     * @return 权限编码集合
     */
    List<String> listPermissionCodesByUserId(Long userId);
}

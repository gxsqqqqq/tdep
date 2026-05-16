package com.tdep.auth.serviceImpl;

import com.tdep.auth.entity.SysPermission;
import com.tdep.auth.entity.SysRole;
import com.tdep.auth.mapper.SysUserMapper;
import com.tdep.auth.service.RbacService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * RBAC 查询服务实现。
 */
@Service
@RequiredArgsConstructor
public class RbacServiceImpl implements RbacService {

    private final SysUserMapper sysUserMapper;

    @Override
    public List<SysRole> listRolesByUserId(Long userId) {
        return sysUserMapper.selectRolesByUserId(userId);
    }

    @Override
    public List<SysPermission> listPermissionsByUserId(Long userId) {
        return sysUserMapper.selectPermissionsByUserId(userId);
    }

    @Override
    public List<String> listRoleCodesByUserId(Long userId) {
        return listRolesByUserId(userId)
                .stream()
                .map(SysRole::getRoleCode)
                .distinct()
                .toList();
    }

    @Override
    public List<String> listPermissionCodesByUserId(Long userId) {
        return listPermissionsByUserId(userId)
                .stream()
                .map(SysPermission::getPermissionCode)
                .distinct()
                .toList();
    }
}

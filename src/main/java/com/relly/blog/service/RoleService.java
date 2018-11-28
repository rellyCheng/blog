package com.relly.blog.service;

import com.relly.blog.common.model.PageResult;
import com.relly.blog.entity.RoleEntity;
import com.relly.blog.entity.UserEntity;

import java.util.List;

public interface RoleService {
    PageResult getRoleList(Integer pageCurrent, Integer pageSize);

    void addRole(RoleEntity roleEntity, UserEntity currentUser);

    void updateRole(RoleEntity roleEntity, UserEntity currentUser);

    void addUserForRole(List<String> addUsers, String roleId);

    void delUserForRole(List<String> deleteUsers, String roleId);

    void addPermissionForRole(List<String> addPermissions, String roleId);

    void delPermissionForRole(List<String> deletePermissions, String roleId);

    List getPermissionByRole(String roleId);

    List getAllPermissionList();
}

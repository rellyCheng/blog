package com.relly.blog.service;

import com.relly.blog.common.model.PageResult;
import com.relly.blog.entity.PermissionEntity;

import java.util.List;

public interface PermissionService {
    void addPermission(PermissionEntity permissionEntity,String currentUserId);

    PageResult getPermissionList(Integer pageCurrent, Integer pageSize);

    List pmenuList(String type);

    List getMenuList(String id);
}

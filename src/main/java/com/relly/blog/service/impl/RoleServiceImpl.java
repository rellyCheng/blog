package com.relly.blog.service.impl;

import com.relly.blog.common.exception.ServiceException;
import com.relly.blog.common.model.PageResult;
import com.relly.blog.dto.RoleDto;
import com.relly.blog.entity.RoleEntity;
import com.relly.blog.entity.UserEntity;
import com.relly.blog.mapper.PermissionMapper;
import com.relly.blog.mapper.RoleMapper;
import com.relly.blog.service.RoleService;
import com.relly.blog.utils.IdUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {

    @Resource
    private RoleMapper roleMapper;
    @Resource
    private PermissionMapper permissionMapper;

    @Override
    public PageResult getRoleList(Integer pageCurrent, Integer pageSize) {
        int rowCount = roleMapper.getRoleListCount();
        PageResult<RoleDto> pageResult = new PageResult<>(pageCurrent, pageSize, rowCount);
        if (pageResult.willCauseEmptyList()) {
            return pageResult;
        }
        List<RoleDto> departmentDtoList = roleMapper.getRoleList(pageResult);
        pageResult.setPageData(departmentDtoList);
        return pageResult;
    }

    @Override
    public void addRole(RoleEntity roleEntity, UserEntity currentUser) {
        String role = roleEntity.getRole();
        int count = roleMapper.getCountByName(role);
        if (count>0){
            throw new ServiceException("已有相同角色!");
        }
        roleEntity.setId(IdUtil.randomId());
        roleEntity.setCreateTime(new Date());
        roleEntity.setCreateUser(currentUser.getId());
        roleEntity.setUpdateUser(currentUser.getId());
        roleMapper.insertSelective(roleEntity);

    }

    @Override
    public void updateRole(RoleEntity roleEntity, UserEntity currentUser) {
        roleEntity.setUpdateUser(currentUser.getId());
        roleMapper.updateByPrimaryKeySelective(roleEntity);

    }

    @Override
    public void addUserForRole(List<String> addUsers, String roleId) {
        roleMapper.insertBatch(addUsers,roleId);
    }

    @Override
    public void delUserForRole(List<String> deleteUsers, String roleId) {
        roleMapper.delBatch(deleteUsers,roleId);
    }

    @Override
    public void addPermissionForRole(List<String> addPermissions, String roleId) {
        roleMapper.insertBatchPermission(addPermissions,roleId);
    }

    @Override
    public void delPermissionForRole(List<String> deletePermissions, String roleId) {
        roleMapper.delBatchPermission(deletePermissions,roleId);
    }

    @Override
    public List getPermissionByRole(String roleId) {
        List list =  roleMapper.getPermissionByRole(roleId);
        return list;
    }

    @Override
    public List getAllPermissionList() {
        List list = permissionMapper.getAllPermissionList();
        return list;
    }
}

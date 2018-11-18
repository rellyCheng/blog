package com.relly.blog.service.impl;

import com.relly.blog.common.exception.ServiceException;
import com.relly.blog.common.model.PageResult;
import com.relly.blog.dto.PermissionDTO;
import com.relly.blog.entity.PermissionEntity;
import com.relly.blog.entity.RoleEntity;
import com.relly.blog.mapper.PermissionMapper;
import com.relly.blog.mapper.RoleMapper;
import com.relly.blog.service.PermissionService;
import com.relly.blog.utils.IdUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class PermissionServiceImpl implements PermissionService {
    @Resource
    private PermissionMapper permissionMapper;
    @Resource
    private RoleMapper roleMapper;

    @Override
    public void addPermission(PermissionEntity permissionEntity,String currentUserId) {
        String permission = permissionEntity.getPermission();
        String name = permissionEntity.getName();
        int count = permissionMapper.getCountByNameAndPermission(name,permission);
        if (count>0){
            throw new ServiceException("已有相同权限!");
        }
        permissionEntity.setId(IdUtil.randomId());
        permissionEntity.setCreateUser(currentUserId);
        permissionEntity.setUpdateUser(currentUserId);
        permissionEntity.setCreateTime(new Date());
        permissionMapper.insertSelective(permissionEntity);
    }

    @Override
    public PageResult getPermissionList(Integer pageCurrent, Integer pageSize) {
        int rowCount = permissionMapper.getListCount();
        PageResult<PermissionEntity> pageResult = new PageResult<>(pageCurrent, pageSize, rowCount);
        if (pageResult.willCauseEmptyList()) {
            return pageResult;
        }
        List<PermissionEntity> permissionList = permissionMapper.getPermissionList(pageResult);
        pageResult.setPageData(permissionList);
        return pageResult;
    }

    @Override
    public List pmenuList(String type) {
        List<PermissionEntity> pmenuList = permissionMapper.pmenuList(type);
        return pmenuList;
    }

    @Override
    public List getMenuList(String userId) {
        List<PermissionDTO> list;
        List pidList = null;
        List<RoleEntity> roleList = roleMapper.getRoleListByUserId(userId);
        Boolean isAdmin = false;
        for (RoleEntity r:roleList) {
            if (r.getRole().equals("admin")){
                isAdmin = true;
            }
        }
        if(isAdmin){
            //获取全部的菜单权限
            list = permissionMapper.getAllPmenuList();
        }else {
            list = permissionMapper.getPmenuList(userId);
            pidList = permissionMapper.getPidbyUid(userId);
        }
        for (int i = 0; i <list.size() ; i++) {
            PermissionDTO permissionDto = list.get(i);
            List<PermissionDTO> childrenList = permissionMapper.getSmenuList(permissionDto.getId(),pidList);
            permissionDto.setChildren(childrenList);
        }
        return list;
    }
}

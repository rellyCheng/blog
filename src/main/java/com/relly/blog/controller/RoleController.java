package com.relly.blog.controller;

import com.relly.blog.common.model.JsonResult;
import com.relly.blog.common.model.PageResult;
import com.relly.blog.entity.RoleEntity;
import com.relly.blog.entity.UserEntity;
import com.relly.blog.service.RoleService;
import com.relly.blog.service.UserService;
import com.relly.blog.utils.JwtUtil;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author Relly
 * @CreteTime 2018/11/8
 * @Description
 */
@RestController
@RequestMapping("/api/role/")
@Validated
public class RoleController {

    @Resource
    private UserService userService;
    @Resource
    private RoleService roleService;

    @PostMapping("/getRoleList")

    public JsonResult getRoleList(@NotNull(message = "当前页不能为空") Integer pageCurrent,
                                  @NotNull(message = "每页条数不能为空") Integer pageSize) {
        PageResult result = roleService.getRoleList(pageCurrent, pageSize);
        return new JsonResult(result);
    }
    @PostMapping("/addRole")
    public JsonResult addRole(@RequestBody RoleEntity roleEntity, HttpServletRequest request){
        UserEntity currentUser = JwtUtil.getUser(request);
        roleService.addRole(roleEntity,currentUser);
        return new JsonResult();
    }
    @PostMapping("/update/role")
    public JsonResult updateRole(@RequestBody RoleEntity roleEntity, HttpServletRequest request){
        UserEntity currentUser = JwtUtil.getUser(request);
        roleService.updateRole(roleEntity,currentUser);
        return new JsonResult();
    }
    @PostMapping("/addUserForRole")
    public JsonResult addUserForRole(@RequestParam(required = false, value = "addUsers[]")List<String> addUsers,
                                     @RequestParam(required = false, value = "deleteUsers[]")List<String> deleteUsers,
                                     String roleId){
        if (deleteUsers==null&&addUsers==null){
            throw new SecurityException("无效的数据!");
        }
        if (addUsers!=null){
            roleService.addUserForRole(addUsers, roleId);
        }
        if (deleteUsers!=null){
            roleService.delUserForRole(deleteUsers, roleId);
        }
        return new JsonResult();
    }
    @PostMapping("/addPermissionForRole")
    public JsonResult addPermissionForRole(@RequestParam(required = false, value = "addPermissions[]")List<String> addPermissions,
                                           @RequestParam(required = false, value = "deletePermissions[]")List<String> deletePermissions,
                                           String roleId){
        if (addPermissions==null&&deletePermissions==null){
            throw new SecurityException("无效的数据!");
        }
        if (addPermissions!=null){
            roleService.addPermissionForRole(addPermissions, roleId);
        }
        if (deletePermissions!=null){
            roleService.delPermissionForRole(deletePermissions, roleId);
        }
        return new JsonResult();
    }
    @PostMapping("/getUserByRole")
    public JsonResult getEmpByRole(String roleId){
        Map map = new HashMap();
        List haveList = userService.getUserListByRole(roleId);
        List allEmpList = userService.getAllUserList();
        map.put("haveList",haveList);
        map.put("allUserList",allEmpList);
        return new JsonResult(map);
    }
    @RequestMapping("/getPermissionByRole")
    public JsonResult getPermissionByRole(String roleId){
        Map map = new HashMap();
        List haveList = roleService.getPermissionByRole(roleId);
        List allPermissionList = roleService.getAllPermissionList();
        map.put("haveList",haveList);
        map.put("allPermissionList",allPermissionList);
        return new JsonResult(map);
    }

}

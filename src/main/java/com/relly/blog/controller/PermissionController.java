package com.relly.blog.controller;

import com.relly.blog.common.model.JsonResult;
import com.relly.blog.common.model.PageResult;
import com.relly.blog.entity.PermissionEntity;
import com.relly.blog.entity.UserEntity;
import com.relly.blog.service.PermissionService;
import com.relly.blog.utils.JwtUtil;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @Author Relly
 * @CreteTime 2018/11/8
 * @Description
 */
@RestController
@RequestMapping("/api/permission")
@Validated
public class PermissionController {
    @Resource
    private PermissionService permissionService;

    @PostMapping("/addPermission")
    public JsonResult addPermission(@RequestBody @Valid PermissionEntity permissionEntity, HttpServletRequest request){
        UserEntity currentUser = JwtUtil.getUser(request);
        permissionService.addPermission(permissionEntity,currentUser.getId());
        return new JsonResult();
    }
    @PostMapping("/getPermissionList")
    public JsonResult getPermissionList(@NotNull(message = "当前页不能为空") Integer pageCurrent,
                                        @NotNull(message = "每页条数不能为空") Integer pageSize){
        PageResult pageResult = permissionService.getPermissionList(pageCurrent,pageSize);
        return new JsonResult(pageResult);
    }
    @PostMapping("/getParentPermissionList")
    public JsonResult getParentPermission(String type){
        List list = permissionService.pmenuList(type);
        return new JsonResult(list);
    }
    @RequestMapping("/getMenuList")
    public JsonResult getMenuList(HttpServletRequest request){
        UserEntity currentUser = JwtUtil.getUser(request);
        List list = permissionService.getMenuList(currentUser.getId());
        return new JsonResult(list);
    }
}

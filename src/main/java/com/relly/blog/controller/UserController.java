package com.relly.blog.controller;

import com.relly.blog.common.model.JsonResult;
import com.relly.blog.common.model.PageResult;
import com.relly.blog.dto.UserDTO;
import com.relly.blog.entity.UserEntity;
import com.relly.blog.service.UserService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;

/**
 * @Author Relly
 * @CreteTime 2018/11/8
 * @Description
 */
@RestController
@RequestMapping("/api/user/")
@Validated
public class UserController {

    @Resource
    private UserService userService;

    @PostMapping("registUser")
    public JsonResult registUser(HttpServletRequest request,@NotBlank String name,
                                 @NotBlank String userName, @NotBlank String password){
//        UserEntity userEntity = JwtUtil.getUser(request);
        UserEntity userEntity = UserEntity.builder().id("admin").build();
        userService.addUser(userEntity,name,userName,password);
        return new JsonResult();
    }

    /**

     *@description 获取用户列表
     *@param pageSize 每页大小
     *@param pageCurrent 第几页
     *@return
     *@author relly
     *@date 2018/11/10
     *@update
     */
    @PostMapping("getUserList")
    public JsonResult getUserList(int pageSize,int pageCurrent){
        PageResult<UserDTO> pageResult = userService.getUserList(pageSize,pageCurrent);
        return new JsonResult(pageResult);
    }
}

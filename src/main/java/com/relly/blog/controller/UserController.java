package com.relly.blog.controller;

import com.relly.blog.common.model.JsonResult;
import com.relly.blog.common.model.PageResult;
import com.relly.blog.dto.UserDTO;
import com.relly.blog.dto.UserDetailDTO;
import com.relly.blog.entity.UserEntity;
import com.relly.blog.service.UserService;
import com.relly.blog.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

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

    @Value("${file.address}")
    private String fileAddress;

    /**
     *
     * 注册用户
     * @author Thunder
     * @date 2018/11/16 17:16
     * @param request, userDTO
     * @return com.relly.blog.common.model.JsonResult
     */
    @PostMapping("registUser")
    public JsonResult registUser(HttpServletRequest request, @RequestBody @Validated UserDTO userDTO){
        UserEntity currentUser = JwtUtil.getUser(request);
        userService.addUser(currentUser,userDTO);
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

    /**
     * 用户添加详细的信息
     * @author relly
     * @date 2018/11/16 13:20
     * @param userDetailDTO 用户的详细信息实体
     * @return com.relly.blog.common.model.JsonResult
     */
    @PostMapping("updateUserDetail")
    public JsonResult updateUserDetail(@RequestBody @Validated UserDetailDTO userDetailDTO, HttpServletRequest request){
        UserEntity currentUser = JwtUtil.getUser(request);
        if(userDetailDTO.getAvatar()!=null){
            userDetailDTO.setAvatar(fileAddress+userDetailDTO.getAvatar());
        }
        userDetailDTO.setUserId(currentUser.getId());
        userService.updateUserDetail(userDetailDTO);
        return new JsonResult();
    }

    @PostMapping("currentUser")
    public JsonResult getUserDetail(HttpServletRequest request){
        UserEntity currentUser = JwtUtil.getUser(request);
        UserDetailDTO userDetailDTO = userService.getUserDetail(currentUser.getId());
        return new JsonResult(userDetailDTO);
    }

    @PostMapping("updateTags")
    public JsonResult updateTags(@RequestParam("tag") String tag,HttpServletRequest request){
        UserEntity currentUser = JwtUtil.getUser(request);
        userService.updateTags(currentUser.getId(),tag);
        return new JsonResult();
    }


}

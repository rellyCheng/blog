package com.relly.blog.controller;

import com.relly.blog.common.model.JsonResult;
import com.relly.blog.common.model.PageResult;
import com.relly.blog.dto.UserDTO;
import com.relly.blog.entity.UserEntity;
import com.relly.blog.mapper.UserMapper;
import com.relly.blog.service.UserService;
import com.relly.blog.utils.IdUtil;
import com.relly.blog.utils.MD5salt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.Map;

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
    @Resource
    private UserMapper userMapper;

    @PostMapping("registUser")
    public JsonResult registUser(HttpServletRequest request, @RequestBody @Validated UserDTO userDTO){
//        UserEntity userEntity = JwtUtil.getUser(request);
        UserEntity currentUser = UserEntity.builder().id("admin").build();
        userService.addUser(currentUser,userDTO);
        return new JsonResult();
    }
    @PostMapping("add")
    public JsonResult add(){
        Map<String,String> map = MD5salt.md5salt("admin","123123");
        UserEntity userEntity = UserEntity.builder()
                .userName("admin")
                .password(map.get("pwd"))
                .salt(map.get("salt"))
                .verify(map.get("verify"))
                .createUser("admin")
                .id(IdUtil.randomId())
                .createTime(new Date())
                .updateUser("admin")
                .name("管理员")
                .bgColor("#FFDEAD")
                .isDelete(0)
                .build();
        userMapper.insert(userEntity);
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

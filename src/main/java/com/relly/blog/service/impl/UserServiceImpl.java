package com.relly.blog.service.impl;

import com.relly.blog.common.model.PageResult;
import com.relly.blog.dto.UserDTO;
import com.relly.blog.entity.UserEntity;
import com.relly.blog.mapper.UserMapper;
import com.relly.blog.service.UserService;
import com.relly.blog.utils.IdUtil;
import com.relly.blog.utils.MD5salt;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {
    @Resource
    private UserMapper userMapper;
    @Override
    public UserEntity getUserByUserName(String userName) {
        UserEntity userEntity = userMapper.getUserByUserName(userName);
        return userEntity;
    }

    @Override
    public void addUser(UserEntity user,String name,String userName, String password) {
        Map<String,String> map = MD5salt.md5salt(userName,password);
        UserEntity userEntity = UserEntity.builder()
                .userName(userName)
                .password(map.get("pwd"))
                .salt(map.get("salt"))
                .createUser(user.getId())
                .id(IdUtil.randomId())
                .createTime(new Date())
                .updateTime(new Date())
                .updateUser(user.getId())
                .isDelete(0)
                .name(name)
                .build();
        userMapper.insert(userEntity);
    }

    @Override
    public PageResult<UserDTO> getUserList(int pageSize, int pageCurrent) {
        int rowCount = userMapper.getUserListCount();
        PageResult<UserDTO> pageResult = new PageResult<>(pageCurrent, pageSize, rowCount);
        if (pageResult.willCauseEmptyList()) {
            return pageResult;
        }
        List<UserDTO> List = userMapper.getUserList(pageResult);
        pageResult.setPageData(List);
        return pageResult;
    }
}

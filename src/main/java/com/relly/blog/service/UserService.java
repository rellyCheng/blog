package com.relly.blog.service;


import com.relly.blog.common.model.PageResult;
import com.relly.blog.dto.UserDTO;
import com.relly.blog.dto.UserDetailDTO;
import com.relly.blog.entity.UserEntity;

public interface UserService {

    UserEntity getUserByUserName(String userName);

    void addUser(UserEntity currentUser,UserDTO userDTO);

    PageResult<UserDTO> getUserList(int pageSize, int pageCurrent);

    void updateUserDetail(UserDetailDTO userDetailDTO);

    UserDetailDTO getUserDetail(String id);

    void updateTags(String currentUserId,String tag);
}

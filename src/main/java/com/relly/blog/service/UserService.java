package com.relly.blog.service;


import com.relly.blog.common.model.PageResult;
import com.relly.blog.dto.AllUserDTO;
import com.relly.blog.dto.UserDTO;
import com.relly.blog.dto.UserDetailDTO;
import com.relly.blog.dto.UserRegisterDTO;
import com.relly.blog.entity.UserEntity;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;

public interface UserService {

    UserEntity getUserByUserName(String userName);

    UserEntity getUserByOpenId(String openid);

    void addUser(UserEntity currentUser,UserDTO userDTO);

    PageResult<UserDTO> getUserList(int pageSize, int pageCurrent);

    void updateUserDetail(UserDetailDTO userDetailDTO);

    UserDetailDTO getUserDetail(String id);

    void updateTags(String currentUserId,String tag);

    List<String> getUserListByRole(String roleId);

    List<AllUserDTO> getAllUserList();

    String register(UserRegisterDTO userRegisterDTO);

    void sendMail(String title, String url, String email);

    void activation(String verify);

    Map<String, Object> githubUser(String code) throws IOException;
}

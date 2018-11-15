package com.relly.blog.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity extends CommonBaseEntity implements Serializable {

    private String id;


    private Date createTime;


    private Date updateTime;


    private String createUser;


    private Integer isDelete;

    private String updateUser;


    private String name;


    private String userName;


    private String password;

    private String salt;

    private String verify;

    private String bgColor;


    private static final long serialVersionUID = 1L;

}
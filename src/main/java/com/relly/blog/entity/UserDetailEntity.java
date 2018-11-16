package com.relly.blog.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailEntity extends CommonBaseEntity implements Serializable {

    private String id;


    private String userId;


    private String address;


    private String avatar;


    private String country;


    private String email;


    private String province;

    private String provinceKey;


    private String city;

    private String cityKey;


    private String phone;


    private String signature;


    private String title;

    private String profile;


    private static final long serialVersionUID = 1L;
}
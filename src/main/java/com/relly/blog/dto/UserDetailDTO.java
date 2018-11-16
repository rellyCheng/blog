package com.relly.blog.dto;

import com.relly.blog.vo.Geographic;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author Relly
 * @CreteTime 2018/11/15 10:02 PM
 * @Description
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailDTO {

    private String id;

    private String userId;

    /**
     * 地址
     */
    private String address;

    /**
     * 国家
     */
    private String country;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 省市
     */
    private Geographic geographic;

    /**
     * 电话
     */
    private String phone;

    /**
     * 简介
     */
    private String profile;


}

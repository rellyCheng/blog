package com.relly.blog.dto;

import com.relly.blog.vo.Geographic;
import com.relly.blog.vo.Tags;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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
     * 背景颜色
     */
    private String bgColor;

    /**
     * 姓名
     */
    private String name;

    /**
     * 提醒的数量
     */
    private Integer notifyCount;

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

    /**
     * 省
     */
    private String province;

    /**
     * 市
     */
    private String city;

    /**
     * 省Key
     */
    private String provinceKey;

    /**
     * 市key
     */
    private String cityKey;

    private String avatar;

    private String signature;

    private String title;

    private String tags;

    private List<Tags> tagsList;

}

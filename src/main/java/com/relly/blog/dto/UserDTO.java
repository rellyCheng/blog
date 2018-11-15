package com.relly.blog.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @Author Relly
 * @CreteTime 2018/11/10 10:02 PM
 * @Description
 */
@Data
public class UserDTO {

    private String id;

    @NotBlank
    private String userName;

    @NotBlank
    private String name;

    @NotBlank
    private String bgColor;

    @NotBlank
    private String password;
}

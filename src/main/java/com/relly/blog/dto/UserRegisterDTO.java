package com.relly.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * @Author Relly
 * @CreteTime 2018/12/1 9:22 AM
 * @Description
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterDTO {

    @NotBlank
    private String email;

    @NotBlank
    private String name;

    @NotBlank
    private String userName;

    @NotBlank
    private String password;

    @NotBlank
    private String bgColor;


    @NotBlank
    private String ipAddress;


    @NotBlank
    private String provinceKey;


    @NotBlank
    private String province;


    @NotBlank
    private String cityKey;

    @NotBlank
    private String city;

    @NotBlank
    private String country;
}

package com.relly.blog.dto;

import lombok.Builder;
import lombok.Data;

/**
 * @Author Relly
 * @CreteTime 2018/11/15 10:02 PM
 * @Description
 */
@Data
@Builder
public class UserDetailDTO {

    private String id;

    private String userId;

    private String address;

}

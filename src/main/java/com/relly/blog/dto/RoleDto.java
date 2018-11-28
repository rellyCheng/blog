package com.relly.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoleDto {

    private Long id;
    /**
     * 角色名称
     */
    private String role;

    /**
     * 描述
     */
    private String description;

    /**
     * 创建时间
     */
    protected Date createTime;
}

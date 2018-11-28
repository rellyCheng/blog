package com.relly.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author Relly
 * @CreteTime 2018/11/28 10:33 PM
 * @Description
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AllUserDTO {
    private String key;
    private String name;
    private String permission;
}

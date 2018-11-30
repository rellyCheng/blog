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
public class NoticeDTO {

    private String id;

    private Date datetime;

    private Date deadline;

    private String title;

    private String description;

    private String type;

    private String avatar;

    private String status;

    private Integer isRead;

    private String userId;

    private String sendId;

    private String extra;
}

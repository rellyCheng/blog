package com.relly.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArticleMessageDTO {
    private String id;
    private String parentId;
    private String articleId;
    private String aite;
    private Date createTime;
    private String name;
    private String commentContent;
    private String bgColor;
    private String avatar;
    private Integer rank;
    private List<ArticleMessageDTO> children;
}

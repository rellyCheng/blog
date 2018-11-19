package com.relly.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArticleDTO {
    private String articleId;
    private Integer type;
    private String content;
    private String title;
    private String cover;
    private String description;
    private String href;
    private String owner;
    private String members;
    private Integer like;
    private Integer message;
    private Integer star;
    private String updateTime;
}

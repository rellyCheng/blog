package com.relly.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArticleDTO {
    private String articleId;
    @NotBlank
    private String type;
    @NotBlank
    private String content;
    @NotBlank
    private String title;
    @NotBlank
    private String cover;
    @NotNull
    private Boolean isPublic;
    @NotBlank
    private String description;
    private String href;
    private String owner;
    private String members;
    private Integer like;
    private Integer message;
    private Integer star;
    private String updateTime;
}

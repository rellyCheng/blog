package com.relly.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

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
    private String ownerName;
    private String members;
    private Integer likeNum;
    private Integer message;
    private Integer star;
    private String updateTime;
    private List<ArticleMessageDTO> articleMessageDTOList;
    private String bgColor;
    private String avatar;
}

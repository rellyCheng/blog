package com.relly.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddArticleMessageDTO {
    private String id;
    private String parentId;
    private String articleId;
    private String aite;
    private String content;
}

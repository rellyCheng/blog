package com.relly.blog.service;

import com.relly.blog.dto.ArticleDTO;

/**
 * @Author Relly
 * @CreteTime 2019-03-09 15:41
 * @Description
 */
public interface ESService {
    /**
     * 保存文章到ES
     * @param articleDTO
     */
    void addArticleForES(ArticleDTO articleDTO);
}

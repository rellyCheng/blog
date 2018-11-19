package com.relly.blog.service;

import com.relly.blog.common.model.PageResult;
import com.relly.blog.dto.ArticleDTO;

import java.util.List;

public interface ArticleService {
    List<ArticleDTO> getArticleListByUser(String id);

    PageResult getMyArticleListMore(String id, int pageCurrent);
}

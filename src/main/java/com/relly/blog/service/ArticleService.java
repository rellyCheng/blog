package com.relly.blog.service;

import com.relly.blog.common.model.PageResult;
import com.relly.blog.dto.AddArticleMessageDTO;
import com.relly.blog.dto.ArticleDTO;
import com.relly.blog.dto.ArticleFilterDTO;
import com.relly.blog.dto.ArticleMessageDTO;
import com.relly.blog.entity.UserEntity;

import java.util.List;

public interface ArticleService {
    List<ArticleDTO> getArticleListByUser(String id);

    PageResult getMyArticleListMore(String id, int pageCurrent);

    void save(UserEntity currentUser, ArticleDTO articleDTO);

    ArticleDTO getArticleDetail(String articleId);

    PageResult<ArticleMessageDTO> getArticleMessageDetail(String articleId, Integer pageSize, Integer pageCurrent);

    void addMessageForArticle(String userId,AddArticleMessageDTO articleMessageDTO);

    void likeArticle(String articleId);

    String starArticle(String userId,String articleId);

    PageResult<ArticleDTO> getAllArticleList(ArticleFilterDTO articleFilterDTO, int pageCurrent);

    PageResult<ArticleDTO> getMystarArticle(String userId, Integer pageCurrent, Integer pageSize);
}

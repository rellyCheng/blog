package com.relly.blog.service.impl;

import com.relly.blog.common.model.PageResult;
import com.relly.blog.dto.ArticleDTO;
import com.relly.blog.mapper.ArticleMapper;
import com.relly.blog.service.ArticleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
@Slf4j
public class ArticleServiceImpl implements ArticleService {

    @Resource
    private ArticleMapper articleMapper;

    @Override
    public List<ArticleDTO> getArticleListByUser(String userId) {
        List<ArticleDTO> list = articleMapper.getArticleListByUser(userId);
        return list;
    }

    @Override
    public PageResult getMyArticleListMore(String userId, int pageCurrent) {
        int rowCount = articleMapper.gettMyArticleListCount(userId);
        PageResult<ArticleDTO> pageResult = new PageResult<>(pageCurrent, 1, rowCount);

        List<ArticleDTO> list = articleMapper.getMyArticleListMore(userId,pageResult);
        pageResult.setPageData(list);
        return pageResult;
    }
}

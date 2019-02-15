package com.relly.blog.service.impl;

import com.relly.blog.common.model.PageResult;
import com.relly.blog.dto.ArticleDTO;
import com.relly.blog.dto.ArticleMessageDTO;
import com.relly.blog.dto.UserDTO;
import com.relly.blog.entity.ArticleEntity;
import com.relly.blog.entity.UserEntity;
import com.relly.blog.mapper.ArticleMapper;
import com.relly.blog.service.ArticleService;
import com.relly.blog.utils.IdUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class ArticleServiceImpl implements ArticleService {

    @Resource
    private ArticleMapper articleMapper;

    @Value("${file.address}")
    private String fileAddress;

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

    @Override
    public void save(UserEntity currentUser, ArticleDTO articleDTO) {
        ArticleEntity articleEntity = ArticleEntity.builder()
                .content(articleDTO.getContent())
                .title(articleDTO.getTitle())
                .cover(fileAddress+articleDTO.getCover())
                .createTime(new Date())
                .type(articleDTO.getType())
                .isPublic(articleDTO.getIsPublic()?0:1)
                .description(articleDTO.getDescription())
                .href("总之")
                .id(IdUtil.randomId())
                .owner(currentUser.getName())
                .createUser(currentUser.getId())
                .updateUser(currentUser.getId())
                .build();
        articleMapper.insertSelective(articleEntity);

    }

    @Override
    public ArticleDTO getArticleDetail(String articleId) {
        ArticleDTO articleDTO = articleMapper.getArticleDetail(articleId);
        return articleDTO;
    }

    @Override
    public PageResult<ArticleMessageDTO> getArticleMessageDetail(String articleId, Integer pageSize, Integer pageCurrent) {
        int count = articleMapper.getArticleMessageCount(articleId);
        PageResult<ArticleMessageDTO> pageResult = new PageResult<>(pageCurrent, pageSize, count);
        List<ArticleMessageDTO> articleMessageDTOList = articleMapper.getArticleMessage(articleId,count+1,pageResult);
        List<ArticleMessageDTO> articleMessageDTOChildrenList = new ArrayList<>();
        for (ArticleMessageDTO am: articleMessageDTOList) {
            articleMessageDTOChildrenList = articleMapper.getArticleMessageChildren(am.getId(),am.getArticleId());
            am.setChildren(articleMessageDTOChildrenList);
        }
        pageResult.setPageData(articleMessageDTOList);
        return pageResult;
    }
}

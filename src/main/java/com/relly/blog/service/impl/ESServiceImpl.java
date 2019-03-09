package com.relly.blog.service.impl;

import com.relly.blog.common.exception.ServiceException;
import com.relly.blog.dto.ArticleDTO;
import com.relly.blog.service.ESService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @Author Relly
 * @CreteTime 2019-03-09 15:42
 * @Description
 */
@Service
@Slf4j
public class ESServiceImpl implements ESService {
    @Resource
    private TransportClient client;

    @Override
    public void addArticleForES(ArticleDTO articleDTO) {
        XContentBuilder content = null;
        try {
            content = XContentFactory.jsonBuilder()
                    .startObject()
                    .field("title",articleDTO.getTitle())
                    .field("ownerName",articleDTO.getOwnerName())
                    .field("articleId",articleDTO.getArticleId())
                    .field("content",articleDTO.getContent())
                    .field("cover",articleDTO.getCover())
                    .field("createUser",articleDTO.getCreateUser())
                    .field("description",articleDTO.getDescription())
                    .field("href",articleDTO.getHref())
                    .field("updateTime",articleDTO.getUpdateTime())
                    .endObject();
        } catch (IOException e) {
            e.printStackTrace();
            throw new ServiceException("保存到ES发生异常");
        }
        this.client.prepareIndex("article","all")
                .setSource(content)
                .get();
    }
}

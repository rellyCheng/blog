package com.relly.blog.api;


import com.relly.blog.common.model.JsonResult;
import com.relly.blog.common.model.PageResult;
import com.relly.blog.dto.ArticleDTO;
import com.relly.blog.dto.ArticleFilterDTO;
import com.relly.blog.dto.ArticleMessageDTO;
import com.relly.blog.service.ArticleService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/publicApi/")
@Slf4j
public class PublicController {
    @Resource
    private ArticleService articleService;
    @Resource
    private TransportClient client;
    /**
     * 获取全部的文章
     * @author Relly
     * @date 2018/02/27 15:17
     * @return com.relly.blog.common.model.JsonResult
     */
    @PostMapping("article/getAllArticleListMore")
    public JsonResult getAllArticleList(@RequestBody ArticleFilterDTO articleFilterDTO, @Param("pageCurrent") int pageCurrent){
        PageResult<ArticleDTO> pageResult = articleService.getAllArticleList(articleFilterDTO,pageCurrent);
        return new JsonResult(pageResult);
    }

    /**
     * 获取文章详情
     * @param articleId
     * @return
     */
    @RequestMapping("article/getArticleDetail")
    public JsonResult getArticleDetail( @NotBlank String articleId){
        ArticleDTO articleDTO = articleService.getArticleDetail(articleId);
        return new JsonResult(articleDTO);
    }
    /**
     * 获取文章评论
     * @param articleId
     * @return
     */
    @RequestMapping("article/getArticleMessageDetail")
    public JsonResult getArticleMessageDetail( @NotBlank String articleId,
                                               @Param("pageSize")Integer pageSize,@Param("pageCurrent")Integer pageCurrent){
        PageResult<ArticleMessageDTO> pageResult = articleService.getArticleMessageDetail(articleId,pageSize,pageCurrent);
        return new JsonResult(pageResult);
    }

    /**
     * 搜索文章 根据title获取文章列表
     * @param title
     * @return
     */
    @PostMapping("article/getArticleByTitle")
    public JsonResult getArticleByTitle(@RequestParam("title") String title){
        PageResult<ArticleDTO> pageResult = articleService.getArticleByTitle(title);
        return new JsonResult(pageResult);
    }

    /**
     * 搜索文章 根据关键字全文检索
     * @param key
     * @return
     */
    @PostMapping("article/getArticleByKey")
    public JsonResult getArticleByKey(@RequestParam(value = "key",required = false) String key){
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if (key!=null){
            boolQueryBuilder.must(QueryBuilders.multiMatchQuery(key,"title","ownerName","description"));
        }
        SearchRequestBuilder searchRequestBuilder = this.client.prepareSearch("article")
                .setTypes("all")
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(boolQueryBuilder)
                .setFrom(0)
                .setSize(10);
        SearchResponse response = searchRequestBuilder.get();
        List<Map<String,Object>> result = new ArrayList<>();
        for (SearchHit hit:response.getHits()) {
            result.add(hit.getSourceAsMap());
        }
        return new JsonResult(result);
    }


}

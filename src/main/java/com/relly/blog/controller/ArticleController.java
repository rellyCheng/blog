package com.relly.blog.controller;

import com.relly.blog.common.model.JsonResult;
import com.relly.blog.common.model.PageResult;
import com.relly.blog.dto.ArticleDTO;
import com.relly.blog.entity.UserEntity;
import com.relly.blog.service.ArticleService;
import com.relly.blog.utils.JwtUtil;
import org.apache.ibatis.annotations.Param;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/article/")
@Validated
public class ArticleController {

    @Resource
    private ArticleService articleService;

    /**
     * 获取我的文章
     * @author Relly
     * @date 2018/11/19 15:07
     * @return com.relly.blog.common.model.JsonResult
     */
    @PostMapping("getArticleListByUser")
    public JsonResult getArticleListByUser(HttpServletRequest request){
        UserEntity userEntity = JwtUtil.getUser(request);
        List<ArticleDTO> list = articleService.getArticleListByUser(userEntity.getId());
        return new JsonResult(list);
    }

    @RequestMapping("getMyArticleListMore")
    public JsonResult getMyArticleListMore(HttpServletRequest request, @Param("pageCurrent") int pageCurrent){
        UserEntity userEntity = JwtUtil.getUser(request);
        PageResult pageResult = articleService.getMyArticleListMore(userEntity.getId(),pageCurrent);
        return new JsonResult(pageResult);
    }
}

package com.relly.blog.controller;

import com.relly.blog.common.model.JsonResult;
import com.relly.blog.common.model.PageResult;
import com.relly.blog.dto.AddArticleMessageDTO;
import com.relly.blog.dto.ArticleDTO;
import com.relly.blog.dto.ArticleMessageDTO;
import com.relly.blog.dto.UserDTO;
import com.relly.blog.entity.UserEntity;
import com.relly.blog.service.ArticleService;
import com.relly.blog.utils.JwtUtil;
import org.apache.ibatis.annotations.Param;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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

    /**
     * 加载更多文章
     * @param request
     * @param pageCurrent
     * @return
     */
    @RequestMapping("getMyArticleListMore")
    public JsonResult getMyArticleListMore(HttpServletRequest request, @Param("pageCurrent") int pageCurrent){
        UserEntity userEntity = JwtUtil.getUser(request);
        PageResult pageResult = articleService.getMyArticleListMore(userEntity.getId(),pageCurrent);
        return new JsonResult(pageResult);
    }

    /**
     * 新建文章
     * @param request
     * @param articleDTO
     * @return
     */
    @RequestMapping("save")
    public JsonResult save(HttpServletRequest request, @RequestBody ArticleDTO articleDTO){
        UserEntity userEntity = JwtUtil.getUser(request);
        articleService.save(userEntity,articleDTO);
        return new JsonResult();
    }

    /**
     * 获取文章详情
     * @param articleId
     * @return
     */
    @RequestMapping("getArticleDetail")
    public JsonResult getArticleDetail( @NotBlank String articleId){
        ArticleDTO articleDTO = articleService.getArticleDetail(articleId);
        return new JsonResult(articleDTO);
    }

    /**
     * 获取文章评论
     * @param articleId
     * @return
     */
    @RequestMapping("getArticleMessageDetail")
    public JsonResult getArticleMessageDetail( @NotBlank String articleId,
                                        @Param("pageSize")Integer pageSize,@Param("pageCurrent")Integer pageCurrent){
        PageResult<ArticleMessageDTO> pageResult = articleService.getArticleMessageDetail(articleId,pageSize,pageCurrent);
        return new JsonResult(pageResult);
    }

    /**
     * 评论文章
     * @param request
     * @param articleMessageDTO
     * @return
     */
    @PostMapping("addMessageForArticle")
    public JsonResult addMessageForArticle(HttpServletRequest request,@RequestBody AddArticleMessageDTO articleMessageDTO){
        UserEntity userEntity = JwtUtil.getUser(request);
        articleService.addMessageForArticle(userEntity.getId(),articleMessageDTO);
        return new JsonResult();
    }
}

package com.relly.blog.api;


import com.alibaba.fastjson.JSONObject;
import com.relly.blog.common.exception.ServiceException;
import com.relly.blog.common.model.JsonResult;
import com.relly.blog.common.model.PageResult;
import com.relly.blog.dto.ArticleDTO;
import com.relly.blog.dto.ArticleFilterDTO;
import com.relly.blog.dto.ArticleMessageDTO;
import com.relly.blog.entity.UserEntity;
import com.relly.blog.entity.WxUserEntity;
import com.relly.blog.mapper.UserMapper;
import com.relly.blog.mapper.WxUserMapper;
import com.relly.blog.service.ArticleService;
import com.relly.blog.service.PermissionService;
import com.relly.blog.service.UserService;
import com.relly.blog.utils.JwtUtil;
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
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.HashMap;
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
    @Resource
    private UserService userService;
    @Resource
    private UserMapper userMapper;
    @Resource
    private PermissionService permissionService;

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
    @RequestMapping("article/getArticleByTitle")
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

    @RequestMapping("wxCode2Session")
    public JsonResult code2Session(@RequestParam String js_code){
        RestTemplate rest = new RestTemplate();
        String appid = "wxa0b3f7973fef558c";
        String secret = "556a36dd25cd4e01a131946e6f5e7fac";
        String grant_type = "authorization_code";
        String url = "https://api.weixin.qq.com/sns/jscode2session?appid="+appid+"&secret="+secret+"&grant_type="+grant_type+"&js_code="+js_code;
        String res = rest.getForObject(url, String.class);
        JSONObject jsonObj = JSONObject.parseObject(res);
        if(jsonObj.getString("errcode")!=null&&jsonObj.getString("errcode").equals("40163")){
            throw new ServiceException("code失效");
        }
        String openid = jsonObj.getString("openid");
        UserEntity userEntity = userService.getUserByOpenId(openid);
        if (userEntity==null){
            throw new ServiceException(openid);
        }
        String token = JwtUtil.sign(userEntity.getUserName(),userEntity.getId(),userEntity.getVerify());
        Map<String,Object> map = new HashMap<>(3);
        map.put("token",token);
        map.put("userId",userEntity.getId());
        map.put("name",userEntity.getName());
        map.put("openid",openid);
        List<String> authList = permissionService.getPermissionListByUserId(userEntity.getId());
        authList.add("currentUser");
        map.put("currentAuthority",authList);
        return new JsonResult(map);
    }

    /**
     *
     * wx关联账号
     * @author Thunder
     * @date 2019/7/11 11:13
     * @param userName, openid
     * @return com.relly.blog.common.model.JsonResult
     */
    @RequestMapping("linkAccount")
    public JsonResult linkAccount(@RequestParam String userName,@RequestParam String openid){
        UserEntity userEntity = userService.getUserByUserName(userName);
        if (userEntity==null){
            throw new ServiceException("当前用户不存在");
        }
        userEntity.setOpenid(openid);
        userMapper.updateByPrimaryKeySelective(userEntity);
        return new JsonResult();
    }
    @PostMapping("wxRegist")
    public JsonResult wxRegist(@RequestBody WxUserEntity wxUserEntity){
        userService.insertWxUser(wxUserEntity);
        return new JsonResult();
    }

}

package com.relly.blog.common.config;

import com.relly.blog.common.model.JsonResult;
import com.relly.blog.entity.UserEntity;
import com.relly.blog.service.UserService;
import com.relly.blog.utils.JwtUtil;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * JWTFilter 拦截所有请求验证token
 * @author Thunder
 * @date 2018/7/27 17:44
 * @param  * @param null
 * @return
 */
@Component
public class JWTFilter extends BasicHttpAuthenticationFilter {

    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    @Resource
    private UserService userService;


    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        Subject subject = getSubject(request,response);
//        未登陆 如果页面长时间未刷新,session就会改变,导致subject未认证,和jwttoken的时效性冲突
        if (!subject.isAuthenticated()){
            response401(response) ;
            return false;
        }

        //判断用户是否想要登入。检测header里面是否包含Authorization字段即可
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String authorization = httpServletRequest.getHeader("Authorization");
        if(authorization == null){
           return false;
        }
        authorization = authorization.substring(7, authorization.length());
        if(authorization== null || authorization.equals("null")){
            return false;
        }
        return checkToken(authorization,response,request) ;
    }

    /**
     *校验token
     */
    protected boolean checkToken(String token, ServletResponse resp, ServletRequest request ) {


        UserEntity user = null;
        try {
            user = JwtUtil.getUser((HttpServletRequest)request);
        } catch (Exception e) {
            e.printStackTrace();
            response401(resp) ;
            return false;
        }
        user = userService.getUserByUserName(user.getUserName());
        Boolean bl = JwtUtil.verify(token,user.getUserName(),user.getId(),user.getVerify());
        if(!bl){
            response401(resp) ;
            return bl;
        }
        System.out.printf(String.valueOf(((HttpServletRequest) request).getSession()));

        return true;

    }
    /**
     * 非法请求 跳转登录界面
     */
    private JsonResult response401( ServletResponse resp) {
        HttpServletResponse response = (HttpServletResponse) resp;
        response.setStatus(401);
        return new JsonResult("登录失效");
    }
}

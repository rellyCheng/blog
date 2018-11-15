package com.relly.blog.common.config;

import com.relly.blog.common.exception.ExceptionMessage;
import com.relly.blog.common.model.JsonResult;
import com.relly.blog.entity.UserEntity;
import com.relly.blog.service.UserService;
import com.relly.blog.utils.JwtUtil;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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

    /**
     * 判断用户是否想要登入。
     * 检测header里面是否包含Authorization字段即可
     */
    @Override
    protected boolean isLoginAttempt(ServletRequest request, ServletResponse response) {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpSession session = req.getSession();
        String authorization = req.getHeader("Authorization");
        return authorization != null;
    }

    /**
     *校验token
     */
    @Override
    protected boolean executeLogin(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String token = httpServletRequest.getHeader("Authorization");
        token = token.substring(7, token.length());

        UserEntity user = JwtUtil.getUser(httpServletRequest);
        user = userService.getUserByUserName(user.getUserName());
        Boolean bl = JwtUtil.verify(token,user.getUserName(),user.getId(),user.getVerify());
        return bl;

    }


    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        if (isLoginAttempt(request, response)) {
            try {
                if (executeLogin(request, response)){
                    return true;
                }
                response401(request, response);
            } catch (Exception e) {
                response401(request, response);
            }
        }else {
            response401(request, response);
        }
        return true;
    }

    /**
     * 非法请求 跳转登录界面
     */
    private JsonResult response401(ServletRequest req, ServletResponse resp) {
        HttpServletResponse response = (HttpServletResponse) resp;
        response.setStatus(401);
        return new JsonResult(ExceptionMessage.INVALID_USER);
    }
}

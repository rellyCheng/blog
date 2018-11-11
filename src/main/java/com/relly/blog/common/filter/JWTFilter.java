package com.relly.blog.common.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.relly.blog.common.exception.ExceptionMessage;
import com.relly.blog.common.model.JsonResult;
import com.relly.blog.utils.JwtUtil;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

public class JWTFilter extends BasicHttpAuthenticationFilter {

    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

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

        Subject subject = getSubject(request,response);
//        未登陆
        if (!subject.isAuthenticated()){
            return false;
        }

        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String authorization = httpServletRequest.getHeader("Authorization");
        authorization = authorization.substring(7, authorization.length());

        DecodedJWT jwt = JWT.decode(authorization);
        String username = jwt.getClaim("username").asString();
        String id = jwt.getClaim("id").asString();
        String password = jwt.getClaim("password").asString();

        Boolean bl = JwtUtil.verify(authorization,username,id,password);
        return bl ? true : false;

    }


    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        if (isLoginAttempt(request, response)) {
            try {
                if (executeLogin(request, response)){
                    return true;
                }else {
                    response401(request, response);
                }
            } catch (Exception e) {
                response401(request, response);
            }
        }else {
            response401(request, response);
        }
        return true;
    }

//    /**
//     * 对跨域提供支持
//     */
//    @Override
//    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
//        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
//        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
//        httpServletResponse.setHeader("Access-control-Allow-Origin", httpServletRequest.getHeader("Origin"));
//        httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS,PUT,DELETE");
//        httpServletResponse.setHeader("Access-Control-Allow-Headers", httpServletRequest.getHeader("Access-Control-Request-Headers"));
//        // 跨域时会首先发送一个option请求，这里我们给option请求直接返回正常状态
//        if (httpServletRequest.getMethod().equals(RequestMethod.OPTIONS.name())) {
//            httpServletResponse.setStatus(HttpStatus.OK.value());
//            return false;
//        }
//        return super.preHandle(request, response);
//    }

    /**
     * 非法请求 跳转登录界面
     */
    private JsonResult response401(ServletRequest req, ServletResponse resp) {
        HttpServletResponse response = (HttpServletResponse) resp;
        response.setStatus(401);
        return new JsonResult(ExceptionMessage.INVALID_USER);
    }
}

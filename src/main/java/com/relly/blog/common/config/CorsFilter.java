package com.relly.blog.common.config;


import com.relly.blog.entity.UserEntity;
import com.relly.blog.utils.JsonUtils;
import com.relly.blog.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;

/**
 * 跨域请求过滤类
 */
@Component
@Slf4j
public class CorsFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        //跨域支持
        response.setHeader("Access-Control-Allow-Origin", ((HttpServletRequest) req).getHeader("Origin"));
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", request.getHeader("Access-Control-Request-Headers"));
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String token = httpServletRequest.getHeader("Authorization");
        HttpServletRequestWrapper requestWrapper = new HttpServletRequestWrapper(request);
        if(token!=null&&token.equals("null")){
            UserEntity userEntity = JwtUtil.getUser(request);
            if(userEntity!=null){
                requestWrapper = new HttpServletRequestWrapper(request) {
                    @Override
                    public String[] getParameterValues(String name) {
                        if (name.equals("loginUserId")) {
                            return new String[] { userEntity.getId() };
                        }
                        return super.getParameterValues(name);
                    }
                    @Override
                    public Enumeration<String> getParameterNames() {
                        Set<String> paramNames = new LinkedHashSet<>();
                        paramNames.add("loginUserId");
                        Enumeration<String> names =  super.getParameterNames();
                        while(names.hasMoreElements()) {
                            paramNames.add(names.nextElement());
                        }
                        return Collections.enumeration(paramNames);
                    }

                    @Override
                    public ServletInputStream getInputStream() throws IOException {
                        byte[] requestBody = new byte[0];
                        try {
                            requestBody = StreamUtils.copyToByteArray(request.getInputStream());
                            Map map = JsonUtils.toMap(new String(requestBody));
                            map.put("loginUserId", userEntity.getId());
                            requestBody = JsonUtils.toJson(map).getBytes();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        final ByteArrayInputStream bais = new ByteArrayInputStream(requestBody);
                        return new ServletInputStream() {
                            @Override
                            public int read() throws IOException {
                                return bais.read();
                            }

                            @Override
                            public boolean isFinished() {
                                return false;
                            }

                            @Override
                            public boolean isReady() {
                                return true;
                            }

                            @Override
                            public void setReadListener(ReadListener listener) {

                            }
                        };
                    }
                };
            }
        }

        chain.doFilter(requestWrapper, res);
    }

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void destroy() {
    }
}
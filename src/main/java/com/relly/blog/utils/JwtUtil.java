package com.relly.blog.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.relly.blog.entity.UserEntity;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.Date;

public class JwtUtil {
    // 过期时间5分钟
    private static final long EXPIRE_TIME = 6000*60*1000;

    /**
     * 校验token是否正确
     * @param token 密钥
     * @param id 用户的id
     * @return 是否正确
     */
    public static boolean verify(String token, String username, String id,String password) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(id);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withClaim("username", username)
                    .withClaim("password", password)
                    .build();
            DecodedJWT jwt = verifier.verify(token);
            return true;
        } catch (Exception exception) {
            exception.printStackTrace();
            System.out.println(exception.getMessage());
            return false;
        }
    }

    /**
     * 获得token中的信息无需secret解密也能获得
     * @return token中包含的用户名
     */
    public static String getUsername(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim("username").asString();
        } catch (JWTDecodeException e) {
            return null;
        }
    }

    /**
     * 获得token中的信息无需secret解密也能获得
     * @return token中包含的用户名
     */
    public static String getUserId(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim("id").asString();
        } catch (JWTDecodeException e) {
            return null;
        }
    }
    /**
     * 生成签名,5min后过期
     * @param username 用户名
     * @param id 用户id
     * @return 加密的token
     */
    public static String sign(String username,String id,String password) {
        try {
            Date date = new Date(System.currentTimeMillis()+EXPIRE_TIME);
            Algorithm algorithm = Algorithm.HMAC256(id);
            // 附带username信息
            return JWT.create()
                    .withClaim("username", username)
                    .withClaim("id", id)
                    .withClaim("password", password)
                    .withExpiresAt(date)
                    .sign(algorithm);
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    /**
     *
     * 获取UserEntity
     * @author Thunder
     * @date 2018/8/2 10:28
     * @return com.jtg.baike.backweb.entity.SysUser
     */
    public static UserEntity getUser(HttpServletRequest request){
        String authorization = request.getHeader("Authorization");
        authorization = authorization.substring(7, authorization.length());
        DecodedJWT jwt = JWT.decode(authorization);
        String username = jwt.getClaim("username").asString();
        String id = jwt.getClaim("id").asString();
        UserEntity sysUser = new UserEntity();
        sysUser.setUserName(username);
        sysUser.setId(id);
        return sysUser;

    }
}

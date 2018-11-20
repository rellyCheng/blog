package com.relly.blog.utils;

import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MD5salt {
    private static final String SECRET = "J0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9";
    public static Map md5salt(String userName, String passWord){
        String hashAlgorithmName = "MD5";//加密方式
        ByteSource salt = ByteSource.Util.bytes(userName);//加盐
        int hashIterations = 2;//加密2次
        Object result = new SimpleHash(hashAlgorithmName,passWord,salt,hashIterations);
        Object verify = new SimpleHash(hashAlgorithmName,String.valueOf(result),SECRET,hashIterations);
        Map<String,String> map = new HashMap<>();
        map.put("pwd",String.valueOf(result));
        map.put("salt",String.valueOf(salt));
        map.put("verify",String.valueOf(verify));
        return map;
    }

    public static Map<String,String>  MD5File(String fileName){
        String hashAlgorithmName = "MD5";//加密方式
        ByteSource salt = ByteSource.Util.bytes(fileName);//加盐
        int hashIterations = 2;//加密2次
        Object result = new SimpleHash(hashAlgorithmName,fileName,salt,hashIterations);
        Map<String,String> map = new HashMap<>();
        map.put("md5file",String.valueOf(result));
        return map;
    }
}

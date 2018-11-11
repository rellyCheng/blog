package com.relly.blog.utils;


/**
 * 用于生成id的工具类
 *
 * @author Kartist 2018/8/14 15:04
 */
public class IdUtil {

    private static SnowFlakeGenerator snowFlakeGenerator;

    public static void setSnowFlakeGenerator(SnowFlakeGenerator snowFlakeGenerator) {
        IdUtil.snowFlakeGenerator = snowFlakeGenerator;
    }

    public static String randomId() {
        if (snowFlakeGenerator != null) {
            return snowFlakeGenerator.nextId();
        }
        return String.valueOf(System.currentTimeMillis());
    }

}
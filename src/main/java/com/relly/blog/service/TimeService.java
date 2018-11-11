package com.relly.blog.service;

import java.util.Date;

/**
 * @author Kartist 2018/8/18 20:17
 */
public interface TimeService {

    /**
     * 获取服务器统一时间
     * 对应分布式环境下不同服务器需要获取统一时间的场景
     *
     * @return
     */
    default Date getCurrentDate() {
        return new Date(System.currentTimeMillis());
    }

}

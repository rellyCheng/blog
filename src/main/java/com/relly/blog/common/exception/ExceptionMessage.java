package com.relly.blog.common.exception;

/**
 * 结果返回消息
 *
 * @author Yvan 2018/9/27 10:54
 */
public interface ExceptionMessage {
    String DATA_EXIST = "访问数据已存在";
    String DATA_NOT_EXIST = "访问数据不存在";
    String INVALID_OPERATION = "无效操作";
    String INVALID_USER = "无效用户";
}

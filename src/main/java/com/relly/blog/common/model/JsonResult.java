package com.relly.blog.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * 结果信息实体
 *
 * @author Yvan 2018/9/27 10:36
 */
@Data
@Builder
@AllArgsConstructor
public class JsonResult implements Serializable {

    public static final String SUCCESS = "OK";
    public static final String ERROR = "Fail";

    /**
     * 状态
     */
    private String state;
    /**
     * 对应状态的消息
     */
    private String message;
    /**
     * 具体业务数据
     */
    private Object data;

    /**
     * 此构造方法应用于data为null的场景
     */
    public JsonResult() {
        this.state = SUCCESS;
        this.message = "OK";
    }

    /**
     * 返回消息
     */
    public JsonResult(String message) {
        this();
        this.message = message;
    }

    /**
     * 有具体业务数据返回时,使用此构造方法
     */
    public JsonResult(Object data) {
        this();
        this.data = data;
    }

    /**
     * 出现异常以后要调用此方法封装异常信息
     */
    public JsonResult(Throwable t) {
        this.state = ERROR;
        this.message = t.getMessage();
    }

    /**
     * 出现异常以后要调用此方法封装异常信息
     * 返回异常信息的描述
     */
    public JsonResult(Throwable t, String data) {
        this.state = ERROR;
        this.message = t.getMessage();
        this.data = data;
    }

    /**
     * 业务数据的构造方法
     * 指定状态码
     *
     * @param state   状态码
     * @param message 业务数据
     */
    public JsonResult(String state, String message) {
        this.state = state;
        this.message = message;
    }


}

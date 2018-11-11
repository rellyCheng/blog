package com.relly.blog.common.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ServiceException extends RuntimeException {

    /**
     * 异常代码
     */
    private String errorCode;

    /**
     * 记录疑似问题的原因,不返回到前端,仅输出日志用
     */
    private String causeStr;

    /**
     * 产生原因的对象数组
     */
    private Object[] causeObj;

    public ServiceException() {
        super();
    }

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String message, Exception e) {
        super(message, e);
    }

    public ServiceException(String message, String causeStr, Object... causeObj) {
        super(message);
        this.causeStr = causeStr;
        this.causeObj = causeObj;
    }


}

package com.relly.blog.entity;

import com.relly.blog.utils.IdUtil;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Kartist 2018/8/13 21:00
 */
@Data
public class BaseEntity implements Serializable {

    protected String id;

    /**
     * 如果当前实体没有id,则随机创建一个id
     * 如果id已存在,则返回已存在的id
     *
     * @return 当前实体的id
     */
    public String initEntityId() {
        if (this.id == null) {
            this.id = String.valueOf(IdUtil.randomId());
            return String.valueOf(this.id);
        }
        return String.valueOf(this.id);
    }

}

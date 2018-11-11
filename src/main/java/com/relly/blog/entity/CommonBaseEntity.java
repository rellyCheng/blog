package com.relly.blog.entity;

import com.relly.blog.service.TimeService;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @author Kartist 2018/8/14 20:59
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CommonBaseEntity extends BaseEntity {

    /**
     * 若不初始化timeService 则使用当前的系统时间
     */
    private static TimeService timeService;
    /**
     * 创建时间 (服务器时间)
     */
    protected Date createTime;
    /**
     * 更新时间 (数据库时间)
     */
    protected Date updateTime;
    /**
     * 创建用户的id
     */
    protected String createUser;
    /**
     * 更新用户的id
     */
    protected String updateUser;
    /**
     * 是否已删除
     */
    protected Boolean delete;

    public CommonBaseEntity() {
    }

    public static void setTimeService(TimeService timeService) {
        CommonBaseEntity.timeService = timeService;
    }

    /**
     * 初始化用户的属性
     * 同时设置entity的创建用户和更新用户
     * 如果传入的user == null
     * 则createUser  updateUser 都 会设置为null
     */
    public void initUserFiled(UserEntity user) {
        if (user == null) {
            this.createUser = null;
            this.updateUser = null;
            return;
        }
        final String userId = user.getId();
        this.createUser = userId;
        this.updateUser = userId;
    }

    /**
     * 初始化用户属性,以及实体的id
     *
     * @param user 用户对象
     * @return 初始化后的实体id
     */
    public String initBaseFiled(UserEntity user) {
        this.delete = false;
        if (timeService != null) {
            createTime = timeService.getCurrentDate();
        } else {
            createTime = new Date();
        }
        initUserFiled(user);
        return initEntityId();
    }
}


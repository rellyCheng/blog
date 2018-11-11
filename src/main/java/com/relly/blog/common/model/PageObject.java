package com.relly.blog.common.model;

import lombok.Data;

/**
 * @author Kartist 2018/8/15 11:04
 */
@Data
public class PageObject {
    /**
     * 当前页面
     */
    protected Integer pageCurrent = 1;
    /**
     * 每页数量
     */
    protected Integer pageSize;
    /**
     * 当前页面的第一条数据,对应在数据库中的序号
     * 计算公式:startIndex = (pageCurrent-1)*pageSize
     */
    protected Integer startIndex;
    /**
     * 数据总条数
     */
    protected Integer rowCount;
    /**
     * 总共有多少页
     * 根据rowCount和pageSize计算
     * totalPage = (rowCount / pageSize) +1
     */
    protected Integer totalPage;


    /**
     * 用于在业务层中方便地创建分页对象
     *
     * @param pageCurrent 第几页 值小于1时 , 默认为1
     * @param pageSize    查询的数量 值小于1时 默认为1
     * @param rowCount    数据总条数 值小于0时,默认为0
     */
    public PageObject(int pageCurrent, int pageSize, int rowCount) {
        //保证当前页和每页大小最小值为1
        if (pageCurrent < 1) {
            pageCurrent = 1;
        }
        if (pageSize < 1) {
            pageSize = 1;
        }
        if (rowCount < 0) {
            rowCount = 0;
        }
        this.pageCurrent = pageCurrent;
        this.pageSize = pageSize;
        this.rowCount = rowCount;
        this.startIndex = (pageCurrent - 1) * pageSize;
        this.totalPage = (rowCount + pageSize - 1) / pageSize;
    }

    /**
     * 判断是否可能查不到数据
     * 便于进一步处理
     *
     * @return 判断结果 分页查询结果是否为空集合
     */
    public boolean willCauseEmptyList() {
        return totalPage < pageCurrent;
    }

}

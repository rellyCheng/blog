package com.relly.blog.common.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kartist 2018/8/17 18:52
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PageResult<T> extends PageObject {

    /**
     * 用于保存分页查询的数据
     */
    private List<T> pageData = new ArrayList<>(1);

    /**
     * 用于在业务层中方便地创建分页对象
     *
     * @param pageCurrent 第几页
     * @param pageSize    查询的数量
     * @param rowCount    数据总条数
     */
    public PageResult(int pageCurrent, int pageSize, int rowCount) {
        super(pageCurrent, pageSize, rowCount);
    }

    /**
     * 通过PageObject构造
     *
     * @param pageObject pageObject
     */
    public PageResult(PageObject pageObject) {
        super(pageObject.getPageCurrent(), pageObject.getPageSize(), pageObject.getRowCount());
    }
}

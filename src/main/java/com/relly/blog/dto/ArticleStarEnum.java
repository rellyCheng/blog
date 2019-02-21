package com.relly.blog.dto;

public enum ArticleStarEnum {
    CANCLE(0,"cancle"),
    STAR(1,"star"),;
    private Integer key;
    private String value;

    ArticleStarEnum(Integer key, String value) {
        this.key = key;
        this.value = value;
    }

    public Integer getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}

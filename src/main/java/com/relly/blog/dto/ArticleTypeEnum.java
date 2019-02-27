package com.relly.blog.dto;

public enum ArticleTypeEnum {
    IT(0,"文集"),
    FINANCE(1,"科学理论"),
    TECHNOLOGY(2,"每日英语"),
    SPORT(3,"学习小技巧"),
    CAR(4,"生活感悟"),
    EIGHT(5,"随笔"),
    MILITARY(6,"体育"),
    ESSAY(7,"其他"),
   ;

    private Integer key;
    private String value;

    ArticleTypeEnum(Integer key, String value) {
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

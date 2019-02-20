package com.relly.blog.dto;

public enum ArticleTypeEnum {
    IT(0,"IT"),
    FINANCE(1,"财经"),
    TECHNOLOGY(2,"科技"),
    SPORT(3,"体育"),
    CAR(4,"汽车"),
    EIGHT(5,"八卦"),
    MILITARY(6,"军事"),
    ESSAY(7,"随笔"),
    Other(8,"其他"),
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

package com.relly.blog.dto;

/**
 * 消息提醒类型
 * @author Relly
 * @date 2018/11/29 16:30
 * @return
 */
public enum NoticeTypeEnum {

    MESSAGE(0, "message"),

    NOTIFICATION(1, "notification"),

    EVENT(2,"event");

    private Integer state;

    private String message;

    NoticeTypeEnum(Integer state, String message) {
        this.message = message;
        this.state = state;
    }

    /**
     * 根据state值获取message
     * @param state
     * @return
     */
    public String getMessage(Integer state) {
        for (NoticeTypeEnum notice: NoticeTypeEnum.values()) {
            if (notice.getState().equals(state)) {
                return notice.getMessage();
            }
        }
        return null;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

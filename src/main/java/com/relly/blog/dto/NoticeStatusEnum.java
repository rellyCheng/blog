package com.relly.blog.dto;

public enum NoticeStatusEnum {

    TODO("todo", "未开始"),

    PROCESSING("processing", "进行中"),

    URGENT("urgent","马上到期"),

    OVERDUE("overdue","已过期"),

    END("end","已结束");
    private String state;

    private String message;

    NoticeStatusEnum(String state, String message) {
        this.message = message;
        this.state = state;
    }

    /**
     * 根据state值获取message
     * @param state
     * @return
     */
    public String getMessage(String state) {
        for (NoticeStatusEnum notice: NoticeStatusEnum.values()) {
            if (notice.getState().equals(state)) {
                return notice.getMessage();
            }
        }
        return null;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

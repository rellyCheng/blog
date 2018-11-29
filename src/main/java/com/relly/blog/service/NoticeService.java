package com.relly.blog.service;

import com.relly.blog.entity.NoticeEntity;

import java.util.List;

public interface NoticeService {
    void addNotice(NoticeEntity noticeEntity);

    List<NoticeEntity> getNoticeList(String userId);
}

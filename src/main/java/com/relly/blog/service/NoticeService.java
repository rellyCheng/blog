package com.relly.blog.service;

import com.relly.blog.dto.NoticeDTO;
import com.relly.blog.entity.NoticeEntity;

import java.util.List;

public interface NoticeService {
    void addNotice(NoticeEntity noticeEntity);

    List<NoticeDTO> getNoticeList(String userId);
}

package com.relly.blog.service.impl;

import com.relly.blog.entity.NoticeEntity;
import com.relly.blog.mapper.NoticeMapper;
import com.relly.blog.service.NoticeService;
import com.relly.blog.utils.IdUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class NoticeServiceImpl implements NoticeService {

    @Resource
    private NoticeMapper noticeMapper;
    @Override
    public void addNotice(NoticeEntity noticeEntity) {
            noticeEntity.setId(IdUtil.randomId());
            noticeMapper.insertSelective(noticeEntity);
    }

    @Override
    public List<NoticeEntity> getNoticeList(String userId) {

        List<NoticeEntity> list = noticeMapper.getNoticeList(userId);
        return list;
    }
}

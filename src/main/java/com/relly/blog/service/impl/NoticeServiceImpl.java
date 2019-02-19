package com.relly.blog.service.impl;

import com.relly.blog.dto.NoticeDTO;
import com.relly.blog.dto.NoticeStatusEnum;
import com.relly.blog.dto.NoticeTypeEnum;
import com.relly.blog.entity.NoticeEntity;
import com.relly.blog.entity.UserDetailEntity;
import com.relly.blog.entity.UserEntity;
import com.relly.blog.mapper.NoticeMapper;
import com.relly.blog.mapper.UserDetailMapper;
import com.relly.blog.mapper.UserMapper;
import com.relly.blog.service.NoticeService;
import com.relly.blog.utils.IdUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.xml.crypto.Data;
import java.util.Date;
import java.util.List;

@Service
public class NoticeServiceImpl implements NoticeService {

    @Resource
    private NoticeMapper noticeMapper;

    @Resource
    private UserDetailMapper userDetailMapper;
    @Override
    public void addNotice(NoticeEntity noticeEntity) {
            noticeEntity.setId(IdUtil.randomId());
            if(noticeEntity.getDatetime()==null){
                noticeEntity.setDatetime(new Date());
            }
            noticeMapper.insertSelective(noticeEntity);
    }

    @Override
    public List<NoticeDTO> getNoticeList(String userId) {
        List<NoticeDTO> list = noticeMapper.getNoticeList(userId);
        UserDetailEntity userDetailEntity;
        Date currentTime = new Date();
        for (NoticeDTO noticeDTO:list) {
            if (noticeDTO.getSendId()!=null){
                userDetailEntity = userDetailMapper.selectByUserId(noticeDTO.getSendId());
                if (userDetailEntity.getAvatar()!=null){
                    noticeDTO.setAvatar(userDetailEntity.getAvatar());
                }
            }
            if (noticeDTO.getType().equals(NoticeTypeEnum.EVENT.getMessage())){
                //当前时间在开始时间之前
                if (currentTime.compareTo(noticeDTO.getDatetime())<0){
                    noticeDTO.setExtra(NoticeStatusEnum.TODO.getMessage());
                    noticeDTO.setStatus(NoticeStatusEnum.TODO.getState());

                }
                //没有结束时间为无期限 注意:当代办任务完成时deadline更新为完成时间
                else if (noticeDTO.getDeadline()==null){
                    noticeDTO.setExtra(NoticeStatusEnum.PROCESSING.getMessage());
                    noticeDTO.setStatus(NoticeStatusEnum.PROCESSING.getState());
                }
                //结束时间在当前时间之前
                else if (noticeDTO.getDeadline().compareTo(currentTime)<0){
                    noticeDTO.setExtra(NoticeStatusEnum.END.getMessage());
                    noticeDTO.setStatus(NoticeStatusEnum.END.getState());
                }
                //当前时间在结束时间之前
                else {
                    //相差的天数
                    int days = (int) ((noticeDTO.getDeadline().getTime() - currentTime.getTime()) / (1000*3600*24));
                    if(days>3){
                        noticeDTO.setExtra(NoticeStatusEnum.PROCESSING.getMessage());
                        noticeDTO.setStatus(NoticeStatusEnum.PROCESSING.getState());
                    }else {
                        noticeDTO.setExtra(NoticeStatusEnum.URGENT.getMessage());
                        noticeDTO.setStatus(NoticeStatusEnum.URGENT.getState());
                    }

                }
            }
        }
        return list;
    }

    @Override
    public void clearNotices(String userId, int type) {
        //查询用户未读的notice
       List<NoticeEntity> noticeEntityList =  noticeMapper.getNoticeListByUserIdAndType(userId,type);
       NoticeEntity notice = new NoticeEntity();
       for (NoticeEntity noticeEntity:noticeEntityList){
           notice.setId(noticeEntity.getId());
           notice.setIsRead(1);
           noticeMapper.updateByPrimaryKeySelective(notice);
       }
    }
}

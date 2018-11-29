package com.relly.blog.controller;

import com.relly.blog.common.model.JsonResult;
import com.relly.blog.entity.NoticeEntity;
import com.relly.blog.entity.UserEntity;
import com.relly.blog.service.NoticeService;
import com.relly.blog.utils.JwtUtil;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/notice/")
@Validated
public class NoticeController {

    @Resource
    private NoticeService noticeService;

    @PostMapping("addNotice")
    public JsonResult addNotice(@RequestBody NoticeEntity noticeEntity){
        noticeService.addNotice(noticeEntity);
        return new JsonResult();
    }

    @PostMapping("getNoticeList")
    public JsonResult getNoticeList(HttpServletRequest request){
        UserEntity currentUser = JwtUtil.getUser(request);
        List<NoticeEntity> list  = noticeService.getNoticeList(currentUser.getId());
        return new JsonResult(list);
    }
}

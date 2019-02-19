package com.relly.blog.controller;

import com.relly.blog.common.model.JsonResult;
import com.relly.blog.dto.NoticeDTO;
import com.relly.blog.dto.NoticeTypeEnum;
import com.relly.blog.entity.NoticeEntity;
import com.relly.blog.entity.UserEntity;
import com.relly.blog.service.NoticeService;
import com.relly.blog.utils.JwtUtil;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
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
        List<NoticeDTO> list  = noticeService.getNoticeList(currentUser.getId());
        return new JsonResult(list);
    }

    @PostMapping("clearNotices")
    public JsonResult clearNotices(HttpServletRequest request,@RequestParam("type")@NotNull int type){
        UserEntity currentUser = JwtUtil.getUser(request);
        noticeService.clearNotices(currentUser.getId(),type);
        return new JsonResult();
    }
}

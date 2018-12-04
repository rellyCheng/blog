package com.relly.blog.api;


import com.relly.blog.common.exception.ServiceException;
import com.relly.blog.common.model.JsonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/publicApi/")
@Slf4j
public class PublicController {
    @RequestMapping("test")
    public JsonResult logout(){
//        if(true){
//            throw new ServiceException("报错啦");
//        }
        return new JsonResult();
    }

}

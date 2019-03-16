package com.relly.blog.controller;

import com.relly.blog.common.exception.ServiceException;
import com.relly.blog.common.model.JsonResult;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * @Author Relly
 * @CreteTime 2019-03-09 12:14
 * @Description
 */
@RestController
@RequestMapping("/api/elasticSearch/")
public class ESController {


    @Resource
    private TransportClient client;

    @RequestMapping("add")
    public JsonResult add(@RequestParam("title") String title,
                          @RequestParam("owner") String owner){

        try {
            XContentBuilder content = XContentFactory.jsonBuilder()
                    .startObject()
                    .field("title",title)
                    .field("owner",owner)
                    .endObject();
            IndexResponse result = this.client.prepareIndex("book","article")
                    .setSource(content)
                    .get();
            return new JsonResult(result.getId());
        } catch (IOException e) {
            e.printStackTrace();
            throw new ServiceException("服务器错误");
        }
    }

    @GetMapping("get")
    public JsonResult get(@RequestParam("id") String id){
        if (id.isEmpty()){
            throw new ServiceException("不存在");
        }
        GetResponse result = this.client.prepareGet("book","article",id)
                .get();
        if (!result.isExists()){
            throw new ServiceException("不存在");
        }

        return new JsonResult(result.getSource());
    }

    @PostMapping("del")
    public JsonResult del(@RequestParam("id") String id){
        DeleteResponse response = this.client.prepareDelete("book","article",id).get();

        return new JsonResult(response.getResult().toString());
    }

    @PostMapping("update")
    public JsonResult update(@RequestParam("id") String id,
                             @RequestParam(value = "title",required = false) String title,
                             @RequestParam(value = "owner",required = false) String owner){
        UpdateRequest updateRequest = new UpdateRequest("book","article",id);
        try {
            XContentBuilder builder = XContentFactory.jsonBuilder()
                    .startObject();
            if (!title.isEmpty()){
                builder.field("title",title);
            }
            if (!owner.isEmpty()){
                builder.field("owner",owner);
            }
            builder.endObject();
            updateRequest.doc(builder);


        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException("服务器报错！");
        }

        UpdateResponse response = null;
        try {
            response = this.client.update(updateRequest).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return new JsonResult(response.getResult());
    }

}

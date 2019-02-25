package com.relly.blog.controller;

import com.alibaba.fastjson.JSONObject;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.relly.blog.common.exception.ServiceException;
import com.relly.blog.common.model.JsonResult;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.validation.constraints.NotNull;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Random;

@RestController
@RequestMapping("/api/qiNiu/")
public class QiNiuController {
    //AK
    public static final String ACCESS_KEY = "XNiqIfIlhS-KSDiltyq2cESdDpthJXonvOge16bO";
    //SK
    public static final String SECRET_KEY = "6jYcSS8mXN3Bv7EOs4KcPGJMvfXdpxeE6J10bTu9";

    public static final String BUCKET_NAME = "relly";

    Configuration cfg = new Configuration(Zone.zone0());
    Auth auth = Auth.create(ACCESS_KEY, SECRET_KEY);
    UploadManager uploadManager = new UploadManager(cfg);
    /**
     * 获取凭证
     * @param bucketName 空间名称
     * @param key 如果需要覆盖上传则设置此参数
     * @return
     */
    public String getUpToken(String bucketName,String key) {
        return auth.uploadToken(bucketName);
    }

    /**
     * 上传原图
     * @param file 文件  （也可以是字节数组、或者File对象）
     * key 上传到七牛上的文件的名称  （同一个空间下，名称【key】是唯一的）
     */
    @RequestMapping("upload")
    public JsonResult upload(@RequestParam @NotNull MultipartFile file) {
        Random ra =new Random();
        String key = new Date().getTime()+String.valueOf(ra.nextInt());
        try {
            // 调用put方法上传
             InputStream inputStream = file.getInputStream();
            Response res = uploadManager.put(inputStream, key, getUpToken(BUCKET_NAME, key),null,null);
            // 打印返回的信息
            System.out.println(res.bodyString());
            JSONObject jsStr = JSONObject.parseObject(res.bodyString());
            return new JsonResult(jsStr);
        } catch (QiniuException e) {
            throw new ServiceException("上传失败!");
        } catch (IOException e) {
            e.printStackTrace();
             throw new ServiceException("上传失败!");
        }
    }


    /**
     * 上传处理过的图片
     * @param file
     * @return
     */

    @RequestMapping("upload1")
    public JsonResult upload1(@RequestParam @NotNull MultipartFile file) {
        Random ra =new Random();
        String key = new Date().getTime()+String.valueOf(ra.nextInt());
        try {
            // 调用put方法上传
            InputStream inputStream = file.getInputStream();
            Response res = uploadManager.put(handleImage(inputStream), key, getUpToken(BUCKET_NAME, key));
            // 打印返回的信息
            System.out.println(res.bodyString());
            JSONObject jsStr = JSONObject.parseObject(res.bodyString());
            return new JsonResult(jsStr);
        } catch (QiniuException e) {
            throw new ServiceException("上传失败!");
        } catch (IOException e) {
            e.printStackTrace();
            throw new ServiceException("上传失败!");
        }
    }


    /**
     * 处理图片
     * @param inputStream
     * @return
     */
    public byte[]  handleImage(InputStream inputStream){

        try {
            BufferedImage image = Thumbnails.of(inputStream)
                    .scale(0.5f).asBufferedImage();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(image, "png", out);
            byte[] b = out.toByteArray();
            return b;
        } catch (IOException e) {
            e.printStackTrace();
            throw new ServiceException("图片处理异常！");
        }
    }
}

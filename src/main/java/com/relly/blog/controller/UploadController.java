package com.relly.blog.controller;

import com.relly.blog.common.exception.ServiceException;
import com.relly.blog.common.model.JsonResult;
import com.relly.blog.utils.FileUtil;
import com.relly.blog.utils.MD5salt;
import com.relly.blog.vo.UploadFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/upload")
public class UploadController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UploadController.class);


    /**
     *
     * 单个文件上传
     * @author Relly
     * @date 2018/11/20 13:55
     * @param file
     * @param request
     * @return com.relly.blog.common.model.JsonResult
     */
    @PostMapping("/singleUpload")
    public JsonResult upload(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        if (file.isEmpty()) {
            throw new ServiceException("上传文件失败,请选择文件");
        }
        String fileName = file.getOriginalFilename();
        //获取后缀
        int begin=fileName.indexOf(".");
        int last=fileName.length();
        String back = fileName.substring(begin,last);
        //加密文件名称
        Map<String,String> map = MD5salt.MD5File(fileName);
        fileName = map.get("md5file")+back;
        String filePath = request.getSession().getServletContext().getRealPath("uploadFile/");
        try {
            FileUtil.uploadFile(file.getBytes(), filePath, fileName);
        } catch (Exception e) {
            throw new ServiceException(""+e+"");
        }
        Map<String,Object> fileMap = new HashMap<>();
        fileMap.put("originName",file.getOriginalFilename());
        fileMap.put("filePath",filePath+fileName);
        return new JsonResult(fileMap);
    }

    /**
     *
     * 多个文件上传
     * @author Relly
     * @date 2018/11/20 13:56
     * @param request
     * @return com.relly.blog.common.model.JsonResult
     */
    @PostMapping("/multiUpload")
    @ResponseBody
    public JsonResult multiUpload(HttpServletRequest request) {
        List<MultipartFile> files = ((MultipartHttpServletRequest) request).getFiles("file");
        String filePath = request.getSession().getServletContext().getRealPath("uploadFile/");
        List<UploadFile> fileList = new ArrayList<>();
        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);
            if (file.isEmpty()) {
                throw new ServiceException("上传文件失败!请选择文件!");
            }
            String fileName = file.getOriginalFilename();
            try {
                FileUtil.uploadFile(file.getBytes(), filePath, fileName);
            } catch (Exception e) {
                throw new ServiceException(""+e+"");
            }
            UploadFile uploadFile = UploadFile.builder()
                    .filePath(filePath+fileName)
                    .originName(file.getOriginalFilename())
                    .build();
            fileList.add(uploadFile);
        }
        return new JsonResult(fileList);
    }

}

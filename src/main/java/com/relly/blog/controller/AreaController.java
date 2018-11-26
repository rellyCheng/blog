package com.relly.blog.controller;

import com.relly.blog.common.model.JsonResult;
import com.relly.blog.entity.CityEntity;
import com.relly.blog.entity.ProvinceEntity;
import com.relly.blog.service.AreaService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author Relly
 * @CreteTime 2018/11/26 10:54 PM
 * @Description
 */
@RestController
@RequestMapping("/api/geographic/")
@Validated
public class AreaController {

    @Resource
    private AreaService areaService;

    @PostMapping("province")
    public JsonResult getAllProvinceList(){
        List<ProvinceEntity> list = areaService.getAllProvinceList();
        return new JsonResult(list);
    }

    @PostMapping("city")
    public JsonResult getCityListByPId(String pid){
        List<CityEntity> list = areaService.getCityListByPId(pid);
        return new JsonResult(list);
    }
}

package com.relly.blog.service;

import com.relly.blog.entity.CityEntity;
import com.relly.blog.entity.ProvinceEntity;

import java.util.List;

/**
 * @Author Relly
 * @CreteTime 2018/11/26 10:55 PM
 * @Description
 */
public interface AreaService {
    List<ProvinceEntity> getAllProvinceList();

    List<CityEntity> getCityListByPId(String pid);
}

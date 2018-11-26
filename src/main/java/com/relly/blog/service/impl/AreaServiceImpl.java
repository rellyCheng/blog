package com.relly.blog.service.impl;

import com.relly.blog.entity.CityEntity;
import com.relly.blog.entity.ProvinceEntity;
import com.relly.blog.mapper.CityMapper;
import com.relly.blog.mapper.ProvinceMapper;
import com.relly.blog.service.AreaService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author Relly
 * @CreteTime 2018/11/26 10:55 PM
 * @Description
 */
@Service
public class AreaServiceImpl implements AreaService {

    @Resource
    private ProvinceMapper provinceMapper;
    @Resource
    private CityMapper cityMapper;


    @Override
    public List<ProvinceEntity> getAllProvinceList() {
        List<ProvinceEntity> list = provinceMapper.getAllProvinceList();
        return list;
    }

    @Override
    public List<CityEntity> getCityListByPId(String pid) {
        List<CityEntity> list = cityMapper.getCityListByPId(pid);
        return list;
    }
}

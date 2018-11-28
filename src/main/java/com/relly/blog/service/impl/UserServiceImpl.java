package com.relly.blog.service.impl;

import com.relly.blog.common.model.PageResult;
import com.relly.blog.dto.UserDTO;
import com.relly.blog.dto.UserDetailDTO;
import com.relly.blog.entity.UserDetailEntity;
import com.relly.blog.entity.UserEntity;
import com.relly.blog.mapper.UserDetailMapper;
import com.relly.blog.mapper.UserMapper;
import com.relly.blog.service.UserService;
import com.relly.blog.utils.ConvertUtils;
import com.relly.blog.utils.IdUtil;
import com.relly.blog.utils.MD5salt;
import com.relly.blog.vo.City;
import com.relly.blog.vo.Geographic;
import com.relly.blog.vo.Province;
import com.relly.blog.vo.Tags;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {
    @Resource
    private UserMapper userMapper;
    @Resource
    private UserDetailMapper userDetailMapper;
    @Override
    public UserEntity getUserByUserName(String userName) {
        UserEntity userEntity = userMapper.getUserByUserName(userName);
        return userEntity;
    }

    @Override
    public void addUser(UserEntity currentUser,UserDTO userDTO) {
        Map<String,String> map = MD5salt.md5salt(userDTO.getUserName(),userDTO.getPassword());
        UserEntity userEntity = UserEntity.builder()
                .userName(userDTO.getUserName())
                .password(map.get("pwd"))
                .salt(map.get("salt"))
                .verify(map.get("verify"))
                .createUser(currentUser.getId())
                .id(IdUtil.randomId())
                .createTime(new Date())
                .updateUser(currentUser.getId())
                .name(userDTO.getName())
                .bgColor(userDTO.getBgColor())
                .isDelete(0)
                .build();
        userMapper.insert(userEntity);
    }

    @Override
    public PageResult<UserDTO> getUserList(int pageSize, int pageCurrent) {
        int rowCount = userMapper.getUserListCount();
        PageResult<UserDTO> pageResult = new PageResult<>(pageCurrent, pageSize, rowCount);
        if (pageResult.willCauseEmptyList()) {
            return pageResult;
        }
        List<UserDTO> List = userMapper.getUserList(pageResult);
        pageResult.setPageData(List);
        return pageResult;
    }

    @Override
    public void updateUserDetail(UserDetailDTO userDetailDTO) {
        UserDetailEntity u = new UserDetailEntity();
        u = (UserDetailEntity) ConvertUtils.populate(userDetailDTO,u);
        u.setProvince(userDetailDTO.getGeographic().getProvince().getLabel());
        u.setProvinceKey(userDetailDTO.getGeographic().getProvince().getKey());
        u.setCity(userDetailDTO.getGeographic().getCity().getLabel());
        u.setCityKey(userDetailDTO.getGeographic().getCity().getKey());
        int count = userDetailMapper.countUserDetailByUserId(userDetailDTO.getUserId());
        if (count==0){
            u.setId(IdUtil.randomId());
            userDetailMapper.insert(u);
        }else {
            u.setId(userDetailMapper.getUserDetailByUserId(userDetailDTO.getUserId()).getId());
            userDetailMapper.updateByPrimaryKeySelective(u);
        }
    }

    @Override
    public UserDetailDTO getUserDetail(String id) {
        UserDetailDTO userDetailDTO = userMapper.getUserDetail(id);
        Province province = Province.builder()
                .key(userDetailDTO.getProvinceKey())
                .label(userDetailDTO.getProvince())
                .build();
        City city = City.builder()
                .key(userDetailDTO.getCityKey())
                .label(userDetailDTO.getCity())
                .build();
        Geographic geographic = Geographic.builder()
                .city(city)
                .province(province)
                .build();
        userDetailDTO.setGeographic(geographic);
        userDetailDTO.setNotifyCount(10);
        String tags = userDetailDTO.getTags();

        String[] strs = tags.split(",");
        List<Tags> list = new ArrayList<>();
        for (int i = 0; i < strs.length ; i++) {
            Tags tags1 = new Tags();
            tags1.setKey(i);
            tags1.setLabel(strs[i]);
            list.add(tags1);
        }
        userDetailDTO.setTagsList(list);
        return userDetailDTO;
    }

    @Override
    public void updateTags(String currentUserId,String tag) {
        UserDetailEntity userDetailEntity = userDetailMapper.getUserDetailByUserId(currentUserId);
        String tags = userDetailEntity.getTags();
        tags = tags+","+tag;
        userDetailEntity.setTags(tags);
        userDetailMapper.updateByPrimaryKeySelective(userDetailEntity);
    }

    @Override
    public List getUserListByRole(String roleId) {
        List<String> list = userMapper.getUserListByRole(roleId);
        return list;
    }

    @Override
    public List getAllUserList() {
        List<String> list = userMapper.getAllUserList();
        return null;
    }

}

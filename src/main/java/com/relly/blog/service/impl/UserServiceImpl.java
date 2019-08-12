package com.relly.blog.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.relly.blog.common.exception.ServiceException;
import com.relly.blog.common.model.PageResult;
import com.relly.blog.dto.AllUserDTO;
import com.relly.blog.dto.UserDTO;
import com.relly.blog.dto.UserDetailDTO;
import com.relly.blog.dto.UserRegisterDTO;
import com.relly.blog.entity.UserDetailEntity;
import com.relly.blog.entity.UserEntity;
import com.relly.blog.entity.WxUserEntity;
import com.relly.blog.mapper.*;
import com.relly.blog.service.UserService;
import com.relly.blog.utils.ConvertUtils;
import com.relly.blog.utils.IdUtil;
import com.relly.blog.utils.JwtUtil;
import com.relly.blog.utils.MD5salt;
import com.relly.blog.utils.httpclient.HttpClientUtils;
import com.relly.blog.utils.httpclient.core.HttpRequestConfig;
import com.relly.blog.utils.httpclient.core.HttpRequestResult;
import com.relly.blog.vo.City;
import com.relly.blog.vo.Geographic;
import com.relly.blog.vo.Province;
import com.relly.blog.vo.Tags;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

@Service
public class UserServiceImpl implements UserService {
    @Resource
    private UserMapper userMapper;
    @Resource
    private UserDetailMapper userDetailMapper;
    @Resource
    private NoticeMapper noticeMapper;
    @Resource
    private JavaMailSender javaMailSender;
    @Resource
    private PermissionMapper permissionMapper;

    @Value("${spring.mail.username}")
    private String from;

    @Value("${apiAddress}")
    private String apiAddress;

    @Resource
    private WxUserMapper wxUserMapper;

    @Override
    public UserEntity getUserByUserName(String userName) {
        UserEntity userEntity = userMapper.getUserByUserName(userName);
        return userEntity;
    }

    @Override
    public UserEntity getUserByOpenId(String openid) {
        UserEntity userEntity = userMapper.getUserByOpenId(openid);
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
        //省市区
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

        //获取通知条数
        int noticeCount = noticeMapper.getNoticeCountByUserId(id);
        userDetailDTO.setNotifyCount(noticeCount);


        String tags = userDetailDTO.getTags();
        if (tags!=null){
            String[] strs = tags.split(",");
            List<Tags> list = new ArrayList<>();
            for (int i = 0; i < strs.length ; i++) {
                Tags tags1 = new Tags();
                tags1.setKey(i);
                tags1.setLabel(strs[i]);
                list.add(tags1);
            }
            userDetailDTO.setTagsList(list);
        }

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
    public List<String> getUserListByRole(String roleId) {
        List<String> list = userMapper.getUserListByRole(roleId);
        return list;
    }

    @Override
    public List<AllUserDTO> getAllUserList() {
        List<AllUserDTO> list = userMapper.getAllUserList();
        return list;
    }

    @Override
    @Transactional
    public String register(UserRegisterDTO userRegisterDTO) {
        UserEntity u = userMapper.getUserByUserName(userRegisterDTO.getEmail());
        if (u!=null){
            throw new ServiceException("当前邮箱已被注册！");
        }

        Map<String,String> map = MD5salt.md5salt(userRegisterDTO.getUserName(),userRegisterDTO.getPassword());
        String userId = IdUtil.randomId();
        u = new UserEntity();
        u = (UserEntity) ConvertUtils.populate(userRegisterDTO,u);
        u.setId(userId);
        u.setCreateTime(new Date());
        u.setUserName(userRegisterDTO.getUserName());
        u.setUpdateUser("register");
        u.setCreateUser("register");
        u.setSalt(map.get("salt"));
        u.setPassword(map.get("pwd"));
        u.setVerify(map.get("verify"));
        u.setIsDelete(1);
        userMapper.insertSelective(u);

        UserDetailEntity userDetailEntity = UserDetailEntity.builder()
                .id(IdUtil.randomId())
                .userId(userId)
                .email(userRegisterDTO.getEmail())
                .city(userRegisterDTO.getCity())
                .cityKey(userRegisterDTO.getCityKey())
                .province(userRegisterDTO.getProvince())
                .provinceKey(userRegisterDTO.getProvinceKey())
                .ipAddress(userRegisterDTO.getIpAddress())
                .country(userRegisterDTO.getCountry())
                .tags("All Star")
                .build();
        userDetailMapper.insertSelective(userDetailEntity);


        //发送激活邮件
        String title = "注册All In All，验证邮箱，激活账号";
        String url = "点击链接激活账号"+apiAddress+"/publicApi/activation?verify="+map.get("verify");
        sendMail(title,url,userRegisterDTO.getEmail());

        String jwtToken = JwtUtil.sign(userRegisterDTO.getUserName(),userId,map.get("verify"));
        return jwtToken;

    }

    @Async
    @Override
    public void sendMail(String title, String url, String email) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from); // 发送人的邮箱
        message.setSubject(title); //标题
        message.setTo(email); //发给谁  对方邮箱
        message.setText(url); //内容
        javaMailSender.send(message); //发送
    }

    @Override
    public void activation(String verify) {
        UserEntity userEntity = userMapper.getUserByVerify(verify);
        if (userEntity!=null){
            userEntity.setIsDelete(0);
            userMapper.updateByPrimaryKeySelective(userEntity);
        }
    }

    /**
     *
     * 根据code获取github的token
     * @author Thunder
     * @date 2018/12/4 14:23
     * @param code
     * @return java.util.Map<java.lang.String,java.lang.Object>
     */
    @Override
    public Map<String, Object>  githubUser(String code) throws IOException {
        String client_id = "0806acecdaa7e16a61c0";
        String client_secret = "e6e5af2f22ad0a00cf259612140564433fd124b6";
        HttpRequestConfig config = HttpRequestConfig.create().url("https://github.com/login/oauth/access_token?code="+code+"&client_id="+client_id+"&client_secret="+client_secret);
        HttpRequestResult result = null;
        try {
            result = HttpClientUtils.post(config);
            String token  = result.getResponseText();
            System.out.println(token);
            Map<String, Object> map= getGithubUserInfo(token);
            return map;
        } catch (IOException e) {
            e.printStackTrace();
            throw new ServiceException();
        }
    }

    @Override
    @Transactional
    public void insertWxUser(WxUserEntity wxUserEntity) {

        try {
            wxUserMapper.insert(wxUserEntity);
        } catch (Exception e) {
            throw new ServiceException("注册失败");
        }

        if (wxUserEntity.getLoginUserId()!=null){
            UserEntity userEntity = userMapper.selectByPrimaryKey(wxUserEntity.getLoginUserId());
            userEntity.setOpenid(wxUserEntity.getOpenid());
            userMapper.updateByPrimaryKeySelective(userEntity);
        }
    }

    /**
     *
     * 根据github的token获取用户详细信息
     * @author Thunder
     * @date 2018/12/4 14:22
     * @param token
     * @return java.util.Map<java.lang.String,java.lang.Object>
     */
    public Map<String, Object> getGithubUserInfo(String token){
        HttpRequestConfig config = HttpRequestConfig.create().url("https://api.github.com/user?"+token);
        System.out.println("https://api.github.com/user?"+token);
        HttpRequestResult result = null;
        try {
            result = HttpClientUtils.get(config);
            System.out.println(result);
            JSONObject data = JSONObject.parseObject(result.getResponseText());
            //根据GitHub的登录名和id拼接用作用户名
            String userName = "github"+data.getString("id");
            UserEntity checkUser = userMapper.getUserByUserName(userName);
            String jwtToken;
            Map<String,Object> userMap = new HashMap<>(3);
            if (checkUser==null){
                String userId = IdUtil.randomId();
                Map<String,String> map = MD5salt.md5salt(userName,"123123");
                UserEntity userEntity = UserEntity.builder()
                        .userName(userName)
                        .createUser("github")
                        .updateUser(data.getString("id"))
                        .bgColor("#a0b")
                        .createTime(new Date())
                        .id(userId)
                        .isDelete(0)
                        .name(data.getString("name"))
                        .salt(map.get("salt"))
                        .password(map.get("pwd"))
                        .verify(map.get("verify"))
                        .build();
                userMapper.insertSelective(userEntity);

                UserDetailEntity userDetailEntity = UserDetailEntity.builder()
                        .id(IdUtil.randomId())
                        .userId(userId)
                        .email(data.getString("email"))
                        .tags("All Star")
                        .build();
                userDetailMapper.insertSelective(userDetailEntity);
                jwtToken = JwtUtil.sign(userName,userId,map.get("verify"));

                userMap.put("token",jwtToken);
                List<String> authList = new ArrayList<>();
                authList.add("currentUser");
                userMap.put("currentAuthority",authList);
                userMap.put("isNew","0");
                userMap.put("userName",userName);
                userMap.put("name",data.getString("name"));
            }else {
                //生成JwtToken
                jwtToken = JwtUtil.sign(userName,checkUser.getId(),checkUser.getVerify());
                userMap.put("token",jwtToken);
                List<String> authList = permissionMapper.getPermissionListByUserId(checkUser.getId());
                authList.add("currentUser");
                userMap.put("currentAuthority",authList);
                userMap.put("isNew","1");
            }
           return userMap;


        } catch (IOException e) {
            e.printStackTrace();
            throw new ServiceException();
        }
    }
}

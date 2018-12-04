package com.relly.blog.api;

import com.relly.blog.common.exception.ServiceException;
import com.relly.blog.common.model.JsonResult;
import com.relly.blog.dto.UserRegisterDTO;
import com.relly.blog.entity.UserEntity;
import com.relly.blog.service.PermissionService;
import com.relly.blog.service.UserService;
import com.relly.blog.utils.HttpUtil;
import com.relly.blog.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/publicApi/")
@Slf4j
public class LoginController {

    @Resource
    private UserService userService;

    @Resource
    private PermissionService permissionService;

    /**

     *@description 账号登录接口
     *@param userName 用户名
     *@param password 密码
     *@return token令牌 name名字
     *@author relly
     *@date 2018/11/10
     *@update
     */
    @PostMapping("login/accountLogin")
    public JsonResult account(@NotBlank String userName,@NotBlank String password){
        UserEntity userEntity = userService.getUserByUserName(userName);
        if (userEntity==null){
            throw new ServiceException("当前账号尚未注册！");
        }
        // 创建Subject实例
        Subject currentUser = SecurityUtils.getSubject();
        // 将用户名及密码封装到UsernamePasswordToken
        UsernamePasswordToken token = new UsernamePasswordToken(userName, password);
        //生成JwtToken
        String jwtToken = JwtUtil.sign(userName,userEntity.getId(),userEntity.getVerify());
        Map<String,Object> map = new HashMap<>(3);
        map.put("token",jwtToken);
        map.put("name",userEntity.getName());
        List<String> authList = permissionService.getPermissionListByUserId(userEntity.getId());
        authList.add("user");
        map.put("currentAuthority",authList);
            // 判断当前用户是否登录
        if (currentUser.isAuthenticated() == true) {
            return new JsonResult(map);
        }
        try {
            currentUser.login(token);
        } catch (AuthenticationException e) {
            throw new ServiceException("用户名或密码错误!!!");
        }

        return new JsonResult(map);
    }

    /**

     *@description 退出登录
     *@return currentAuthority 用户的角色
     *@author relly
     *@date 2018/11/10
     *@update
     */
    @PostMapping("logout")
    public JsonResult logout(){
        Subject currentUser = SecurityUtils.getSubject();
        currentUser.logout();
        Map<String,Object> map = new HashMap<>(1);
        map.put("currentAuthority","admin");
        return new JsonResult(map);


    }

   /**

    *@description 注册
    *@param userRegisterDTO
    *@return
    *@author relly
    *@date 2018/12/2
    *@update
    */
    @PostMapping("register")
    public JsonResult register(@RequestBody UserRegisterDTO userRegisterDTO){
        String token = userService.register(userRegisterDTO);
        Map<String,String> map = new HashMap<>();
        map.put("token",token);
        return new JsonResult(map);
    }

    /**
     *@description 根据IP获取地址 可用作判断常用地址异常
     *@param
     *@return
     *@author relly
     *@date 2018/12/2
     *@update
     */
    @PostMapping("getIpInfo")
    public JsonResult getIpInfo(HttpServletRequest request){
        String ip = HttpUtil.getIp(request);
        try {
            URL url = new URL("http://ip.taobao.com/service/getIpInfo.php?ip="+ip);
            HttpURLConnection urlCon = (HttpURLConnection) url.openConnection();
            urlCon.connect();
            InputStream inputStream = urlCon.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String s = bufferedReader.readLine();
            Map<String,Object> map = new HashMap<>();
            map.put("ipInfo",s);
            return new JsonResult(map);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException("淘宝IP地址库异常");
        }

    }

    /**
     *
     * 校验邮件
     * @author Relly
     * @date 2018/12/4 8:23
     * @param verify
     * @return com.relly.blog.common.model.JsonResult
     */
    @PostMapping("activation")
    public JsonResult activation(@NotNull String verify){
        userService.activation(verify);
        return new JsonResult();
    }

    /**
     *
     * GitHub第三方登录
     * @author Thunder
     * @date 2018/12/4 14:18
     * @param code 前端请求https://github.com/login/oauth/authorize?client_id=myclient_id&scope=user:email会生成code,
     *             根据github 申请第三方登录的时候配置的回调地址将会请求到当前接口
     * @return com.relly.blog.common.model.JsonResult
     */
    @RequestMapping("githubUser")
    public JsonResult githubUser(@NotNull String code, HttpServletResponse response){
        try {
            Map<String,Object> map = userService.githubUser(code);

            response.sendRedirect("http://localhost:8000/account/center?token="+map.get("token")+"&auth="+map.get("currentAuthority"));
            return new JsonResult(map);

        } catch (IOException e) {
            e.printStackTrace();
            throw new ServiceException("github登录失败");
        }

    }
}

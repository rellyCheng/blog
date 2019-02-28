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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

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

    @Value("${webAddress}")
    private String webAddress;

    public static String WEBHOOK_TOKEN = "https://oapi.dingtalk.com/robot/send?access_token=08c3e07e4c021237b67cc7b9c0a9dcb790d3239041076f9a4831c44d92d35164";

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
        map.put("userId",userEntity.getId());
        map.put("name",userEntity.getName());
        List<String> authList = permissionService.getPermissionListByUserId(userEntity.getId());
        authList.add("currentUser");
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
    @RequestMapping("activation")
    public void activation(@NotNull String verify,HttpServletResponse response){
        userService.activation(verify);
        try {
            response.sendRedirect(webAddress+"/result/activationSuccess");
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    /**

     *@description 像钉钉机器人发送通知
     *@param
     *@return
     *@author relly
     *@date 2019-01-26
     *@update

     */
    @PostMapping("ddWebHook")
    public JsonResult ddWebHook(@NotBlank String content) throws Exception {
        HttpClient httpclient = HttpClients.createDefault();

        HttpPost httppost = new HttpPost(WEBHOOK_TOKEN);
        httppost.addHeader("Content-Type", "application/json; charset=utf-8");

        String textMsg = "{ \"msgtype\": \"text\", \"text\": {\"content\": \""+content+"\"}}";
        StringEntity se = new StringEntity(textMsg, "utf-8");
        httppost.setEntity(se);

        HttpResponse response = httpclient.execute(httppost);
        if (response.getStatusLine().getStatusCode()== HttpStatus.SC_OK){
            String result= EntityUtils.toString(response.getEntity(), "utf-8");
            System.out.println(result);
            return new JsonResult(result);
        }
        
        throw  new ServiceException("发送失败");
    }
}

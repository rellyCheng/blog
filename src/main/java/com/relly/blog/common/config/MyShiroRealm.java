package com.relly.blog.common.config;


import com.relly.blog.entity.UserEntity;
import com.relly.blog.service.UserService;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;

import javax.annotation.Resource;

public class MyShiroRealm extends AuthorizingRealm {

    @Resource
    private UserService userService;
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
//        SysUser userInfo  = (SysUser)principals.getPrimaryPrincipal();
//        List<SysRole> roleList = sysRoleMapper.getRoleList(userInfo.getUid());
//        System.out.println(roleList);
//        for(SysRole role:roleList){
//            System.out.println(role.getRole());
//            List<SysPermission> pList = sysPermissionMapper.getPermissionListByRoleId(role.getId());
//            authorizationInfo.addRole(role.getRole());
//            for(SysPermission p:pList){
//                authorizationInfo.addStringPermission(p.getPermission());
//            }
//        }
        //authorizationInfo.addStringPermission("userInfo:del");
        return authorizationInfo;
    }

    /*主要是用来进行身份认证的，也就是说验证用户输入的账号和密码是否正确。*/
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        //获取用户的输入的账号.
        String username = (String)token.getPrincipal();
        //通过username从数据库中查找 User对象，如果找到，没找到.
        UserEntity userInfo = userService.getUserByUserName(username);
        if(userInfo == null){
            return null;
        }
        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(
                userInfo, //用户名
                userInfo.getPassword(), //密码
                ByteSource.Util.bytes(userInfo.getUserName()),//salt=username+salt
                getName()  //realm name
        );
        System.out.println(authenticationInfo);
        return authenticationInfo;
    }

}

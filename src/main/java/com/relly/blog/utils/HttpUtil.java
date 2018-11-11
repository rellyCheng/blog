package com.relly.blog.utils;


import com.alibaba.fastjson.JSON;
import com.relly.blog.common.model.JsonResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * @author Kartist 2018/8/15 10:51
 */
public class HttpUtil {

    private static String UNKNOWN = "unknown";

    /**
     * 判断是否是AJAX请求
     *
     * @param request
     * @return
     */
    public static boolean isAjaxRequest(HttpServletRequest request) {
        String requestType = request.getHeader("X-Requested-With");
        String acceptType = request.getHeader("Accept");
        return "XMLHttpRequest".equals(requestType) || (acceptType != null && acceptType.contains("application/json"));
    }

    /**
     * 已流的形式返回jsonResult
     *
     * @param response   response
     * @param jsonResult 要返回的JsonResult
     */
    public static void responseUseJsonType(HttpServletResponse response, JsonResult jsonResult) {
        try {
            response.setHeader("Content-Type", "application/json;charset=UTF-8");
            final PrintWriter writer = response.getWriter();
            writer.write(JSON.toJSONString(jsonResult));
            writer.flush();
        } catch (Exception e) {
            response.setStatus(500);
        }
    }


    /**
     * 获取用户真实IP地址
     *
     * @param request
     * @return
     */
    public static String getIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("x-forwarded-for");
        if (ipAddress == null || ipAddress.length() == 0 || UNKNOWN.equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || UNKNOWN.equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || UNKNOWN.equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || UNKNOWN.equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ipAddress == null || ipAddress.length() == 0 || UNKNOWN.equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        if (ipAddress != null) {
            final int index = ipAddress.indexOf(",");
            if (index != -1) {
                ipAddress = ipAddress.substring(0, index);
            }
        }
        return ipAddress;
    }

    /**
     * 将IP地址转化为int
     *
     * @param strIp
     * @return
     */
    public static Long ipToLong(String strIp) {
        long[] ip = new long[4];
        // 先找到IP地址字符串中.的位置
        int position1 = strIp.indexOf(".");
        if (position1 == -1) {
            return 0L;
        }
        int position2 = strIp.indexOf(".", position1 + 1);
        int position3 = strIp.indexOf(".", position2 + 1);
        // 将每个.之间的字符串转换成整型
        ip[0] = Long.parseLong(strIp.substring(0, position1));
        ip[1] = Long.parseLong(strIp.substring(position1 + 1, position2));
        ip[2] = Long.parseLong(strIp.substring(position2 + 1, position3));
        ip[3] = Long.parseLong(strIp.substring(position3 + 1));
        return ((ip[0] << 24) + (ip[1] << 16) + (ip[2] << 8) + ip[3]);
    }

    /**
     * 将int类型IP转化为String
     * * @param longIp
     *
     * @return
     */
    public static String longToIP(long longIp) {
        StringBuilder sb = new StringBuilder("");
        // 直接右移24位
        sb.append(String.valueOf((longIp >>> 24)));
        sb.append(".");
        // 将高8位置0，然后右移16位
        sb.append(String.valueOf((longIp & 0x00FFFFFF) >>> 16));
        sb.append(".");
        // 将高16位置0，然后右移8位
        sb.append(String.valueOf((longIp & 0x0000FFFF) >>> 8));
        sb.append(".");
        // 将高24位置0
        sb.append(String.valueOf((longIp & 0x000000FF)));
        return sb.toString();
    }
}

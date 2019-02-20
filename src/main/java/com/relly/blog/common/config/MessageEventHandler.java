package com.relly.blog.common.config;


import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;
import com.relly.blog.entity.UserEntity;
import com.relly.blog.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class MessageEventHandler {
    private static SocketIOServer server;
    static ArrayList<UUID> listClient = new ArrayList<>();
    static Map<String,UUID> mapClient = new HashMap<>();

    @Autowired
    public MessageEventHandler(SocketIOServer server){
        this.server = server;
    }
    /**
     * 链接客户端,方便后面发送消息时查找到对应的目标client
     * @author Thunder
     * @date 2018/9/19 10:11
     * @param client
     * @return void
     */
    @OnConnect
    public void onConnect(SocketIOClient client){
        UUID clientId = client.getSessionId();
        listClient.add(clientId);
        String userId = JwtUtil.getUserId(client.getHandshakeData().getSingleUrlParam("token"));
        mapClient.put(userId,clientId);
        System.out.println("客户端:" + clientId + "连接成功");
    }

    /**
     * 添加@OnDisconnect事件，客户端断开连接时调用，刷新客户端信息
     * @author Thunder
     * @date 2018/9/19 10:11
     * @param client
     * @return void
     */
    @OnDisconnect
    public void onDisconnect(SocketIOClient client) {
        UUID clientId = client.getSessionId();
        listClient.remove(clientId);
        String userId = JwtUtil.getUserId(client.getHandshakeData().getSingleUrlParam("token"));
        mapClient.remove(userId);
        System.out.println("客户端:" + clientId + "断开连接");
    }

    //消息接收入口，当接收到消息后，查找发送目标客户端，并且向该客户端发送消息，且给自己发送消息
    @OnEvent(value = "messageevent")
    public void onEvent(SocketIOClient client, AckRequest request, String data)
    {
        System.out.println(request.isAckRequested());
        System.out.println("发来消息：" + data);
        server.getClient(client.getSessionId()).sendEvent("messageevent", "back data");
    }

    /**
     * 向客户端推消息了
     * 提醒的内容
     * 对应的链接
     * @author Thunder
     * @date 2018/9/19 10:07
     * @param noticeContentMap, url
     * @return void
     */
    public static void sendBuyLogEvent(Map<String,Object> noticeContentMap, List<String> userIdList) {
        ArrayList<UUID> listClient = new ArrayList<>();
        for (String userId:userIdList) {
            if(mapClient.get(userId)!=null){
                listClient.add(mapClient.get(userId));
            }
        }
        System.out.println("向客户端"+listClient+"推送消息");
        for (UUID clientId : listClient) {
            if (server.getClient(clientId) == null)
                continue;
            server.getClient(clientId).sendEvent("enewbuy", noticeContentMap, 1);
        }

    }
}

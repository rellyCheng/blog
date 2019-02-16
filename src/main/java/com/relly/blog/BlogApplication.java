package com.relly.blog;


import com.corundumstudio.socketio.AuthorizationListener;
import com.corundumstudio.socketio.HandshakeData;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.SpringAnnotationScanner;
import com.relly.blog.common.config.MessageEventHandler;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author relly
 */
@SpringBootApplication
@ComponentScan("com.relly.blog.**")
@MapperScan("com.relly.blog.mapper.**")
public class BlogApplication {

    @Value("${serverAddress}")
    private String serverAddress;
    @Value("${socket.port}")
    private Integer socketPort;


    public static void main(String[] args) {
        SpringApplication.run(BlogApplication.class, args);
    }


    @Bean
    public SocketIOServer socketIOServer() {
        com.corundumstudio.socketio.Configuration config = new com.corundumstudio.socketio.Configuration();
        config.setHostname(serverAddress);
        config.setPort(socketPort);
        config.setAuthorizationListener(new AuthorizationListener() {
            @Override
            public boolean isAuthorized(HandshakeData data) {
                // 获取token  可以用来校验身份
                String token = data.getSingleUrlParam("token");
                System.out.println("连接参数：token=" + token);
                // 如果认证不通过会返回一个Socket.EVENT_CONNECT_ERROR事件
                return true;
            }
        });
        final SocketIOServer server = new SocketIOServer(config);
        return server;
    }

    @Bean
    public SpringAnnotationScanner springAnnotationScanner(SocketIOServer socketServer) {
        return new SpringAnnotationScanner(socketServer);
    }
}

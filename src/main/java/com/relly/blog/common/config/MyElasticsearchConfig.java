package com.relly.blog.common.config;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @Author Relly
 * @CreteTime 2019-02-24 20:39
 * @Description
 */
@Configuration
public class MyElasticsearchConfig {
    @Value("${elasticIp}")
    private String elasticIp;

    @Bean
    public TransportClient client() throws UnknownHostException {

        TransportAddress node = new TransportAddress(
                InetAddress.getByName(elasticIp),
                9300
        );

        Settings settings = Settings.builder()
                .put("cluster.name","docker-cluster")
                .build();
        PreBuiltTransportClient client = new PreBuiltTransportClient(settings);
        client.addTransportAddress(node);


        return client;
    }
    /**
     * RedisConfig在没加 @EnableCaching注解之前是不会报这这个异常的，加了之后才报的
     * 防止netty的bug
     * java.lang.IllegalStateException: availableProcessors is already set to [4], rejecting [4]
     */
    @PostConstruct
    void init() {
        System.setProperty("es.set.netty.runtime.available.processors", "false");
    }

}

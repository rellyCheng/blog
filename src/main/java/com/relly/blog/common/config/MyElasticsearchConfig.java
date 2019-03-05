package com.relly.blog.common.config;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @Author Relly
 * @CreteTime 2019-02-24 20:39
 * @Description
 */
@Configuration
public class MyElasticsearchConfig {
    @Bean
    public TransportClient client() throws UnknownHostException {

        TransportAddress node = new TransportAddress(
                InetAddress.getByName("localhost"),
                9300
        );

        Settings settings = Settings.builder()
                .put("cluster.name","relly")
                .build();
        PreBuiltTransportClient client = new PreBuiltTransportClient(settings);
        client.addTransportAddress(node);


        return client;
    }
}

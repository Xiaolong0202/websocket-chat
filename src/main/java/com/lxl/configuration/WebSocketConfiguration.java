package com.lxl.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * @Author LiuXiaolong
 * @Description test-websocket
 * @DateTime 2023/10/20  21:27
 **/
@Configuration
public class WebSocketConfiguration {

    /**
     * 注入一个serverEndpointExporter，该Bean会自动注册使用@serverEndpoint注解由明的websocket endpoint
     * @return
     */
    @Bean
    public ServerEndpointExporter serverEndpointExporter(){
        return new ServerEndpointExporter();
    }
}

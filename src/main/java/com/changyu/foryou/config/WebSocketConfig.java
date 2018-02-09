package com.changyu.foryou.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.changyu.foryou.interceptor.MyWebSocketInterceptor;
import com.changyu.foryou.controller.WebSocketPushHandler;

@Configuration
@EnableWebSocket
public class WebSocketConfig extends WebMvcConfigurerAdapter implements WebSocketConfigurer {
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
    	System.out.println("WebSocketConfig enter");

        registry.addHandler(WebSocketPushHandler(), "/webSocketServer").addInterceptors(new MyWebSocketInterceptor()).setAllowedOrigins("*");
        //registry.addHandler(WebSocketPushHandler(), "/sockjs/webSocketServer")
        //        .addInterceptors(new MyWebSocketInterceptor()).withSockJS();
    }

    @Bean
    public WebSocketHandler WebSocketPushHandler() {
        return new WebSocketPushHandler();
    }

}
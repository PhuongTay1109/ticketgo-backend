package com.ticketgo.config.websocket;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Data
@Configuration
@ConfigurationProperties(prefix = "queue.rabbitmq.socket")
@EnableWebSocketMessageBroker
@Component
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private String host;
    private int port;
    private String username;
    private String password;
    private String appDest;
    private String stompBroker;
    private String sockjsEndpoint;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint(sockjsEndpoint)
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes(appDest)
                .enableStompBrokerRelay(stompBroker)
                .setRelayHost(host)
                .setRelayPort(port)
                .setClientLogin(username)
                .setClientPasscode(password)
                .setSystemLogin(username)
                .setSystemPasscode(password);
    }
}


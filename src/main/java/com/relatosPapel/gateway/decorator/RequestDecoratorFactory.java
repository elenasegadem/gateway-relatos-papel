package com.relatosPapel.gateway.decorator;

import com.relatosPapel.gateway.model.GatewayRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class RequestDecoratorFactory {

    private final ObjectMapper objectMapper;

    public ServerHttpRequestDecorator getDecorator(GatewayRequest gatewayRequest) {
        return switch (gatewayRequest.getHttpMethod().name().toUpperCase()) {
            case "GET" -> new GetRequestDecorator(gatewayRequest);
            case "POST" -> new PostRequestDecorator(gatewayRequest, objectMapper);
            case "PUT" -> new PutRequestDecorator(gatewayRequest, objectMapper);
            case "PATCH" -> new PatchRequestDecorator(gatewayRequest, objectMapper);
            case "DELETE" -> new DeleteRequestDecorator(gatewayRequest);
            default -> throw new IllegalArgumentException("Unsupported HTTP method: " + gatewayRequest.getHttpMethod());
        };
    }
}

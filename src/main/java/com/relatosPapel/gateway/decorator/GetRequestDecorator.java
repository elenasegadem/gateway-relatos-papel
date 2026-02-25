package com.relatosPapel.gateway.decorator;

import com.relatosPapel.gateway.model.GatewayRequest;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;

import java.net.URI;
import java.util.Map;
import java.util.Objects;

@Slf4j
public class GetRequestDecorator extends ServerHttpRequestDecorator {

    private final GatewayRequest gatewayRequest;

    public GetRequestDecorator(GatewayRequest gatewayRequest) {
        super(gatewayRequest.getExchange().getRequest());
        this.gatewayRequest = gatewayRequest;
    }

    @Override
    @NonNull
    public HttpMethod getMethod() {
        return HttpMethod.GET;
    }

    @Override
    @NonNull
    public URI getURI() {
        return UriComponentsBuilder
                .fromUri((URI) gatewayRequest.getExchange().getAttributes().get(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR))
                .queryParams(gatewayRequest.toMultiValueQueryParams())
                .build()
                .toUri();
    }

    @Override
    @NonNull
    public HttpHeaders getHeaders() {
        return gatewayRequest.getHeaders();
    }

    @Override
    @NonNull
    public Flux<DataBuffer> getBody() {
        return Flux.empty();
    }


    private MultiValueMap<String, String> toQueryParams(MultiValueMap<String, String> in) {
        LinkedMultiValueMap<String, String> out = new LinkedMultiValueMap<>();
        if (in == null) return out;

        in.forEach((key, values) -> {
            if (key == null || values == null) return;
            values.stream()
                    .filter(Objects::nonNull)
                    .forEach(v -> out.add(key, v));
        });

        return out;
    }
}

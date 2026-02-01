package com.relatosPapel.gateway.utils;

import com.relatosPapel.gateway.model.GatewayRequest;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.bouncycastle.util.Strings;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import tools.jackson.databind.ObjectMapper;

import org.springframework.core.io.buffer.DataBuffer;

import java.util.concurrent.atomic.AtomicReference;

@Component
@RequiredArgsConstructor
public class RequestBodyExtractor {

    private final ObjectMapper objectMapper;

    @SneakyThrows
    public GatewayRequest getRequest(ServerWebExchange exchange, DataBuffer dataBuffer) {
        DataBufferUtils.retain(dataBuffer);
        Flux<DataBuffer> cachedFlux = Flux.defer(() -> Flux.just(dataBuffer.split(dataBuffer.readableByteCount())));
        String rawBody = getRawRequestBody(cachedFlux);
        DataBufferUtils.release(dataBuffer);
        GatewayRequest request = objectMapper.readValue(rawBody, GatewayRequest.class);
        request.setExchange(exchange);

        HttpHeaders headers = new HttpHeaders();
        headers.putAll(exchange.getRequest().getHeaders());
        headers.remove(HttpHeaders.CONTENT_LENGTH);
        headers.set(HttpHeaders.TRANSFER_ENCODING, "chuked");
        request.setHeaders(headers);
        return request;
    }

    private String getRawRequestBody(Flux<DataBuffer> body) {
        AtomicReference<String> rawRef = new AtomicReference<>();
        body.subscribe(dataBuffer -> {
            byte[] bytes = new byte[dataBuffer.readableByteCount()];
            dataBuffer.read(bytes);
            rawRef.set(Strings.fromUTF8ByteArray(bytes));
        });
        return rawRef.get();
    }
}

package com.relatosPapel.gateway.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GatewayRequest {

    private HttpMethod httpMethod;

    private LinkedMultiValueMap<String, String> queryParams;

    public MultiValueMap<String, String> toMultiValueQueryParams() {
        LinkedMultiValueMap<String, String> out = new LinkedMultiValueMap<>();
        if (queryParams == null) return out;

        queryParams.forEach((k, v) -> {
            if (v == null) return;

            if (v instanceof Iterable<?> it) {
                for (Object item : it) if (item != null) out.add(k, item.toString());
            } else {
                out.add(k, v.toString());
            }
        });

        return out;
    }

    private Object body;

    @JsonIgnore
    private ServerWebExchange exchange;

    @JsonIgnore
    private HttpHeaders headers;
}

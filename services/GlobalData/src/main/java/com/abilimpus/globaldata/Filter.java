package com.abilimpus.globaldata;

import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

public class Filter implements WebFilter {

    private static final Logger log = LoggerFactory.getLogger(Filter.class);

    @Override
    @NonNull
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        log.info(
            exchange.getRequest().getHeaders().toSingleValueMap().toString()
        );
        return chain.filter(exchange);
    }
}

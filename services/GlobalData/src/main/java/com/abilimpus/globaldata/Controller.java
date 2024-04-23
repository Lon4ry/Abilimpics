package com.abilimpus.globaldata;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/data")
public class Controller {

    private static final Logger log = LoggerFactory.getLogger(Controller.class);

    @GetMapping("/user")
    public Mono<Void> getExchange(ServerWebExchange exchange) {
        return exchange
            .getRequest()
            .getBody()
            .doOnNext(i -> log.info("Request body: {}", i))
            .then();
    }
}

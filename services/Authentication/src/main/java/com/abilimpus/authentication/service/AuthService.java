package com.abilimpus.authentication.service;

import com.abilimpus.authentication.exception.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Service
public class AuthService {

    private final ServerSecurityContextRepository securityContextRepository;

    @Autowired
    public AuthService(
        ServerSecurityContextRepository securityContextRepository
    ) {
        this.securityContextRepository = securityContextRepository;
    }

    public Mono<Void> authenticate(
        ServerWebExchange exchange,
        Authentication authentication
    ) throws UnauthorizedException {
        if (
            !authentication.isAuthenticated()
        ) return this.securityContextRepository.save(exchange, null)
            .contextWrite(
                ReactiveSecurityContextHolder.withSecurityContext(Mono.empty())
            )
            .then(Mono.error(new UnauthorizedException("Unauthorized")));

        SecurityContextImpl securityContext = new SecurityContextImpl(
            authentication
        );

        return this.securityContextRepository.save(
                exchange,
                securityContext
            ).contextWrite(
                ReactiveSecurityContextHolder.withSecurityContext(
                    Mono.just(securityContext)
                )
            );
    }

    @PreAuthorize("isAuthenticated()")
    public Mono<Void> logout(ServerWebExchange exchange) {
        return this.securityContextRepository.save(exchange, null).contextWrite(
                ReactiveSecurityContextHolder.withSecurityContext(Mono.empty())
            );
    }
}

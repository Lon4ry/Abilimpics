package com.abilimpus.authentication.implemetation;

import com.abilimpus.authentication.exception.UnauthorizedException;
import com.abilimpus.authentication.service.UserService;
import com.abilimpus.authentication.token.EmailPasswordAuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class ReactiveAuthenticationManagerImpl
    implements ReactiveAuthenticationManager {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public ReactiveAuthenticationManagerImpl(
        UserService userService,
        PasswordEncoder passwordEncoder
    ) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication)
        throws UnauthorizedException {
        if (
            authentication.getClass() == EmailPasswordAuthenticationToken.class
        ) {
            return this.userService.findByEmail(
                    (String) authentication.getPrincipal()
                )
                .onErrorMap(e -> new UnauthorizedException("Unauthorized"))
                .mapNotNull(
                    user ->
                        this.passwordEncoder.matches(
                                    (String) authentication.getCredentials(),
                                    user.getPassword()
                                )
                            ? (Authentication) new EmailPasswordAuthenticationToken(
                                authentication.getPrincipal(),
                                null,
                                user
                                    .getAuthorities()
                                    .stream()
                                    .map(SimpleGrantedAuthority::new)
                                    .toList()
                            )
                            : null
                )
                .switchIfEmpty(
                    Mono.error(new UnauthorizedException("Unauthorized"))
                );
        }
        throw new UnauthorizedException("Unauthorized");
    }
}

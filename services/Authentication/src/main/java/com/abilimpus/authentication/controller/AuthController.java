package com.abilimpus.authentication.controller;

import com.abilimpus.authentication.dto.LoginDto;
import com.abilimpus.authentication.dto.RegisterDto;
import com.abilimpus.authentication.entity.User;
import com.abilimpus.authentication.exception.UnauthorizedException;
import com.abilimpus.authentication.exception.UnprocessableEntityException;
import com.abilimpus.authentication.implemetation.ReactiveAuthenticationManagerImpl;
import com.abilimpus.authentication.service.AuthService;
import com.abilimpus.authentication.service.UserService;
import com.abilimpus.authentication.token.EmailPasswordAuthenticationToken;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(
        AuthController.class
    );
    private final ReactiveAuthenticationManager authenticationManager;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;

    @Autowired
    public AuthController(
        ReactiveAuthenticationManagerImpl authenticationManager,
        UserService userService,
        PasswordEncoder passwordEncoder,
        AuthService authService
    ) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.authService = authService;
    }

    @PostMapping("/registration")
    public Mono<ResponseEntity<Authentication>> register(
        @RequestBody RegisterDto registerDto,
        ServerWebExchange exchange
    ) throws UnprocessableEntityException {
        User buildedUser = User.builder()
            .email(registerDto.getEmail())
            .firstName(registerDto.getFirstName())
            .lastName(registerDto.getLastName())
            .patronymic(registerDto.getPatronymic())
            .password(this.passwordEncoder.encode(registerDto.getPassword()))
            .authorities(Arrays.stream(new String[] { "COMMENT" }).toList())
            .build();

        return this.userService.save(buildedUser)
            .flatMap(
                user ->
                    this.authenticationManager.authenticate(
                            new EmailPasswordAuthenticationToken(
                                registerDto.getEmail(),
                                registerDto.getPassword()
                            )
                        )
            )
            .flatMap(
                authentication ->
                    this.authService.authenticate(
                            exchange,
                            authentication
                        ).thenReturn(
                            ResponseEntity.status(HttpStatus.CREATED).body(
                                authentication
                            )
                        )
            );
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<Authentication>> login(
        @RequestBody LoginDto loginDto,
        ServerWebExchange exchange
    ) throws UnauthorizedException {
        return this.authenticationManager.authenticate(
                new EmailPasswordAuthenticationToken(
                    loginDto.getEmail(),
                    loginDto.getPassword()
                )
            ).flatMap(
                authentication ->
                    this.authService.authenticate(
                            exchange,
                            authentication
                        ).thenReturn(
                            ResponseEntity.status(HttpStatus.OK).body(
                                authentication
                            )
                        )
            );
    }

    @PostMapping("/logout")
    public Mono<ResponseEntity<String>> logout(ServerWebExchange exchange) {
        return this.authService.logout(exchange).thenReturn(
                ResponseEntity.status(HttpStatus.OK).body("Logged out")
            );
    }

    @GetMapping("/user")
    @ResponseBody
    public Mono<Authentication> getAuthentication(
        Authentication authentication
    ) {
        log.info(authentication.toString());

        return Mono.just(authentication);
    }
}

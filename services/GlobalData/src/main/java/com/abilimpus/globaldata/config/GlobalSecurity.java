package com.abilimpus.globaldata.config;

import com.abilimpus.globaldata.Filter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository;

@Configuration
@EnableWebFluxSecurity
public class GlobalSecurity {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ServerSecurityContextRepository serverSecurityContextRepository() {
        return new WebSessionServerSecurityContextRepository();
    }

    public Filter filter() {
        return new Filter();
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(
        ServerHttpSecurity http,
        ServerSecurityContextRepository securityContextRepository
    ) {
        return http
            .addFilterAt(filter(), SecurityWebFiltersOrder.AUTHENTICATION)
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .cors(ServerHttpSecurity.CorsSpec::disable)
            .authorizeExchange(
                authorizeExchangeSpec ->
                    authorizeExchangeSpec
                        .pathMatchers(HttpMethod.GET, "/data/user")
                        .permitAll()
                        .pathMatchers(HttpMethod.GET, "/actuator/**")
                        .permitAll()
                        .anyExchange()
                        .denyAll()
            )
            .securityContextRepository(securityContextRepository)
            .build();
    }
}

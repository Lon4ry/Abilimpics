package com.abilimpus.authentication.service;

import com.abilimpus.authentication.entity.User;
import com.abilimpus.authentication.exception.NotFoundException;
import com.abilimpus.authentication.exception.UnprocessableEntityException;
import com.abilimpus.authentication.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(
        UserService.class
    );
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Mono<User> save(User user) throws UnprocessableEntityException {
        return Mono.fromCallable(() -> this.userRepository.save(user))
            .onErrorMap(
                e ->
                    e.getMessage().contains("email")
                        ? new UnprocessableEntityException(
                            "Email already exists"
                        )
                        : new UnprocessableEntityException(e.getMessage())
            )
            .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<User> findByEmail(String email) throws NotFoundException {
        return Mono.fromCallable(
            () ->
                this.userRepository.findByEmail(email).orElseThrow(
                        () -> new NotFoundException("User not found")
                    )
        ).subscribeOn(Schedulers.boundedElastic());
    }
}

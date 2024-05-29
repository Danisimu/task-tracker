package com.example.tasktracker.security;

import com.example.tasktracker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
@Qualifier("reactiveUserDetailsServiceImpl")
public class ReactiveUserDetailsServiceImpl implements ReactiveUserDetailsService {
    private final UserService userService;

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return userService.findByUserName(username).map(AppUserDetails::new);
    }
}

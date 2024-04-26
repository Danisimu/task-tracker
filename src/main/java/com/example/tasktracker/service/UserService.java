package com.example.tasktracker.service;

import com.example.tasktracker.entity.User;
import com.example.tasktracker.exception.EntityNotFoundException;
import com.example.tasktracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


import java.text.MessageFormat;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;


    public Flux<User> findAllUsers(){

      return userRepository.findAll();
    }

    public Mono<User> findByIdUser(String id){
        return userRepository.findById(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException(MessageFormat.format("Пользователь с ID: {0} не найден", id))));
    }

    public Mono<User> saveUser(User user){
        user.setId(UUID.randomUUID().toString());
        return userRepository.save(user);
    }

    public Mono<User> updateUser(String id, User user){
        return findByIdUser(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException(MessageFormat.format("Пользователь с ID: {0} не найден", id))))
            .flatMap(userForUpdate -> {
            if (StringUtils.hasText(userForUpdate.getUserName())){
                userForUpdate.setUserName(user.getUserName());
            }
            if (StringUtils.hasText(userForUpdate.getEmail())){
                userForUpdate.setEmail(user.getEmail());
            }

            return userRepository.save(userForUpdate);
        });
    }

    public Mono<Void> deleteUser(String id){
        return userRepository.deleteById(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException(MessageFormat.format("Пользователь с ID: {0} не найден", id))));
    }




}

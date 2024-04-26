package com.example.tasktracker.web.controller;

import com.example.tasktracker.mapper.UserMapper;
import com.example.tasktracker.service.UserService;
import com.example.tasktracker.web.model.UpsertUserRequest;
import com.example.tasktracker.web.model.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserMapper userMapper;

    private final UserService userService;

    @GetMapping
    public Flux<UserResponse> getAllUsers(){
        return userService.findAllUsers().map(userMapper::userToUserResponse);
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<UserResponse>> getUserById(@PathVariable String id){
        return userService.findByIdUser(id)
                .map(userMapper::userToUserResponse)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<UserResponse>> createUser(@RequestBody UpsertUserRequest request){
        return userService.saveUser(userMapper.requestToUser(request))
                .map(userMapper::userToUserResponse)
                .map(ResponseEntity::ok);

    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<UserResponse>> updateUser(@PathVariable String id, @RequestBody UpsertUserRequest request){
        return userService.updateUser(id, userMapper.requestToUser(request))
                .map(userMapper::userToUserResponse)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteUser(@PathVariable String id){
        return userService.deleteUser(id)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }
}

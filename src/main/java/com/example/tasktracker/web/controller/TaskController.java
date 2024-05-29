package com.example.tasktracker.web.controller;

import com.example.tasktracker.mapper.TaskMapper;
import com.example.tasktracker.service.TaskService;
import com.example.tasktracker.web.model.TaskResponse;
import com.example.tasktracker.web.model.UpsertTaskRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/task")
public class TaskController {

    private final TaskMapper taskMapper;

    private final TaskService taskService;

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_MANAGER')")
    @GetMapping
    public Flux<TaskResponse> findAllTasks(){
        return taskService.findAllTasks().map(taskMapper::taskToTaskResponse);
    }

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_MANAGER')")
    @GetMapping("/{id}")
    public Mono<ResponseEntity<TaskResponse>> findTaskById(@PathVariable String id){
        return taskService.findTaskById(id)
                .map(taskMapper::taskToTaskResponse)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAnyRole('ROLE_MANAGER')")
    @PostMapping
    public Mono<ResponseEntity<TaskResponse>> createTask(@RequestBody UpsertTaskRequest request){
        return taskService.saveTask(taskMapper.requestToTask(request))
                .map(taskMapper::taskToTaskResponse)
                .map(ResponseEntity::ok);
    }

    @PreAuthorize("hasAnyRole('ROLE_MANAGER')")
    @PutMapping("/{id}")
    public Mono<ResponseEntity<TaskResponse>> updateTask(@PathVariable String id, @RequestBody UpsertTaskRequest request){
        return taskService.update(id, taskMapper.requestToTask(request))
                .map(taskMapper::taskToTaskResponse)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAnyRole('ROLE_MANAGER')")
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteTask(@PathVariable String id){
        return taskService.delete(id)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }

    @PreAuthorize("hasAnyRole('ROLE_MANAGER')")
    @PutMapping("/addObserver")
    public  Mono<ResponseEntity<TaskResponse>> addObserver(@RequestParam String taskId, @RequestParam String observerId){
        return taskService.addObserver(taskId,observerId)
                .map(taskMapper::taskToTaskResponse)
                .map(ResponseEntity::ok);

    }

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_MANAGER')")
    @PutMapping("/removeObserver")
    public Mono<ResponseEntity<TaskResponse>> removeObserver(@RequestParam String taskId, @RequestParam String observerId){
        return taskService.deleteObserver(taskId,observerId)
                .map(taskMapper::taskToTaskResponse)
                .map(ResponseEntity::ok);
    }
}

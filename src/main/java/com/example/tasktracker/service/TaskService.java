package com.example.tasktracker.service;

import com.example.tasktracker.entity.Task;
import com.example.tasktracker.entity.TaskStatus;
import com.example.tasktracker.entity.User;
import com.example.tasktracker.exception.EntityNotFoundException;
import com.example.tasktracker.repository.TaskRepository;
import com.example.tasktracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.text.MessageFormat;
import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    private final UserRepository userRepository;


    public Flux<Task> findAllTasks(){

        return taskRepository.findAll()
                .flatMap(this::tasks);
    }

    public Mono<Task> findTaskById(String id){

        return taskRepository.findById(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException(MessageFormat.format("Задача с ID: {0} не найдена", id))))
                .flatMap(this::tasks);
    }

    public Mono<Task> saveTask(Task task){


        task.setId(UUID.randomUUID().toString());
        task.setCreatedAt(Instant.now());
        task.setUpdatedAt(Instant.now());
        task.setStatus(TaskStatus.TODO);

        return taskRepository.save(task)
                .flatMap(this::tasks);
    }

    public Mono<Task> update(String id, Task task){
        task.setUpdatedAt(Instant.now());
        return taskRepository.findById(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException(MessageFormat.format("Задача с ID: {0} не найдена", id))))
                .flatMap(taskForUpdate -> {
                    if (StringUtils.hasText(taskForUpdate.getName())){
                        taskForUpdate.setName(task.getName());
                    }
                    if (StringUtils.hasText(taskForUpdate.getDescription())){
                        taskForUpdate.setDescription(task.getDescription());
                    }
                    if (StringUtils.hasText(taskForUpdate.getAuthorId())){
                        taskForUpdate.setAuthorId(task.getAuthorId());
                    }
                    if (StringUtils.hasText(taskForUpdate.getAssigneeId())){
                        taskForUpdate.setAssigneeId(task.getAssigneeId());
                    }
                    if (taskForUpdate.getObserverIds() != null){
                        taskForUpdate.setObserverIds(task.getObserverIds());
                    }

                    return taskRepository.save(taskForUpdate)
                            .flatMap(this::tasks);
                });
    }

    public Mono<Void> delete(String id){
        return taskRepository.findById(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException(MessageFormat.format("Задача с ID: {0} не найдена", id))))
                .flatMap(taskRepository::delete);
    }

    public Mono<Task> addObserver(String taskId, String observerId){
        Mono<User> observerMono = userRepository.findById(observerId)
                .switchIfEmpty(Mono.error(new EntityNotFoundException(MessageFormat.format("Пользователь с ID: {0} не найден", observerId))));
        return taskRepository.findById(taskId)
                .switchIfEmpty(Mono.error(new EntityNotFoundException(MessageFormat.format("Задача с ID: {0} не найдена", taskId))))
                .zipWith(observerMono, (task, observer) ->
                { task.getObserverIds().add(observer.getId());
                    return task;
                })
                .flatMap(taskRepository::save)
                .flatMap(this::tasks);

    }
    public Mono<Task> deleteObserver(String taskId, String observerId){
        Mono<User> observerMono = userRepository.findById(observerId)
                .switchIfEmpty(Mono.error(new EntityNotFoundException(MessageFormat.format("Пользователь с ID: {0} не найден", observerId))));
        return taskRepository.findById(taskId)
                .switchIfEmpty(Mono.error(new EntityNotFoundException(MessageFormat.format("Задача с ID: {0} не найдена", taskId))))
                .zipWith(observerMono, (task, observer) ->
                {
                    Set<String> observerIds = task.getObserverIds();
                    if (observerIds == null && observerIds.stream().noneMatch(id -> id.equals(observer.getId()))){
                        throw new EntityNotFoundException(MessageFormat.format("Пользователь с ID: {0} не найден", observer.getId()));
                    }
                    task.getObserverIds().remove(observer.getId());
                    return task;
                })
                .flatMap(taskRepository::save)
                .flatMap(this::tasks);

    }

    private Mono<? extends Task>  tasks(Task initialTask){
        Mono<User> authorMono = userRepository.findById(initialTask.getAuthorId())
                .switchIfEmpty(Mono.error(new EntityNotFoundException(MessageFormat.format("Пользователь с ID: {0} не найден", initialTask.getAuthorId()))));
        Mono<User> assigneeMono = userRepository.findById(initialTask.getAssigneeId())
                .switchIfEmpty(Mono.error(new EntityNotFoundException(MessageFormat.format("Пользователь с ID: {0} не найден", initialTask.getAssigneeId()))));
        Flux<User> observersFlux;
        if (initialTask.getObserverIds() != null){
            observersFlux = userRepository.findAllById(initialTask.getObserverIds());
        }
        else {
            observersFlux = Flux.fromIterable(Collections.emptySet());
        }

        return Mono.just(initialTask)
                .zipWith(authorMono, (task, author) -> {
                    task.setAuthor(author);
                    return task;
                })
                .zipWith(assigneeMono, (task, assignee) -> {
                    task.setAssignee(assignee);
                    return task;
                })
                .zipWith(observersFlux.collectList(), (task, observers) -> {
                    task.setObservers(new HashSet<>(observers));
                    return task;
                });


    }

}

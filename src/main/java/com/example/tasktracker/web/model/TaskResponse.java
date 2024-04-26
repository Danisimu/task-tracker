package com.example.tasktracker.web.model;

import com.example.tasktracker.entity.TaskStatus;
import com.example.tasktracker.entity.User;
import lombok.Data;

import java.time.Instant;
import java.util.Set;

@Data
public class TaskResponse {

    private String id;

    private String name;

    private String description;

    private Instant createdAt;

    private Instant updatedAt;

    private TaskStatus status;

    private String authorId;

    private String assigneeId;

    private Set<String> observerIds;

    private User author;

    private User assignee;

    private Set<User> observers;
}

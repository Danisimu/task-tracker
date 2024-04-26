package com.example.tasktracker.web.model;

import lombok.Data;

import java.util.Set;

@Data
public class UpsertTaskRequest {

    private String name;

    private String description;

    private String authorId;

    private String assigneeId;

    private Set<String> observerIds;

    private UpsertUserRequest author;

    private UpsertUserRequest assignee;

}

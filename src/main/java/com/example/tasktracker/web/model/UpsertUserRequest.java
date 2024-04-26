package com.example.tasktracker.web.model;

import lombok.Data;

@Data
public class UpsertUserRequest {

    private String userName;

    private String email;
}

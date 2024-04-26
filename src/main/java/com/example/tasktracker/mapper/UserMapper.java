package com.example.tasktracker.mapper;

import com.example.tasktracker.entity.User;
import com.example.tasktracker.web.model.UpsertUserRequest;
import com.example.tasktracker.web.model.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    User requestToUser(UpsertUserRequest request);

    UserResponse userToUserResponse(User user);


}

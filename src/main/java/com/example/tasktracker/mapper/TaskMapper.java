package com.example.tasktracker.mapper;

import com.example.tasktracker.entity.Task;
import com.example.tasktracker.web.model.TaskResponse;
import com.example.tasktracker.web.model.UpsertTaskRequest;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TaskMapper {

    Task requestToTask(UpsertTaskRequest request);

    TaskResponse taskToTaskResponse(Task task);



}

package com.example.mapper;

import com.example.dtos.CreateTaskDto;
import com.example.dtos.PageableDto;
import com.example.dtos.TaskDto;
import com.example.dtos.UpdateTaskDto;
import com.example.entities.Priority;
import com.example.entities.Status;
import com.example.entities.Task;
import com.example.entities.User;
import org.mapstruct.*;
import org.springframework.data.domain.Page;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        imports = {Status.class, Priority.class},
        uses = CommentMapper.class
)
public interface TaskMapper {

    @Mapping(target = "status", defaultExpression = "java(Status.TODO)")
    @Mapping(target = "priority", defaultExpression = "java(Priority.LOW)")
    @Mapping(target = "author", expression = "java(author)")
    Task fromDto(CreateTaskDto createTaskDto, @Context User author);

    TaskDto toDto(Task task);

    @Mapping(target = "pageNumber", source = "number")
    @Mapping(target = "pageSize", source = "size")
    PageableDto<TaskDto> toPageableDto(Page<Task> tasks);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Task partialUpdate(UpdateTaskDto updateTaskDto, @MappingTarget Task task);
}

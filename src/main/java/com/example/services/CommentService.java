package com.example.services;

import com.example.dtos.CreateCommentDto;
import com.example.entities.Comment;
import com.example.entities.Task;
import com.example.entities.User;

public interface CommentService {
    Comment createComment(CreateCommentDto commentDto, User author, Task task);

    void deleteComment(String commentId, User user);

    Comment findById(String commentId);
}

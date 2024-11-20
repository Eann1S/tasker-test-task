package com.example.services.impl;

import com.example.dtos.CreateCommentDto;
import com.example.entities.Comment;
import com.example.entities.Role;
import com.example.entities.Task;
import com.example.entities.User;
import com.example.exceptions.ForbiddenException;
import com.example.exceptions.NotFoundException;
import com.example.mapper.CommentMapper;
import com.example.repositories.CommentRepository;
import com.example.services.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    @Override
    public Comment createComment(CreateCommentDto commentDto, User author, Task task) {
        Comment comment = commentMapper.toEntity(commentDto, author, task);
        return commentRepository.saveAndFlush(comment);
    }

    @Override
    public void deleteComment(String commentId, User user) {
        Comment comment = findById(commentId);
        if (notAuthor(comment, user) && notAdmin(user)) {
            throw new ForbiddenException("You are not allowed to delete this comment");
        }
        commentRepository.delete(comment);
    }

    @Override
    public Comment findById(String commentId) {
        return commentRepository.findById(commentId).orElseThrow(() -> new NotFoundException("Comment not found"));
    }

    private boolean notAdmin(User user) {
        return !user.getRoles().contains(Role.ROLE_ADMIN);
    }

    private boolean notAuthor(Comment comment, User user) {
        return !user.equals(comment.getAuthor());
    }
}

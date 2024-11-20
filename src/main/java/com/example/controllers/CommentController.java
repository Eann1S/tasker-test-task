package com.example.controllers;

import com.example.entities.User;
import com.example.services.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable String commentId, @AuthenticationPrincipal User user) {
        commentService.deleteComment(commentId, user);
        return ResponseEntity.ok().build();
    }
}

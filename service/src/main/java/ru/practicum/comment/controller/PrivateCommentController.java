package ru.practicum.comment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.service.CommentService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/comments")
public class PrivateCommentController {

    private final CommentService commentService;

    /**
     * Добавить комментарий к событию.
     * POST /users/{userId}/comments/{eventId}
     */
    @PostMapping("/{eventId}")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto addComment(@PathVariable Long userId,
                                 @PathVariable Long eventId,
                                 @Valid @RequestBody NewCommentDto dto) {
        return commentService.addComment(userId, eventId, dto);
    }

    /**
     * Обновить (PATCH) свой комментарий commentId.
     * PATCH /users/{userId}/comments/{commentId}
     */
    @PatchMapping("/{commentId}")
    public CommentDto updateComment(@PathVariable Long userId,
                                    @PathVariable Long commentId,
                                    @Valid @RequestBody NewCommentDto dto) {
        return commentService.updateCommentByUser(userId, commentId, dto);
    }

    /**
     * Удалить свой комментарий commentId.
     * DELETE /users/{userId}/comments/{commentId}
     */
    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Long userId,
                              @PathVariable Long commentId) {
        commentService.deleteCommentByUser(userId, commentId);
    }

    /**
     * Получить все комментарии текущего пользователя.
     * GET /users/{userId}/comments
     */
    @GetMapping
    public List<CommentDto> getUserComments(@PathVariable Long userId) {
        return commentService.getUserComments(userId);
    }

    /**
     * Получить все комментарии пользователя к конкретному событию.
     * GET /users/{userId}/comments/{eventId}
     */
    @GetMapping("/{eventId}")
    public List<CommentDto> getUserCommentsForEvent(@PathVariable Long userId,
                                                    @PathVariable Long eventId) {
        return commentService.getUserCommentsForEvent(userId, eventId);
    }


}

package ru.practicum.comment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.service.CommentService;

import java.util.List;

@RestController
@RequestMapping("/events/{eventId}/comments")
@RequiredArgsConstructor
public class PublicCommentController {

    private final CommentService commentService;

    /**
     * Публичный эндпоинт для получения всех ОДОБРЕННЫХ комментариев к событию.
     */
    @GetMapping
    public List<CommentDto> getEventComments(@PathVariable Long eventId) {
        return commentService.getApprovedCommentsForEvent(eventId);
    }
}

package ru.practicum.comment.service;

import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.NewCommentDto;

import java.util.List;

public interface CommentService {

    CommentDto addComment(Long userId, Long eventId, NewCommentDto dto);

    CommentDto updateCommentByUser(Long userId, Long commentId, NewCommentDto dto);

    void deleteCommentByUser(Long userId, Long commentId);

    List<CommentDto> getUserComments(Long userId);

    List<CommentDto> getUserCommentsForEvent(Long userId, Long eventId);

    CommentDto approveComment(Long commentId);

    void deleteComment(Long commentId);

    List<CommentDto> getApprovedCommentsForEvent(Long eventId);


}

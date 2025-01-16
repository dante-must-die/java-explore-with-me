package ru.practicum.comment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.mapper.CommentMapper;
import ru.practicum.comment.model.Comment;
import ru.practicum.comment.repository.CommentRepository;
import ru.practicum.event.enum_.EventState;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exceptions.ConflictException;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    // ====================== PRIVATE (пользователи) ======================

    @Override
    public CommentDto addComment(Long userId, Long eventId, NewCommentDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User " + userId + " not found"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event " + eventId + " not found"));

        // 1) Проверяем статус события
        if (event.getState() != EventState.PUBLISHED) {
            throw new ConflictException("Cannot add comment to not PUBLISHED event");
        }
        // 2) Если positive=true, проверяем, нет ли уже положительного комментария
        if (Boolean.TRUE.equals(dto.getPositive())) {
            boolean exists = commentRepository.existsByUser_IdAndEvent_IdAndPositiveTrue(userId, eventId);
            if (exists) {
                throw new ConflictException("User " + userId + " already left a positive comment for event " + eventId);
            }
        }

        // Создаём сущность
        Comment comment = CommentMapper.toComment(dto);
        comment.setUser(user);
        comment.setEvent(event);
        comment.setCreatedOn(LocalDateTime.now());
        comment.setApproved(false);
        comment.setLastModify(LocalDateTime.now()); // При первом создании lastModify = createdOn

        // Сохраняем
        Comment saved = commentRepository.save(comment);
        return CommentMapper.toCommentDto(saved);
    }

    @Override
    public CommentDto updateCommentByUser(Long userId, Long commentId, NewCommentDto dto) {
        Comment c = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment " + commentId + " not found"));

        // Проверяем владельца
        if (!c.getUser().getId().equals(userId)) {
            throw new ConflictException("User " + userId + " not owner of comment " + commentId);
        }

        // Меняем текст (если пустой → 400)
        if (dto.getText() == null || dto.getText().isBlank()) {
            throw new ConflictException("Text cannot be empty");
        }
        c.setContent(dto.getText());

        // Меняем positive
        if (dto.getPositive() != null) {
            if (!c.getPositive() && dto.getPositive()) {
                // Текущий коммент был negative, теперь станет positive
                boolean exists = commentRepository.existsByUser_IdAndEvent_IdAndPositiveTrue(
                        c.getUser().getId(), c.getEvent().getId()
                );
                if (exists) {
                    throw new ConflictException("Cannot set positive because user already has a positive comment");
                }
            }
            c.setPositive(dto.getPositive());
        }

        // lastModify
        c.setLastModify(LocalDateTime.now());



        Comment updated = commentRepository.save(c);
        return CommentMapper.toCommentDto(updated);
    }

    @Override
    public void deleteCommentByUser(Long userId, Long commentId) {
        Comment c = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment " + commentId + " not found"));

        if (!c.getUser().getId().equals(userId)) {
            throw new ConflictException("User " + userId + " is not owner of comment " + commentId);
        }

        commentRepository.delete(c);
    }

    @Override
    public List<CommentDto> getUserComments(Long userId) {
        // Все комментарии userId
        List<Comment> list = commentRepository.findByUser_Id(userId);
        return list.stream().map(CommentMapper::toCommentDto).collect(Collectors.toList());
    }

    @Override
    public List<CommentDto> getUserCommentsForEvent(Long userId, Long eventId) {
        List<Comment> list = commentRepository.findByUser_IdAndEvent_Id(userId, eventId);
        return list.stream().map(CommentMapper::toCommentDto).collect(Collectors.toList());
    }

    // ====================== ADMIN ======================

    @Override
    public CommentDto approveComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment " + commentId + " not found"));

        comment.setApproved(true);
        Comment saved = commentRepository.save(comment);
        return CommentMapper.toCommentDto(saved);
    }

    @Override
    public void deleteComment(Long commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new NotFoundException("Comment " + commentId + " not found");
        }
        commentRepository.deleteById(commentId);
    }

    // ====================== PUBLIC ======================

    @Override
    public List<CommentDto> getApprovedCommentsForEvent(Long eventId) {
        List<Comment> comments = commentRepository.findByEvent_IdAndApprovedTrue(eventId);
        return comments.stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }
}

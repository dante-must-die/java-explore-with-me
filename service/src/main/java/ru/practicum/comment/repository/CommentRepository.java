package ru.practicum.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.comment.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    // Все одобренные комментарии к событию
    List<Comment> findByEvent_IdAndApprovedTrue(Long eventId);

    // Все комментарии конкретного пользователя
    List<Comment> findByUser_Id(Long userId);

    // Все комментарии конкретного пользователя к конкретному событию
    List<Comment> findByUser_IdAndEvent_Id(Long userId, Long eventId);

    // Проверка, есть ли уже positive коммент у данного user/event
    boolean existsByUser_IdAndEvent_IdAndPositiveTrue(Long userId, Long eventId);
}

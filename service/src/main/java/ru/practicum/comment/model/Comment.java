package ru.practicum.comment.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.event.model.Event;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // какому событию
    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    // автор
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // текст комментария
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    // время создания
    @Column(name = "created_on", nullable = false)
    private LocalDateTime createdOn;

    // одобрен ли администратором
    @Column(name = "approved", nullable = false)
    private Boolean approved = false;

    // положительный/отрицательный (true/false)
    @Column(name = "positive", nullable = false)
    private Boolean positive = false;

    // время последнего обновления (может быть null, если не редактировался)
    @Column(name = "last_modify")
    private LocalDateTime lastModify;
}

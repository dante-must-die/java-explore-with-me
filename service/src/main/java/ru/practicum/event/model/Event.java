package ru.practicum.event.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.category.model.Category;
import ru.practicum.event.enum_.EventState;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;

/**
 * Таблица "events" (в соответствии с вашей SQL-схемой).
 * lat/lon — обязательны (NOT NULL).
 */
@Entity
@Table(name = "events")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Краткое описание (2000 символов макс).
     */
    @Column(length = 2000, nullable = false)
    private String annotation;

    /**
     * Категория (не nullable), внешний ключ на categories.
     */
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    /**
     * Сколько уже подтвержденных заявок на участие.
     */
    @Column(name = "confirmed_requests")
    private Long confirmedRequests;

    /**
     * Дата/время создания события.
     */
    @Column(name = "created_on", nullable = false)
    private LocalDateTime createdOn;

    /**
     * Полное описание (макс 7000 символов).
     */
    @Column(length = 7000, nullable = false)
    private String description;

    /**
     * Дата/время, когда событие должно произойти.
     * (не раньше, чем сейчас+2ч при создании).
     */
    @Column(name = "event_date", nullable = false)
    private LocalDateTime eventDate;

    /**
     * Инициатор события (пользователь).
     */
    @ManyToOne
    @JoinColumn(name = "initiator_id", nullable = false)
    private User initiator;

    /**
     * Координата широты (NOT NULL).
     */
    @Column(nullable = false)
    private Float lat;

    /**
     * Координата долготы (NOT NULL).
     */
    @Column(nullable = false)
    private Float lon;

    /**
     * Нужно ли оплачивать участие (NOT NULL).
     */
    @Column(nullable = false)
    private Boolean paid;

    /**
     * Лимит участников. Значение 0 => безлимит.
     */
    @Column(name = "participant_limit")
    private Long participantLimit;

    /**
     * Дата/время публикации (когда событие перешло в state=PUBLISHED).
     */
    @Column(name = "published_on")
    private LocalDateTime publishedOn;

    /**
     * Нужно ли подтверждать заявки на участие.
     */
    @Column(name = "request_moderation")
    private Boolean requestModeration;

    /**
     * Текущий статус (PENDING, PUBLISHED, CANCELED).
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private EventState state;

    /**
     * Заголовок (макс 120 символов).
     */
    @Column(length = 120, nullable = false)
    private String title;

    /**
     * Счетчик просмотров.
     */
    private Long views;
}

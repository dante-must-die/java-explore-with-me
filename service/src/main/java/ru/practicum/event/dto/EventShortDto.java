package ru.practicum.event.dto;

import lombok.*;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.user.dto.UserShortDto;

/**
 * Короткая информация о событии (для списков).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventShortDto {

    private Long id;
    private String annotation;
    private CategoryDto category;
    private Long confirmedRequests;
    private String eventDate;       // строка "yyyy-MM-dd HH:mm:ss"
    private UserShortDto initiator;
    private Boolean paid;
    private String title;
    private Long views;
}

package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import org.hibernate.validator.constraints.Length;

/**
 * DTO для создания нового события (через Private API).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NewEventDto {

    /**
     * Краткое описание (20..2000 символов).
     */
    @NotBlank
    @Length(min = 20, max = 2000)
    private String annotation;

    /**
     * Идентификатор категории.
     */
    @NotNull
    private Long category;

    /**
     * Полное описание (20..7000 символов).
     */
    @NotBlank
    @Length(min = 20, max = 7000)
    private String description;

    /**
     * Дата/время проведения события. Формат "yyyy-MM-dd HH:mm:ss".
     * Должна быть >= (сейчас + 2ч).
     */
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private java.time.LocalDateTime eventDate;

    /**
     * Обязательные координаты (lat/lon).
     */
    @NotNull
    private LocationDto location;

    /**
     * Нужно ли оплачивать участие (по умолчанию false).
     */
    private Boolean paid = false;

    /**
     * Лимит участников (0 => безлимит).
     */
    @PositiveOrZero(message = "Ограничение на количество участников не может быть отрицательным числом")
    private Long participantLimit = 0L;

    /**
     * Нужно ли подтверждать заявки (по умолчанию true).
     */
    private Boolean requestModeration = true;

    /**
     * Заголовок (3..120 символов).
     */
    @NotBlank
    @Length(min = 3, max = 120)
    private String title;
}

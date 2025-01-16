package ru.practicum.comment.dto;

import lombok.*;
import ru.practicum.user.dto.UserShortDto;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {

    private Long id;
    private Long event;          // ID события
    private UserShortDto author; // информация об авторе
    private String text;         // текст комментария
    private LocalDateTime created;       // время создания
    private Boolean positive;    // положительный/отрицательный
    private LocalDateTime lastModify;    // время последнего изменения (если было)
    private Boolean approved;    // одобрен?
}

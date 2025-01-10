package ru.practicum.comment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NewCommentDto {

    @NotBlank
    private String text;      // Текст комментария (ранее "content")

    private Boolean positive;  // true/false (может быть null, будем считать default=false)
}

package ru.practicum.request.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ParticipationRequestDto {
    private Long id;
    private String status;   // PENDING / CONFIRMED / REJECTED / CANCELED
    private Long event;
    private Long requester;
    private String created;  // строка "yyyy-MM-dd HH:mm:ss"
}

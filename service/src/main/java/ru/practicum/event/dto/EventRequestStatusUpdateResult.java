package ru.practicum.event.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.request.dto.ParticipationRequestDto;

import java.util.List;

/**
 * Результат подтверждения/отклонения заявок.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventRequestStatusUpdateResult {
    /**
     * Подтверждённые заявки.
     */
    private List<ParticipationRequestDto> confirmedRequests;

    /**
     * Отклонённые заявки.
     */
    private List<ParticipationRequestDto> rejectedRequests;
}


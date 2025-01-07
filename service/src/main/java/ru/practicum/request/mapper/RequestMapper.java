package ru.practicum.request.mapper;

import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.model.Request;

import java.time.format.DateTimeFormatter;

public class RequestMapper {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private RequestMapper() {}

    public static ParticipationRequestDto toDto(Request r) {
        return new ParticipationRequestDto(
                r.getId(),
                r.getStatus(),
                r.getEvent().getId(),
                r.getRequester().getId(),
                r.getCreated().format(FMT)
        );
    }
}

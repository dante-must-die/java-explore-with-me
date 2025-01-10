package ru.practicum.event.mapper;

import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.LocationDto;
import ru.practicum.event.model.Event;
import ru.practicum.user.mapper.UserMapper;

import java.time.format.DateTimeFormatter;

/**
 * Преобразование Event <-> DTO.
 */
public class EventMapper {

    private EventMapper() {
    }

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Event -> EventFullDto
     */
    public static EventFullDto toEventFullDto(Event e) {
        EventFullDto dto = new EventFullDto();
        dto.setId(e.getId());
        dto.setAnnotation(e.getAnnotation());
        dto.setCategory(CategoryMapper.toCategoryDto(e.getCategory()));
        dto.setConfirmedRequests(
                e.getConfirmedRequests() == null ? 0 : e.getConfirmedRequests()
        );
        dto.setCreatedOn(e.getCreatedOn());
        dto.setDescription(e.getDescription());
        dto.setEventDate(e.getEventDate());
        dto.setInitiator(UserMapper.toUserShortDto(e.getInitiator()));
        dto.setLocation(new LocationDto(e.getLat(), e.getLon()));
        dto.setPaid(e.getPaid());
        dto.setParticipantLimit(
                e.getParticipantLimit() == null ? 0L : e.getParticipantLimit()
        );
        dto.setPublishedOn(e.getPublishedOn());
        dto.setRequestModeration(e.getRequestModeration());
        dto.setState(e.getState().name());
        dto.setTitle(e.getTitle());
        dto.setViews(e.getViews() == null ? 0 : e.getViews());
        return dto;
    }

    /**
     * Event -> EventShortDto
     */
    public static EventShortDto toEventShortDto(Event e) {
        EventShortDto dto = new EventShortDto();
        dto.setId(e.getId());
        dto.setAnnotation(e.getAnnotation());
        dto.setCategory(CategoryMapper.toCategoryDto(e.getCategory()));
        dto.setConfirmedRequests(
                e.getConfirmedRequests() == null ? 0 : e.getConfirmedRequests()
        );
        if (e.getEventDate() != null) {
            dto.setEventDate(e.getEventDate().format(FMT));
        }
        dto.setInitiator(UserMapper.toUserShortDto(e.getInitiator()));
        dto.setPaid(e.getPaid());
        dto.setTitle(e.getTitle());
        dto.setViews(e.getViews() == null ? 0 : e.getViews());
        return dto;
    }
}

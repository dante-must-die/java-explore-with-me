package ru.practicum.event.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.*;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.event.service.EventService;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.model.Request;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.request.service.RequestService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
public class PrivateEventController {

    private final EventService eventService;
    private final RequestService requestService;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;

    /**
     * Получение (список) событий текущего пользователя.
     */
    @GetMapping
    public List<EventShortDto> getUserEvents(@PathVariable Long userId,
                                             @RequestParam(defaultValue = "0") int from,
                                             @RequestParam(defaultValue = "10") int size) {
        return eventService.getUserEvents(userId, from, size);
    }

    /**
     * Создание нового события.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto addEvent(@PathVariable Long userId,
                                 @Valid @RequestBody NewEventDto dto) {
        return eventService.addEvent(userId, dto);
    }

    /**
     * Получить полную инфу о событии текущего пользователя.
     */
    @GetMapping("/{eventId}")
    public EventFullDto getUserEvent(@PathVariable Long userId,
                                     @PathVariable Long eventId) {
        return eventService.getUserEvent(userId, eventId);
    }

    /**
     * Изменение события (PENDING/CANCELED).
     */
    @PatchMapping("/{eventId}")
    public EventFullDto updateUserEvent(@PathVariable Long userId,
                                        @PathVariable Long eventId,
                                        @Validated @RequestBody UpdateEventUserRequest updateRequest) {
        Float lat = (updateRequest.getLocation() != null) ? updateRequest.getLocation().getLat() : null;
        Float lon = (updateRequest.getLocation() != null) ? updateRequest.getLocation().getLon() : null;

        return eventService.updateUserEvent(
                userId,
                eventId,
                updateRequest.getAnnotation(),
                updateRequest.getCategory(),
                updateRequest.getDescription(),
                updateRequest.getEventDate(),
                lat,
                lon,
                updateRequest.getPaid(),
                updateRequest.getParticipantLimit(),
                updateRequest.getRequestModeration(),
                updateRequest.getStateAction(),
                updateRequest.getTitle()
        );
    }

    /**
     * Изменение статуса заявок на участие в событии текущего пользователя.
     * PATCH /users/{userId}/events/{eventId}/requests
     */
    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult changeRequestStatus(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @Validated @RequestBody EventRequestStatusUpdateRequest updateRequest
    ) {
        // Вызываем метод в RequestService:
        return requestService.changeRequestStatus(userId, eventId, updateRequest);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getRequestsInUserEvent(
            @PathVariable Long userId,
            @PathVariable Long eventId
    ) {
        // 1) проверить, что eventId существует + принадлежит userId
        Event e = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("..."));
        if (!e.getInitiator().getId().equals(userId)) {
            throw new NotFoundException("Event is not owned by user");
        }
        // 2) вернуть список заявок
        List<Request> requests = requestRepository.findByEvent_Id(eventId);
        return requests.stream().map(RequestMapper::toDto).collect(Collectors.toList());
    }
}

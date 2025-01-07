package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.UpdateEventAdminRequest;
import ru.practicum.event.service.EventService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
public class AdminEventController {

    private final EventService eventService;

    // GET /admin/events
    @GetMapping
    public List<EventFullDto> getEvents(@RequestParam(required = false) List<Long> users,
                                        @RequestParam(required = false) List<String> states,
                                        @RequestParam(required = false) List<Long> categories,
                                        @RequestParam(required = false) LocalDateTime rangeStart,
                                        @RequestParam(required = false) LocalDateTime rangeEnd,
                                        @RequestParam(defaultValue = "0") int from,
                                        @RequestParam(defaultValue = "10") int size) {
        return eventService.adminSearchEvents(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    // PATCH /admin/events/{eventId}
    @PatchMapping("/{eventId}")
    public EventFullDto updateEvent(@PathVariable Long eventId,
                                    @Validated @RequestBody UpdateEventAdminRequest updateRequest)
    {
        return eventService.adminUpdateEvent(
                eventId,
                updateRequest.getAnnotation(),
                updateRequest.getCategory(),
                updateRequest.getDescription(),
                updateRequest.getEventDate(),
                (updateRequest.getLocation() != null ? updateRequest.getLocation().getLat() : null),
                (updateRequest.getLocation() != null ? updateRequest.getLocation().getLon() : null),
                updateRequest.getPaid(),
                updateRequest.getParticipantLimit(),
                updateRequest.getRequestModeration(),
                updateRequest.getStateAction(),
                updateRequest.getTitle()
        );
    }
}

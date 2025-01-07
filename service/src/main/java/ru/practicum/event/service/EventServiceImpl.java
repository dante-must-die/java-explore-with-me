package ru.practicum.event.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.client.StatsClient;
import ru.practicum.EndpointHit;
import ru.practicum.ViewStats;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.enum_.EventState;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exceptions.BadRequestException;
import ru.practicum.exceptions.ConflictException;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    private final StatsClient statsClient;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // ------------------ ADMIN ------------------

    @Override
    public List<EventFullDto> adminSearchEvents(List<Long> users,
                                                List<String> states,
                                                List<Long> categories,
                                                LocalDateTime rangeStart,
                                                LocalDateTime rangeEnd,
                                                int from,
                                                int size) {

        List<Event> all = eventRepository.findAll();

        if (users != null && !users.isEmpty()) {
            all = all.stream()
                    .filter(e -> users.contains(e.getInitiator().getId()))
                    .collect(Collectors.toList());
        }
        if (states != null && !states.isEmpty()) {
            all = all.stream()
                    .filter(e -> states.contains(e.getState().name()))
                    .collect(Collectors.toList());
        }
        if (categories != null && !categories.isEmpty()) {
            all = all.stream()
                    .filter(e -> categories.contains(e.getCategory().getId()))
                    .collect(Collectors.toList());
        }
        if (rangeStart != null) {
            all = all.stream()
                    .filter(e -> !e.getEventDate().isBefore(rangeStart))
                    .collect(Collectors.toList());
        }
        if (rangeEnd != null) {
            all = all.stream()
                    .filter(e -> !e.getEventDate().isAfter(rangeEnd))
                    .collect(Collectors.toList());
        }


        int startIdx = from;
        int endIdx = Math.min(startIdx + size, all.size());
        if (startIdx >= all.size()) {
            return Collections.emptyList();
        }

        return all.subList(startIdx, endIdx).stream()
                .map(EventMapper::toEventFullDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto adminUpdateEvent(Long eventId,
                                         String annotation,
                                         Long categoryId,
                                         String description,
                                         LocalDateTime eventDate,
                                         Float lat,
                                         Float lon,
                                         Boolean paid,
                                         Long participantLimit,
                                         Boolean requestModeration,
                                         String stateAction, // "PUBLISH_EVENT" / "REJECT_EVENT"
                                         String title) {
        Event e = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event " + eventId + " not found"));

        if (eventDate != null) {
            LocalDateTime mustBeAfter = LocalDateTime.now().plusHours(1);
            if (eventDate.isBefore(mustBeAfter)) {
                throw new BadRequestException(
                        "Event date must be at least 1 hour after now for admin update"
                );
            }
        }

        // Изменяем поля, если они не null:
        if (annotation != null) {
            e.setAnnotation(annotation);
        }
        if (categoryId != null) {
            Category cat = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new NotFoundException("Category " + categoryId + " not found"));
            e.setCategory(cat);
        }
        if (description != null) {
            e.setDescription(description);
        }
        if (eventDate != null) {
            // Дата начала не ранее чем за час от даты публикации
            if (eventDate.isBefore(LocalDateTime.now().plusHours(1))) {
                throw new BadRequestException("Event date must be at least 1 hour after now for admin update");
            }
            e.setEventDate(eventDate);
        }
        if (lat != null) {
            e.setLat(lat);
        }
        if (lon != null) {
            e.setLon(lon);
        }
        if (paid != null) {
            e.setPaid(paid);
        }
        if (participantLimit != null) {
            if (participantLimit < 0) {
                throw new BadRequestException("participantLimit cannot be negative");
            }
            e.setParticipantLimit(participantLimit);
        }
        if (requestModeration != null) {
            e.setRequestModeration(requestModeration);
        }
        if (title != null) {
            e.setTitle(title);
        }

        // stateAction
        if (stateAction != null) {
            switch (stateAction) {
                case "PUBLISH_EVENT":
                    // публиковать можно только если PENDING
                    if (e.getState() != EventState.PENDING) {
                        throw new ConflictException("Cannot publish event in state " + e.getState());
                    }
                    e.setState(EventState.PUBLISHED);
                    e.setPublishedOn(LocalDateTime.now());
                    break;
                case "REJECT_EVENT":
                    // отклонять можно, если не PUBLISHED
                    if (e.getState() == EventState.PUBLISHED) {
                        throw new ConflictException("Cannot reject already published event");
                    }
                    e.setState(EventState.CANCELED);
                    break;
                default:
                    // игнорируем
                    break;
            }
        }

        Event saved = eventRepository.save(e);
        return EventMapper.toEventFullDto(saved);
    }

    // ------------------ PUBLIC ------------------

    @Override
    public List<EventShortDto> publicGetEvents(String text,
                                               List<Long> categories,
                                               Boolean paid,
                                               LocalDateTime rangeStart,
                                               LocalDateTime rangeEnd,
                                               Boolean onlyAvailable,
                                               String sort,
                                               int from,
                                               int size) {
        // Возвращаем только PUBLISHED
        List<Event> all = eventRepository.findAll().stream()
                .filter(e -> e.getState() == EventState.PUBLISHED)
                .collect(Collectors.toList());

        // фильтр text
        if (text != null && !text.isBlank()) {
            String lower = text.toLowerCase();
            all = all.stream()
                    .filter(e -> e.getAnnotation().toLowerCase().contains(lower)
                            || e.getDescription().toLowerCase().contains(lower))
                    .collect(Collectors.toList());
        }

        // фильтр категорий
        if (categories != null && !categories.isEmpty()) {
            all = all.stream()
                    .filter(e -> categories.contains(e.getCategory().getId()))
                    .collect(Collectors.toList());
        }

        // фильтр платности
        if (paid != null) {
            all = all.stream().filter(e -> e.getPaid().equals(paid)).collect(Collectors.toList());
        }

        // фильтр по времени
        LocalDateTime now = LocalDateTime.now();
        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new BadRequestException("rangeStart cannot be after rangeEnd");
        }
        if (rangeStart == null && rangeEnd == null) {
            // берем события после "сейчас"
            all = all.stream()
                    .filter(e -> e.getEventDate().isAfter(now))
                    .collect(Collectors.toList());
        } else {
            if (rangeStart != null) {
                all = all.stream()
                        .filter(e -> !e.getEventDate().isBefore(rangeStart))
                        .collect(Collectors.toList());
            }
            if (rangeEnd != null) {
                all = all.stream()
                        .filter(e -> !e.getEventDate().isAfter(rangeEnd))
                        .collect(Collectors.toList());
            }
        }

        // onlyAvailable => учитываем лимит
        if (Boolean.TRUE.equals(onlyAvailable)) {
            all = all.stream()
                    .filter(e -> {
                        long limit = (e.getParticipantLimit() == null) ? 0 : e.getParticipantLimit();
                        long confirmed = (e.getConfirmedRequests() == null) ? 0 : e.getConfirmedRequests();
                        return (limit == 0) || (confirmed < limit);
                    })
                    .collect(Collectors.toList());
        }

        // сортировка
        if (sort != null) {
            switch (sort) {
                case "EVENT_DATE":
                    all.sort((o1, o2) -> o1.getEventDate().compareTo(o2.getEventDate()));
                    break;
                case "VIEWS":
                    all.sort((o1, o2) -> {
                        long v1 = (o1.getViews() == null ? 0 : o1.getViews());
                        long v2 = (o2.getViews() == null ? 0 : o2.getViews());
                        return Long.compare(v1, v2);
                    });
                    break;
                default:
                    // нет сортировки
                    break;
            }
        }

        // пагинация
        int startIdx = from;
        int endIdx = Math.min(startIdx + size, all.size());
        if (startIdx >= all.size()) {
            return Collections.emptyList();
        }

        return all.subList(startIdx, endIdx).stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto publicGetEvent(Long id, HttpServletRequest request) {
        // 1) Находим событие
        Event e = eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Event " + id + " not found"));

        // 2) Проверяем статус
        if (e.getState() != EventState.PUBLISHED) {
            throw new NotFoundException("Event " + id + " is not published");
        }

        // 3) Отправляем "hit" в сервис статистики
        sendHitToStats(e, request);

        // 4) Теперь получаем уникальное количество IP из сервиса статистики
        long uniqueViews = getUniqueViews(e);

        // 5) Записываем это число во "views"
        e.setViews(uniqueViews);
        eventRepository.save(e); // Сохраняем изменения

        // 6) Возвращаем EventFullDto
        return EventMapper.toEventFullDto(e);
    }


    private void sendHitToStats(Event e, HttpServletRequest request) {
        try {
            EndpointHit hit = new EndpointHit();
            hit.setApp("ewm-service");  // название нашего приложения
            hit.setIp(request.getRemoteAddr()); // IP клиента
            hit.setUri(request.getRequestURI());
            hit.setTimestamp(LocalDateTime.now().format(FORMATTER)); // Используем правильный формат

            statsClient.hit(hit);
        } catch (Exception ex) {
            // Логируем, но не ломаем запрос:
            System.err.println("sendHitToStats error: " + ex.getMessage());
        }
    }


    // ------------------ PRIVATE ------------------

    @Override
    public List<EventShortDto> getUserEvents(Long userId, int from, int size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User " + userId + " not found"));

        List<Event> all = eventRepository.findAll().stream()
                .filter(e -> e.getInitiator().getId().equals(userId))
                .collect(Collectors.toList());

        int startIdx = from;
        int endIdx = Math.min(startIdx + size, all.size());
        if (startIdx >= all.size()) {
            return Collections.emptyList();
        }
        return all.subList(startIdx, endIdx).stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto addEvent(Long userId, NewEventDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User " + userId + " not found"));

        Category category = categoryRepository.findById(dto.getCategory())
                .orElseThrow(() -> new NotFoundException("Category " + dto.getCategory() + " not found"));

        LocalDateTime now = LocalDateTime.now();
        // дата не раньше, чем сейчас+2ч
        if (dto.getEventDate().isBefore(now.plusHours(2))) {
            throw new BadRequestException("Event date must be >= 2 hours from now");
        }

        // Создаем Event
        Event e = new Event();
        e.setAnnotation(dto.getAnnotation());
        e.setCategory(category);
        e.setConfirmedRequests(0L);
        e.setCreatedOn(now);
        e.setDescription(dto.getDescription());
        e.setEventDate(dto.getEventDate());
        e.setInitiator(user);
        e.setLat(dto.getLocation().getLat());
        e.setLon(dto.getLocation().getLon());
        e.setPaid(dto.getPaid() == null ? false : dto.getPaid());
        if (dto.getParticipantLimit() != null && dto.getParticipantLimit() < 0) {
            throw new BadRequestException("participantLimit cannot be negative");
        } else {
            e.setParticipantLimit(dto.getParticipantLimit());
        }

        e.setPublishedOn(null);
        e.setRequestModeration(dto.getRequestModeration() == null ? true : dto.getRequestModeration());
        e.setState(EventState.PENDING);
        e.setTitle(dto.getTitle());
        e.setViews(0L);

        Event saved = eventRepository.save(e);
        return EventMapper.toEventFullDto(saved);
    }

    @Override
    public EventFullDto getUserEvent(Long userId, Long eventId) {
        Event e = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event " + eventId + " not found"));
        if (!e.getInitiator().getId().equals(userId)) {
            throw new NotFoundException("Event " + eventId + " is not owned by user " + userId);
        }
        return EventMapper.toEventFullDto(e);
    }

    @Override
    public EventFullDto updateUserEvent(Long userId,
                                        Long eventId,
                                        String annotation,
                                        Long categoryId,
                                        String description,
                                        LocalDateTime eventDate,
                                        Float lat,
                                        Float lon,
                                        Boolean paid,
                                        Long participantLimit,
                                        Boolean requestModeration,
                                        String stateAction,
                                        String title) {
        Event e = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event " + eventId + " not found"));

        if (!e.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Event " + eventId + " is not owned by user " + userId);
        }
        // изменить можно только PENDING или CANCELED
        if (e.getState() != EventState.PENDING && e.getState() != EventState.CANCELED) {
            throw new ConflictException("Only PENDING or CANCELED events can be changed");
        }

        if (annotation != null) {
            e.setAnnotation(annotation);
        }
        if (categoryId != null) {
            Category cat = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new NotFoundException("Category " + categoryId + " not found"));
            e.setCategory(cat);
        }
        if (description != null) {
            e.setDescription(description);
        }
        if (eventDate != null) {
            // дата >= сейчас+2ч
            if (eventDate.isBefore(LocalDateTime.now().plusHours(2))) {
                throw new BadRequestException("Event date must be >= 2 hours from now");
            }
            e.setEventDate(eventDate);
        }
        if (lat != null) {
            e.setLat(lat);
        }
        if (lon != null) {
            e.setLon(lon);
        }
        if (paid != null) {
            e.setPaid(paid);
        }
        if (participantLimit != null) {
            if (participantLimit < 0) {
                throw new BadRequestException("participantLimit cannot be negative");
            }
            e.setParticipantLimit(participantLimit);
        }

        if (requestModeration != null) {
            e.setRequestModeration(requestModeration);
        }
        if (title != null) {
            e.setTitle(title);
        }

        // stateAction => SEND_TO_REVIEW / CANCEL_REVIEW
        if (stateAction != null) {
            switch (stateAction) {
                case "SEND_TO_REVIEW":
                    e.setState(EventState.PENDING);
                    break;
                case "CANCEL_REVIEW":
                    e.setState(EventState.CANCELED);
                    break;
                default:
                    // игнорируем
                    break;
            }
        }

        Event saved = eventRepository.save(e);
        return EventMapper.toEventFullDto(saved);
    }

    /**
     * Метод, который делает GET /stats?start=...&end=...&uris=...&unique=true
     * и возвращает количество уникальных IP
     */
    private long getUniqueViews(Event e) {
        // Берём начало интервала = publishedOn (или какое-то дефолтное)
        String start = String.valueOf(e.getPublishedOn());
        if (start == null) {
            // если почему-то событие published, но publishedOn == null
            start = String.valueOf(LocalDateTime.now().minusYears(10));
        }
        // конец интервала = "сейчас"
        String end = String.valueOf(LocalDateTime.now());

        // uri = "/events/{id}"
        String uri = "/events/" + e.getId();

        try {
            // Передаём LocalDateTime объекты
            List<ViewStats> stats = statsClient.getStats(start, end, List.of(uri), true);
            if (stats.isEmpty()) {
                return 0L;
            } else {
                return stats.get(0).getHits();
            }
        } catch (Exception ex) {
            System.err.println("getUniqueViews error: " + ex.getMessage());
            // Если статистика недоступна — вернём, напр., 0
            return 0L;
        }
    }
}

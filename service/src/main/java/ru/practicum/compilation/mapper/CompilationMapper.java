package ru.practicum.compilation.mapper;


import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.mapper.EventMapper;

import java.util.List;
import java.util.stream.Collectors;

public class CompilationMapper {
    private CompilationMapper() {}

    public static CompilationDto toCompilationDto(Compilation c) {
        List<EventShortDto> eventShorts = c.getEvents().stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());
        return new CompilationDto(c.getId(), eventShorts, c.isPinned(), c.getTitle());
    }
}

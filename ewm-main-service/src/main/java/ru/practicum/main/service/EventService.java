package ru.practicum.main.service;

import ru.practicum.main.dto.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    EventFullDto getPublishedEvent(Long eventId, HttpServletRequest request);

    List<EventShortDto> getPublishedEvents(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                           LocalDateTime rangeEnd, Boolean onlyAvailable, String sort, Integer from,
                                           Integer size, HttpServletRequest request);
}

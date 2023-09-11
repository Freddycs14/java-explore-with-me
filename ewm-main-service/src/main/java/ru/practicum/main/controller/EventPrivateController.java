package ru.practicum.main.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.dto.*;
import ru.practicum.main.service.EventPrivateService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users/{userId}/events")
@RequiredArgsConstructor
@Validated
public class EventPrivateController {
    private final EventPrivateService service;

    @GetMapping
    public List<EventFullDto> getUserEvents(@PathVariable Long userId,
                                            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                            @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Получение события с Id {}", userId);
        return service.getUserEvents(userId, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(@PathVariable Long userId, @Validated @RequestBody NewEventDto newEventDto) {
        log.info("Создание события пользователем {}", userId);
        return service.createEvent(userId, newEventDto);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEventOfUser(@PathVariable Long userId, @PathVariable Long eventId) {
        log.info("Получение полной информации о событии пользователем {}", userId);
        return service.getEventOfUser(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEventOfUser(@PathVariable Long userId, @PathVariable Long eventId,
                                          @Validated @RequestBody UpdateEventUserRequest updateEventUserRequest) {
        log.info("Обновление события с Id {}", eventId);
        return service.updateEventOfUser(userId, eventId, updateEventUserRequest);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getEventRequests(@PathVariable Long userId, @PathVariable Long eventId) {
        log.info("Получение информации об участии пользователя с Id {} в событии Id {}", userId, eventId);
        return service.getEventRequests(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult updateRequestStatus(@PathVariable Long userId, @PathVariable Long eventId,
                                                              @RequestBody EventRequestStatusUpdateRequest requests) {
        log.info("Обновление события пользователем Id {}", userId);
        return service.updateRequestStatus(userId, eventId, requests);
    }
}

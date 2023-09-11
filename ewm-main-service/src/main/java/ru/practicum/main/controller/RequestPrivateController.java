package ru.practicum.main.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.dto.ParticipationRequestDto;
import ru.practicum.main.service.RequestService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Validated
public class RequestPrivateController {
    private final RequestService service;

    @GetMapping(path = "/{userId}/requests")
    public List<ParticipationRequestDto> getUsersRequests(@PathVariable Long userId) {
        log.info("Получение информации об участниках события пользователем с id {}", userId);
        return service.getUsersRequests(userId);
    }

    @PostMapping(path = "/{userId}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto createRequest(@PathVariable Long userId,
                                                 @RequestParam(name = "eventId") String eventId) {
        log.info("Добавление на участие в событии id {}", eventId);
        return service.createRequest(userId, Long.parseLong(eventId));
    }

    @PatchMapping(path = "/{userId}/requests/{requestId}/cancel")
    public ParticipationRequestDto canceledRequest(@PathVariable Long userId,
                                                   @PathVariable Long requestId) {
        log.info("Отмена заявки на участие");
        return service.canceledRequest(userId, requestId);
    }
}

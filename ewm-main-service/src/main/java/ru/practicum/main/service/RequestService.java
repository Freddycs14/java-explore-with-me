package ru.practicum.main.service;

import ru.practicum.main.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestService {
    List<ParticipationRequestDto> getUsersRequests(Long userId);

    ParticipationRequestDto createRequest(Long userId, Long eventId);

    ParticipationRequestDto canceledRequest(Long userId, Long requestId);
}

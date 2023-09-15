package ru.practicum.main.mappers;

import lombok.experimental.UtilityClass;
import ru.practicum.main.dto.EventRequestStatusUpdateResult;
import ru.practicum.main.dto.ParticipationRequestDto;
import ru.practicum.main.model.Request;
import ru.practicum.main.model.RequestUpdate;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class RequestMapper {
    public ParticipationRequestDto toParticipationRequestDto(Request request) {
        return ParticipationRequestDto.builder()
                .id(request.getId())
                .created(request.getCreated())
                .event(request.getEvent().getId())
                .requester(request.getRequester().getId())
                .status(request.getStatus())
                .build();
    }

    public List<ParticipationRequestDto> toListParticipationRequestDto(List<Request> requests) {
        return requests.stream().map(RequestMapper::toParticipationRequestDto).collect(Collectors.toList());
    }

    public EventRequestStatusUpdateResult toEventRequestStatusUpdateResult(RequestUpdate requestUpdate) {
        return EventRequestStatusUpdateResult.builder()
                .rejectedRequests(RequestMapper.toListParticipationRequestDto(requestUpdate.getRequestCanceled()))
                .confirmedRequests(RequestMapper.toListParticipationRequestDto(requestUpdate.getRequestConfirmed()))
                .build();
    }
}

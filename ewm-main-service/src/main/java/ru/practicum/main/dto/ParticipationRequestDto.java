package ru.practicum.main.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.main.model.enums.RequestStatus;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ParticipationRequestDto {
    Long id;
    Long event;
    LocalDateTime created;
    Long requester;
    RequestStatus status;
}

package ru.practicum.main.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConfirmedRequestShort {
    Long eventId;
    Long confirmedRequestCount;

    public ConfirmedRequestShort(Long eventId, Long confirmedRequestCount) {
        this.eventId = eventId;
        this.confirmedRequestCount = confirmedRequestCount;
    }
}
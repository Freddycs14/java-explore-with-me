package ru.practicum.main.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.main.model.enums.RequestStatus;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RequestShort {
    List<Long> requestIds;
    RequestStatus status;
}

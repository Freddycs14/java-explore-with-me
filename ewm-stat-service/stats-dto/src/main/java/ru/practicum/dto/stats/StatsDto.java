package ru.practicum.dto.stats;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StatsDto {
    Long id;
    String app;
    String uri;
    String ip;
    String timestamp;
}

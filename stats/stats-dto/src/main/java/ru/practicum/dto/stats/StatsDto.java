package ru.practicum.dto.stats;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;


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

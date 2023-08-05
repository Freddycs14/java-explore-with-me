package ru.practicum.stats.model.mapper;

import ru.practicum.dto.stats.StatsDto;
import ru.practicum.stats.model.Stats;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class StatsMapper {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static Stats toStats(StatsDto hit) {
        return Stats.builder()
                .app(hit.getApp())
                .uri(hit.getUri())
                .ip(hit.getIp())
                .timestamp(LocalDateTime.parse(hit.getTimestamp(), formatter))
                .build();
    }

    public static StatsDto toStatsDto(Stats stats) {
        return StatsDto.builder()
                .id(stats.getId())
                .app(stats.getApp())
                .ip(stats.getIp())
                .uri(stats.getUri())
                .timestamp(stats.getTimestamp().format(formatter))
                .build();
    }
}

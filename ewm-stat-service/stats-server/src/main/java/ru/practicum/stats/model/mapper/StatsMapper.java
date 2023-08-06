package ru.practicum.stats.model.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.stats.StatsDto;
import ru.practicum.stats.model.Stats;

@UtilityClass
public class StatsMapper {
    public Stats toStats(StatsDto hit) {
        return Stats.builder()
                .app(hit.getApp())
                .uri(hit.getUri())
                .ip(hit.getIp())
                .timestamp(hit.getTimestamp())
                .build();
    }

    public StatsDto toStatsDto(Stats stats) {
        return StatsDto.builder()
                .id(stats.getId())
                .app(stats.getApp())
                .ip(stats.getIp())
                .uri(stats.getUri())
                .timestamp(stats.getTimestamp())
                .build();
    }
}

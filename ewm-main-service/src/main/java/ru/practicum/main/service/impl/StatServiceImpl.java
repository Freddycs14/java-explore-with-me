package ru.practicum.main.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.stats.StatsClient;
import ru.practicum.dto.stats.StatsDto;
import ru.practicum.dto.stats.ViewStats;
import ru.practicum.main.exception.ConflictException;
import ru.practicum.main.model.ConfirmedRequestShort;
import ru.practicum.main.model.Event;
import ru.practicum.main.repository.RequestRepository;
import ru.practicum.main.service.StatService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatServiceImpl implements StatService {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final RequestRepository requestRepository;
    private final StatsClient statsClient;
    private final ObjectMapper objectMapper;
    @Value("${main_app}")
    private String app;

    @Override
    public Map<Long, Long> toConfirmedRequest(Collection<Event> list) {
        List<Long> listEventId = list.stream()
                .map(a -> a.getId())
                .collect(Collectors.toList());
        List<ConfirmedRequestShort> confirmedRequestShorts = requestRepository.countByEvent(listEventId);
        Map<Long, Long> mapConRequest = confirmedRequestShorts.stream()
                .collect(Collectors.toMap(ConfirmedRequestShort::getEventId, ConfirmedRequestShort::getConfirmedRequestCount));
        return mapConRequest;
    }

    @Override
    public Map<Long, Long> toView(Collection<Event> list) {
        Map<Long, Long> mapView = new HashMap<>();
        LocalDateTime start = list.stream()
                .map(a -> a.getCreatedOn())
                .min(LocalDateTime::compareTo).orElse(null);
        if (start == null) {
            return Map.of();
        }
        List<String> uris = list.stream()
                .map(a -> "/events/" + a.getId())
                .collect(Collectors.toList());
        ResponseEntity<Object> response = statsClient.getStats(start.format(FORMATTER), LocalDateTime.now().format(FORMATTER), uris, true);
        try {
            List<ViewStats> stat = Arrays.asList(objectMapper.readValue(objectMapper.writeValueAsString(response.getBody()), ViewStats[].class));
            stat.forEach(statistic -> mapView.put(
                    Long.parseLong(statistic.getUri().replaceAll("[\\D]+", "")), statistic.getHits()));
        } catch (JsonProcessingException e) {
            throw new ConflictException("Произошла ошибка выполнения запроса статистики");
        }
        return mapView;
    }

    @Override
    public void addHits(HttpServletRequest request) {
        StatsDto statsDto = StatsDto.builder()
                .ip(request.getRemoteAddr())
                .uri(request.getRequestURI())
                .app(app)
                .timestamp(LocalDateTime.now())
                .build();
        statsClient.postHit(statsDto);
    }
}

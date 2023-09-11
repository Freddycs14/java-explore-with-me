package ru.practicum.client.stats;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import ru.practicum.client.stats.client.BaseClient;
import ru.practicum.dto.stats.StatsDto;

@Service
public class StatsClient extends BaseClient {

    @Autowired
    public StatsClient(@Value("${stats-service.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> getStats(String start, String end, @Nullable List<String> uris, boolean unique) {
        Map<String, Object> parameters;
        if (uris == null) {
            parameters = Map.of("start", encodeValue(start),
                    "end", encodeValue(end),
                    "unique", unique);
            return get("/stats?start={start}&end={end}&unique={unique}", parameters);
        } else {
            parameters = Map.of("start", start,
                    "end", end,
                    "uris", String.join(",", uris),
                    "unique", unique);
            return get("/stats?start={start}&end={end}&uris={uris}&unique={unique}", parameters);
        }
    }

    public ResponseEntity<Object> postHit(StatsDto hit) {
        return post("/hit", hit);
    }

    private String encodeValue(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}

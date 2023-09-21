package ru.practicum.main.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.main.dto.CommentDto;
import ru.practicum.main.service.CommentService;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
public class CommentPublicController {
    private final CommentService service;

    @GetMapping("/comment/{comId}")
    public CommentDto getComment(@PathVariable Long comId) {
        log.info("Получение комментария по id");
        return service.getComment(comId);
    }
}
